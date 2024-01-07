package com.example.bugtracker.data.network.models

import com.example.bugtracker.data.domain.Project
import com.example.bugtracker.data.local.models.DatabaseProject
import kotlinx.serialization.Serializable

@Serializable
data class NetworkProject(
    val projectID: Int,
    val name: String
) : NetworkModelConversion<NetworkProject, DatabaseProject, Project> {

    override fun asDatabaseModel(): DatabaseProject {
        return DatabaseProject(
            projectID = projectID,
            name = name
        )
    }

    override fun asDomainModel(): Project {
        return Project(
            projectID = projectID,
            name = name
        )
    }
}