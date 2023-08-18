package com.example.bugtracker.data.datasource

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bugtracker.data.data.Project
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class ProjectDataSource(
    val dbURL: String,
    private val context: Context
) {
    companion object {
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
     * Gets a list of all the projects
     * @param onProjectsReceived Callback for when projects have been received OR empty array if error
     */
    fun getProjects(onProjectsReceived: (result: Array<Project>) -> Unit) {
        val req = JsonArrayRequest(Request.Method.GET, "$dbURL/projects", null,
            { response ->
                val projects = Json.decodeFromString<Array<Project>>(response.toString())
                onProjectsReceived.invoke(projects)
            },
            { error ->
                onProjectsReceived.invoke(arrayOf<Project>())
            }
        )

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }

    /**
     * Gets a single project at the given id
     * @param projectID ID of project to get
     * @param onProjectReceived Callback for when project has been received OR null if error
     */
    fun getProject(projectID: Int, onProjectReceived: (result: Project?) -> Unit) {
        val req = JsonObjectRequest(Request.Method.GET, "$dbURL/projects/$projectID", null,
            { response ->
                val project = Json.decodeFromString<Project>(response.toString())
                onProjectReceived.invoke(project)
            },
            { error ->
                onProjectReceived.invoke(null)
            }
        )

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }

    /**
     * Adds a project to the database
     * @param project Project to add to the database
     * @param onResponse Callback for if the request was a success or not
     */
    fun addProject(project: Project, onResponse: (isSuccess: Boolean) -> Unit) {
        val req = object : StringRequest(Request.Method.POST, "$dbURL/projects",
            {
                onResponse(true)
            },
            {
                onResponse(false)
            }
        ) {
            override fun getBody(): ByteArray {
                return Json.encodeToString(project).toByteArray()
            }
        }

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }

    /**
     * Replaces the project at the given ID with the project passed in
     * @param projectID ID of project to replace
     * @param project Project to replace project with given ID
     * @param onResponse Callback for if the request was a success or not
     */
    fun updateProject(projectID: Int, project: Project, onResponse: (isSuccess: Boolean) -> Unit) {
        val req = object : StringRequest(Request.Method.PUT, "$dbURL/projects/$projectID",
            {
                onResponse(true)
            },
            {
                onResponse(false)
            }
        ) {
            override fun getBody(): ByteArray {
                return Json.encodeToString(project).toByteArray()
            }
        }

        req.setShouldRetryServerErrors(false)
        queue.add(req)
    }

    /**
     * Deletes project with the given ID
     * @param projectID ID of project to delete
     * @param onResponse Callback for if the request was a success or not
     */
    fun deleteProject(projectID: Int, onResponse: (isSuccess: Boolean) -> Unit) {
        val req = StringRequest(Request.Method.DELETE, "$dbURL/projects/$projectID",
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