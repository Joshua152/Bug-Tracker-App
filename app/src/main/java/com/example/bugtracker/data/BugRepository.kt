package com.example.bugtracker.data

import com.example.bugtracker.data.domain.Bug
import com.example.bugtracker.data.local.dao.BugDao
import com.example.bugtracker.data.local.models.asDomainModel
import com.example.bugtracker.data.network.datasource.BugDataSource
import com.example.bugtracker.data.network.models.NetworkBug
import com.example.bugtracker.data.network.models.asDatabaseModel
import com.example.bugtracker.data.network.models.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class BugRepository(
    val bugDao: BugDao,
    val bugDataSource: BugDataSource
) {
    companion object {
        @Volatile
        private var instance: BugRepository? = null

        fun getInstance(bugDao: BugDao, bugDataSource: BugDataSource) =
            instance ?: synchronized(this) {
                instance ?: BugRepository(bugDao, bugDataSource).also { instance = it }
            }
    }

    suspend fun getAllBugs(): List<Bug> = withContext(Dispatchers.IO) {
        var bugs: List<Bug>? = null

        bugs = bugDao.getAll().asDomainModel()

        if (bugs!!.isEmpty()) {
            val networkBugs = bugDataSource.getBugs();
            bugs = networkBugs.asDomainModel();
            bugDao.add(networkBugs.asDatabaseModel());
        }

        bugs!!
    }

    suspend fun getBug(bugID: Int): Bug? = withContext(Dispatchers.IO) {
        var bug: Bug? = null

        val daoBug = bugDao.getBug(bugID)

        if(daoBug == null) {
            val networkBug = bugDataSource.getBug(bugID)
            if(networkBug != null) {
                bug = networkBug.asDomainModel()
            }
        } else {
            bug = daoBug.asDomainModel()
        }

        bug
    }
}