package com.example.bugtracker.data.network.datasource

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bugtracker.NetworkRequest
import com.example.bugtracker.data.network.models.NetworkBug
import com.example.bugtracker.data.serializer.networkRequestQueueStore
import com.example.bugtracker.networkutils.InternetConnectivityObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.LinkedList
import java.util.Queue
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class BugDataSource(
    val dbURL: String,
    val context: Context
) : DataSource<NetworkBug>() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: BugDataSource? = null

        /**
         * Gets singleton instance of the BugDataSource
         * @param dbURL Base URL of the API
         * @param context The context
         */
        fun getInstance(dbURL: String, context: Context) =
            instance ?: synchronized(this) {
                instance ?: BugDataSource(dbURL, context.applicationContext).also { instance = it }
            }
    }

    // TODO CREATE HELPER FUNCTION THAT RETURSN FUNCTION FOR OFFLINE OR ONLINE QUEUE

    private val volleyRequestQueue: RequestQueue = Volley.newRequestQueue(context)
    private val connectivity: InternetConnectivityObserver = InternetConnectivityObserver.getInstance(context)

    /**
     * Gets a single bug at the given id
     * @param id ID of the bug to get
     * @return NetworkBug with the given ID OR null if error
     */
    override suspend fun get(id: Int): NetworkBug? = suspendCoroutine<NetworkBug?> { cont ->
        val req = JsonObjectRequest(Request.Method.GET, "$dbURL/bugs/$id", null,
            { response ->
                val networkBug = Json.decodeFromString<NetworkBug>(response.toString())
                cont.resume(networkBug)
            },
            { error ->
                cont.resume(null)
            }
        )

        req.setShouldRetryServerErrors(false)
        volleyRequestQueue.add(req)
    }

    /**
     * Gets a list of all the bugs
     * @return List of NetworkBugs from server OR empty if error
     */
    override suspend fun getAll() = suspendCoroutine<List<NetworkBug>> { cont ->
        val req = JsonArrayRequest(
            Request.Method.GET, "$dbURL/bugs", null,
            { response ->
                val networkBugs = Json.decodeFromString<List<NetworkBug>>(response.toString())
                cont.resume(networkBugs)
            },
            { error ->
                cont.resume(listOf())
            }
        )

        req.setShouldRetryServerErrors(false)
        volleyRequestQueue.add(req)
    }

    /**
     * Gets a list of all the bugs for the project with the given ID
     * @param projectID The ID of the project to get the hugs of
     * @return List of NetworkBugs for a given project OR an empty list if error
     */
    suspend fun getProjectBugs(projectID: Int) = suspendCoroutine<List<NetworkBug>> { cont ->
        val req = JsonArrayRequest(Request.Method.GET, "$dbURL/projects/$projectID/bugs", null,
            { response ->
                val networkBugs = Json.decodeFromString<List<NetworkBug>>(response.toString())
                cont.resume(networkBugs)
            },
            { error ->
                cont.resume(listOf())
            }
        )

        req.setShouldRetryServerErrors(false)
        volleyRequestQueue.add(req)
    }

    /**
     * Adds a bug to the database
     * @param networkBug Bug to add to the database
     * @return Returns a boolean for if the operation was a success or not
     */
    override suspend fun add(vararg networkBug: NetworkBug): Boolean {
        val uri = "$dbURL/bugs"
        val jsonBug = Json.encodeToString(networkBug)

        if (!connectivity.ping()) {
            addToOfflineQueue(Request.Method.POST, uri, jsonBug)

            return true
        }

        return suspendCoroutine { cont ->
            val req = object : StringRequest(Request.Method.POST, uri,
                { response ->
                    cont.resume(true)
                },
                { error ->
                    cont.resume(false)
                }
            ) {
                override fun getBody(): ByteArray {
                    return jsonBug.toByteArray()
                }
            }

            req.setShouldRetryServerErrors(false)
            volleyRequestQueue.add(req)
        }
    }

    /**
     * Adds a list of bugs to the database
     * @param networkBugs List of bugs to add to the database
     * @return Returns a boolean on if the operation was a success or not
     */
    override suspend fun add(networkBugs: List<NetworkBug>): Boolean {
        return add(*networkBugs.toTypedArray())
    }

    /**
     * Replaces the bug at the given ID with the bug passed in
     * @param id ID of the bug to replace
     * @param networkBug Bug to replace bug with given ID
     */
    override suspend fun update(id: Int, networkBug: NetworkBug): Boolean {
        val uri = "$dbURL/bugs/$id"
        val jsonBug = Json.encodeToString(networkBug)

        if (!connectivity.ping()) {
            addToOfflineQueue(Request.Method.PUT, uri, jsonBug)

            return true
        }

        return suspendCoroutine { cont ->
            val req = object : StringRequest(Request.Method.PUT, uri,
                { response ->
                    cont.resume(true)
                },
                { error ->
                    cont.resume(false)
                }
            ) {
                override fun getBody(): ByteArray {
                    return jsonBug.toByteArray()
                }
            }

            req.setShouldRetryServerErrors(false)
            volleyRequestQueue.add(req)
        }
    }

    /**
     * Deletes bug with the given ID
     * @param id ID of bug to delete
     */
    override suspend fun delete(id: Int): Boolean {
        val uri = "$dbURL/bugs/$id"

        if (!connectivity.ping()) {
            addToOfflineQueue(Request.Method.DELETE, uri, null)

            return true
        }

        return suspendCoroutine { cont ->
            val req = StringRequest(Request.Method.DELETE, uri,
                { response ->
                    cont.resume(true)
                },
                { error ->
                    cont.resume(false)
                }
            )

            req.setShouldRetryServerErrors(false)
            volleyRequestQueue.add(req)
        }
    }

    private suspend fun addToOfflineQueue(httpVerb: Int, uri: String, body: String?) {
        context.networkRequestQueueStore.updateData { currentQueue ->
            currentQueue.toBuilder()
                .addNetworkRequest(NetworkRequest.newBuilder()
                    .setHttpVerb(httpVerb)
                    .setUri(uri)
                    .setBody(body ?: ""))
                .build()
        }
    }
}