package com.example.bugtracker.data

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bugtracker.NetworkRequest
import com.example.bugtracker.data.local.models.LocalModelConversion
import com.example.bugtracker.data.network.datasource.DataSource
import com.example.bugtracker.data.network.models.NetworkModelConversion
import com.example.bugtracker.data.network.models.asDatabaseModel
import com.example.bugtracker.networkutils.InternetConnectivityObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.LinkedList
import java.util.Queue
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
    private val context: Context
) {
    private val volleyRequestQueue: RequestQueue = Volley.newRequestQueue(context)
    private val offlineRequestQueue: Queue<NetworkRequest> = LinkedList()

    private val connectivity: InternetConnectivityObserver = InternetConnectivityObserver.getInstance(context)

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
        println("Current queue: $offlineRequestQueue")

        // handle deletes, and everything
        // database syncing

        /*
        grab all from remote

         */

        /*
        1) send requests in queue
        2) delete all from dao (can find sub of sets)
        3) fetch from remote
         */

        // don't allow get multiple times in a row

        while (offlineRequestQueue.isNotEmpty()) {
            val req = offlineRequestQueue.remove()
            val statusOk = sendNetworkRequest(req.httpVerb, req.uri, req.body)
            if (!statusOk) {
                println("Error clearing request queue during sync")
            }
        }

        refresh()
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val itemsFromNetwork = dataSource.getAll()
        daoDeleteAll()
        daoAddAll(itemsFromNetwork.asDatabaseModel())
    }

    private suspend fun sendNetworkRequest(httpVerb: Int, uri: String, body: String) = suspendCoroutine { cont ->
        val req = object : StringRequest(
            httpVerb, uri,
            { response ->
                cont.resume(true)
            },
            { error ->
                cont.resume(false)
            }
        ) {
            override fun getBody(): ByteArray {
                return body.toByteArray()
            }
        }

        req.setShouldRetryServerErrors(false)
        volleyRequestQueue.add(req)
    }

    fun addToQueue(req: StringRequest) {
//        if(connectivity.ping()) {
//            volleyRequestQueue.add(req)
//        } else {
//            offlineRequestQueue.add(NetworkRequest(
//    //  TODO CHANGE PROTO TO REPEATED OF NETWORK REQUEST (need list in proto)
//            ))
//        }
    }
}