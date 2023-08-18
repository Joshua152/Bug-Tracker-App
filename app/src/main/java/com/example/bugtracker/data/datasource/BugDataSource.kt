package com.example.bugtracker.data.datasource

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bugtracker.data.data.Bug
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
     * @param onBugsReceived Callback for when bugs have been received OR empty array fi error
     */
    fun getBugs(onBugsReceived: (result: Array<Bug>) -> Unit) {
        val req = JsonArrayRequest(
            Request.Method.GET, "$dbURL/bugs", null,
            { response ->
                val bugs = Json.decodeFromString<Array<Bug>>(response.toString())
                onBugsReceived.invoke(bugs)
            },
            { error ->
                onBugsReceived.invoke(arrayOf<Bug>())
            }
        )

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }

    /**
     * Gets a single bug at the given id
     * @param bugID ID of the bug to get
     * @param onBugReceived Callback for when bug has been received OR null if error
     */
    fun getBug(bugID: Int, onBugReceived: (result: Bug?) -> Unit) {
        val req = JsonObjectRequest(Request.Method.GET, "$dbURL/bugs/$bugID", null,
            { response ->
                val bug = Json.decodeFromString<Bug>(response.toString())
                onBugReceived.invoke(bug)
            },
            { error ->
                onBugReceived.invoke(null)
            }
        )

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }

    /**
     * Gets a list of all the bugs for the project with the given ID
     * @param projectID The ID of the project to get the hugs of
     * @param onBugsReceived Callback for when bugs have been received OR empty array if error
     */
    fun getProjectBugs(projectID: Int, onBugsReceived: (result: Array<Bug>) -> Unit) {
        val req = JsonArrayRequest(Request.Method.GET, "$dbURL/projects/$projectID/bugs", null,
            { response ->
                val bugs = Json.decodeFromString<Array<Bug>>(response.toString())
                onBugsReceived.invoke(bugs)
            },
            { error ->
                onBugsReceived.invoke(arrayOf<Bug>())
            }
        )

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }

    /**
     * Adds a bug to the database
     * @param bug Bug to add to the database
     * @param onResponse Callback for if the request was a success or not
     */
    fun addBug(bug: Bug, onResponse: (isSuccess: Boolean) -> Unit) {
        val req = object : StringRequest(Request.Method.POST, "$dbURL/bugs",
            { response ->
                onResponse(true)
            },
            { error ->
                onResponse(false)
            }
        ) {
            override fun getBody(): ByteArray {
                return Json.encodeToString(bug).toByteArray()
            }
        }

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }

    /**
     * Replaces the bug at the given ID with the bug passed in
     * @param bugID ID of the bug to replace
     * @param bug Bug to replace bug with given ID
     * @param onResponse Callback for if the request was a success or not
     */
    fun updateBug(bugID: Int, bug: Bug, onResponse: (isSuccess: Boolean) -> Unit) {
        val req = object : StringRequest(Request.Method.PUT, "$dbURL/bugs/$bugID",
            { response ->
                onResponse(true)
            },
            { error ->
                onResponse(false)
            }
        ) {
            override fun getBody(): ByteArray {
                return Json.encodeToString(bug).toByteArray()
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