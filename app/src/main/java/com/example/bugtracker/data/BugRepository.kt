package com.example.bugtracker.data

import android.content.Context
import com.example.bugtracker.data.domain.Bug
import com.example.bugtracker.data.local.dao.BugDao
import com.example.bugtracker.data.local.models.DatabaseBug
import com.example.bugtracker.data.local.models.asDomainModel
import com.example.bugtracker.data.network.datasource.BugDataSource
import com.example.bugtracker.data.network.models.NetworkBug
import com.example.bugtracker.data.network.models.asDatabaseModel
import com.example.bugtracker.data.network.models.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BugRepository(
    private val bugDao: BugDao,
    private val bugDataSource: BugDataSource,
    private val context: Context
) : DBSync<DatabaseBug, NetworkBug, Bug>(bugDao::deleteAll, bugDao::addAll, bugDataSource, context) {
    companion object {
        @Volatile
        private var instance: BugRepository? = null

        fun getInstance(bugDao: BugDao, bugDataSource: BugDataSource, context: Context) =
            instance ?: synchronized(this) {
                instance ?: BugRepository(bugDao, bugDataSource, context).also { instance = it }
            }
    }

    // can also handle where only retrieves from dao, and refreshes on opening the app
    suspend fun getAllBugs(): List<Bug> = withContext(Dispatchers.IO) {
        var bugs: List<Bug> = bugDao.getAll().asDomainModel()

        if (bugs.isEmpty()) {
            val networkBugs = bugDataSource.getAll();
            bugs = networkBugs.asDomainModel();
            bugDao.addAll(networkBugs.asDatabaseModel());
        }

        bugs
    }

    // only query dao instead of checking network? Do I want to leave refreshing only up to the caller?
    suspend fun getBug(bugID: Int): Bug? = withContext(Dispatchers.IO) {
        var bug: Bug? = null

        val daoBug = bugDao.get(bugID)

        if (daoBug == null) {
            val networkBug = bugDataSource.get(bugID)
            if(networkBug != null) {
                bug = networkBug.asDomainModel()
            }
        } else {
            bug = daoBug.asDomainModel()
        }

        bug
    }

    // sync with the network database on refresh
    // first upload local to remote, then grab remote
    suspend fun addBug(bug: Bug) = withContext(Dispatchers.IO) {
        bugDao.add(bug.asDatabaseModel())
        bugDataSource.add(bug.asNetworkModel())
    }

    suspend fun updateBug(bug: Bug) = withContext(Dispatchers.IO) {
        bugDao.add(bug.asDatabaseModel())
        bugDataSource.update(bug.bugID, bug.asNetworkModel())
    }

    suspend fun deleteBug(bugID: Int) = withContext(Dispatchers.IO) {
        bugDao.delete(bugID)
        bugDataSource.delete(bugID)
    }

    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        bugDao.deleteAll()
    }
}