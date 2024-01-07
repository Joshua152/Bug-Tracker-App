package com.example.bugtracker.data.network.datasource

import android.annotation.SuppressLint
import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bugtracker.data.network.models.NetworkBug
import com.example.bugtracker.data.network.models.NetworkProject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ProjectDataSource(
    val dbURL: String,
    private val context: Context
) : DataSource<NetworkProject> {
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

    private val queue = Volley.newRequestQueue(context)

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
        queue.add(req)
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
                cont.resume(listOf<NetworkProject>())
            }
        )

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }

    /**
     * Adds a project to the database
     * @param networkProject Project to add to the database
     */
    override suspend fun add(vararg networkProject: NetworkProject) = suspendCoroutine<Boolean> { cont ->
        val req = object : StringRequest(Request.Method.POST, "$dbURL/projects",
            {
                cont.resume(true)
            },
            {
                cont.resume(false)
            }
        ) {
            override fun getBody(): ByteArray {
                return Json.encodeToString(networkProject).toByteArray()
            }
        }

        req.setShouldRetryServerErrors(false)
        queue.add(req)
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
    override suspend fun update(id: Int, networkProject: NetworkProject) = suspendCoroutine<Boolean>{ cont ->
        val req = object : StringRequest(Request.Method.PUT, "$dbURL/projects/$id",
            {
                cont.resume(true)
            },
            {
                cont.resume(false)
            }
        ) {
            override fun getBody(): ByteArray {
                return Json.encodeToString(networkProject).toByteArray()
            }
        }

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }

    /**
     * Deletes project with the given ID
     * @param id ID of project to delete
     */
    override suspend fun delete(id: Int) = suspendCoroutine<Boolean> { cont ->
        val req = StringRequest(Request.Method.DELETE, "$dbURL/projects/$id",
            {
                cont.resume(true)
            },
            {
                cont.resume(false)
            }
        )

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }
}