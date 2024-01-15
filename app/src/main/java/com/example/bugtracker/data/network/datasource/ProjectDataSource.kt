package com.example.bugtracker.data.network.datasource

import android.annotation.SuppressLint
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bugtracker.NetworkRequest
import com.example.bugtracker.data.network.models.NetworkBug
import com.example.bugtracker.data.network.models.NetworkProject
import com.example.bugtracker.data.serializer.networkRequestQueueStore
import com.example.bugtracker.networkutils.InternetConnectivityObserver
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ProjectDataSource(
    val dbURL: String,
    val context: Context
) : DataSource<NetworkProject>() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: ProjectDataSource? = null

        /**
         * Gets singleton instance of the ProjectDataSource
         * @param dbURL Base URL of the API
         * @param context The context
         */
        fun getInstance(dbURL: String, context: Context) =
            instance ?: synchronized(this) {
                instance ?: ProjectDataSource(dbURL, context.applicationContext).also { instance = it }
            }
    }

    private val volleyRequestQueue: RequestQueue = Volley.newRequestQueue(context)
    private val connectivity: InternetConnectivityObserver = InternetConnectivityObserver.getInstance(context)
    /**
     * Gets a single project at the given id
     * @param id ID of project to get
     */
    override suspend fun get(id: Int) = suspendCoroutine<NetworkProject?> { cont ->
        val req = JsonObjectRequest(Request.Method.GET, "$dbURL/projects/$id", null,
            { response ->
                val networkProject = Json.decodeFromString<NetworkProject>(response.toString())
                cont.resume(networkProject)
            },
            { error ->
                cont.resume(null)
            }
        )

        req.setShouldRetryServerErrors(false)
        volleyRequestQueue.add(req)
    }

    /**
     * Gets a list of all the projects
     */
    override suspend fun getAll() = suspendCoroutine<List<NetworkProject>> { cont ->
        val req = JsonArrayRequest(Request.Method.GET, "$dbURL/projects", null,
            { response ->
                val networkProjects = Json.decodeFromString<List<NetworkProject>>(response.toString())
                cont.resume(networkProjects)
            },
            { error ->
                cont.resume(listOf())
            }
        )

        req.setShouldRetryServerErrors(false)
        volleyRequestQueue.add(req)
    }

    /**
     * Adds a project to the database
     * @param networkProject Project to add to the database
     */
    override suspend fun add(vararg networkProject: NetworkProject): Boolean {
        val uri = "$dbURL/projects"
        val jsonProject = Json.encodeToString(networkProject)

        if (!connectivity.ping()) {
            addToOfflineQueue(Request.Method.POST, uri, jsonProject)

            return true
        }

        return suspendCoroutine { cont ->
            val req = object : StringRequest(Request.Method.POST, uri,
                {
                    cont.resume(true)
                },
                {
                    cont.resume(false)
                }
            ) {
                override fun getBody(): ByteArray {
                    return jsonProject.toByteArray()
                }
            }

            req.setShouldRetryServerErrors(false)
            volleyRequestQueue.add(req)
        }
    }

    /**
     * Adds a list of projects to the database
     * @param networkProjects List of projects to add to the database
     * @return Returns a boolean on if the operation was a success or not
     */
    override suspend fun add(networkProjects: List<NetworkProject>): Boolean {
        return add(*networkProjects.toTypedArray())
    }

    /**
     * Replaces the project at the given ID with the project passed in
     * @param id ID of project to replace
     * @param networkProject Project to replace project with given ID
     */
    override suspend fun update(id: Int, networkProject: NetworkProject): Boolean {
        val uri = "$dbURL/projects/$id"
        val jsonProject = Json.encodeToString(networkProject)

        if (!connectivity.ping()) {
            addToOfflineQueue(Request.Method.PUT, uri, jsonProject)

            return true
        }

        return suspendCoroutine { cont ->
            val req = object : StringRequest(Request.Method.PUT, uri,
                {
                    cont.resume(true)
                },
                {
                    cont.resume(false)
                }
            ) {
                override fun getBody(): ByteArray {
                    return jsonProject.toByteArray()
                }
            }

            req.setShouldRetryServerErrors(false)
            volleyRequestQueue.add(req)
        }
    }

    /**
     * Deletes project with the given ID
     * @param id ID of project to delete
     */
    override suspend fun delete(id: Int): Boolean {
        val uri = "$dbURL/projects/$id"

        if (!connectivity.ping()) {
            addToOfflineQueue(Request.Method.DELETE, uri, null)

            return true
        }

        return suspendCoroutine { cont ->
            val req = StringRequest(Request.Method.DELETE, uri,
                {
                    cont.resume(true)
                },
                {
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
                .addNetworkRequest(
                    NetworkRequest.newBuilder()
                    .setHttpVerb(httpVerb)
                    .setUri(uri)
                    .setBody(body ?: ""))
                .build()
        }
    }
}