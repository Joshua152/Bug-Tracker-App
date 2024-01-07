package com.example.bugtracker.data.domain

import com.example.bugtracker.data.local.models.DatabaseProject
import com.example.bugtracker.data.network.models.NetworkProject

data class Project(
    val projectID: Int,
    val name: String
) : DomainModelConversions<Project, DatabaseProject, NetworkProject> {
    override fun asDatabaseModel(): DatabaseProject {
        return DatabaseProject(
            projectID = projectID,
            name = name
        )
    }

    override fun asNetworkModel(): NetworkProject {
        return NetworkProject(
            projectID = projectID,
            name = name
        )
    }
}