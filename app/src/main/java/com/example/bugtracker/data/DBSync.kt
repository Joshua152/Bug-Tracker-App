package com.example.bugtracker.data

import android.content.Context
import com.example.bugtracker.data.local.models.LocalModelConversion
import com.example.bugtracker.data.network.datasource.DataSource
import com.example.bugtracker.data.network.models.NetworkModelConversion
import com.example.bugtracker.data.network.models.asDatabaseModel
import com.example.bugtracker.networkutils.InternetConnectivityObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Database sync class for handing syncing between the dao and data source
 *
 * T - Local model
 * S - Network model
 * U - Domain model
 */
open class DBSync<T: LocalModelConversion<T, S, U>, S: NetworkModelConversion<S, T, U>, U> (
//    private val dao: LocalDao<T>, // pass in dao functions instead
    private val daoDeleteAll: () -> Unit,
    private val daoAddAll: (items: List<T>) -> Unit,
    private val dataSource: DataSource<S>,
) {

    /*
    Refresh: only fetch from remote
    Sync: sync and refresh

    ******
    If no network, queue requests in datastore
    On network connectivity change, execute all; let backend handle merging
    */

    /*
    Could also disallow offline queries
     */

    // diff between sync and refresh?
    // upload local to remote
    suspend fun sync() = withContext(Dispatchers.IO) {
        // handle deletes, and everything
        // database syncing

        /*
        grab all from remote

         */
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val itemsFromNetwork = dataSource.getAll()
        daoDeleteAll()
        daoAddAll(itemsFromNetwork.asDatabaseModel())
    }
}