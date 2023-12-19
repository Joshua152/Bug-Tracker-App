package com.example.bugtracker.data.network.datasource

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bugtracker.data.network.models.NetworkBug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class BugDataSource(
    val dbURL: String,
    private val context: Context
) {
    companion object {
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

    private val queue = Volley.newRequestQueue(context)

    /**
     * Gets a list of all the bugs
     * @return List of NetworkBugs from server OR empty if error
     */
    suspend fun getBugs() = suspendCoroutine<List<NetworkBug>> { cont ->
        val req = JsonArrayRequest(
            Request.Method.GET, "$dbURL/bugs", null,
            { response ->
                val networkBugs = Json.decodeFromString<List<NetworkBug>>(response.toString())
                cont.resume(networkBugs);
            },
            { error ->
                cont.resume(listOf<NetworkBug>());
            }
        )

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }

    /**
     * Gets a single bug at the given id
     * @param bugID ID of the bug to get
     * @return NetworkBug with the given ID OR null if error
     */
    suspend fun getBug(bugID: Int) = suspendCoroutine<NetworkBug?> { cont ->
        val req = JsonObjectRequest(Request.Method.GET, "$dbURL/bugs/$bugID", null,
            { response ->
                val networkBug = Json.decodeFromString<NetworkBug>(response.toString())
                cont.resume(networkBug)
            },
            { error ->
                cont.resume(null)
            }
        )

        req.setShouldRetryServerErrors(false)
        queue.add(req)
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
                cont.resume(listOf<NetworkBug>())
            }
        )

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }

    /**
     * Adds a bug to the database
     * @param networkBug Bug to add to the database
     * @return Returns a boolean for if the operation was a success or not
     */
    suspend fun addBug(networkBug: NetworkBug) = suspendCoroutine<Boolean> { cont ->
        val req = object : StringRequest(Request.Method.POST, "$dbURL/bugs",
            { response ->
                cont.resume(true)
            },
            { error ->
                cont.resume(false)
            }
        ) {
            override fun getBody(): ByteArray {
                return Json.encodeToString(networkBug).toByteArray()
            }
        }

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }

    /**
     * Replaces the bug at the given ID with the bug passed in
     * @param bugID ID of the bug to replace
     * @param networkBug Bug to replace bug with given ID
     * @param onResponse Callback for if the request was a success or not
     */
    fun updateBug(bugID: Int, networkBug: NetworkBug, onResponse: (isSuccess: Boolean) -> Unit) {
        val req = object : StringRequest(Request.Method.PUT, "$dbURL/bugs/$bugID",
            { response ->
                onResponse(true)
            },
            { error ->
                onResponse(false)
            }
        ) {
            override fun getBody(): ByteArray {
                return Json.encodeToString(networkBug).toByteArray()
            }
        }

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }

    /**
     * Deletes bug with the given ID
     * @param bugID ID of bug to delete
     * @param onResponse Callback for if the request was a success or not
     */
    fun deleteBug(bugID: Int, onResponse: (isSuccess: Boolean) -> Unit) {
        val req = StringRequest(Request.Method.DELETE, "$dbURL/bugs/$bugID",
            {
                onResponse(true)
            },
            {
                onResponse(false)
            }
        )

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }
}