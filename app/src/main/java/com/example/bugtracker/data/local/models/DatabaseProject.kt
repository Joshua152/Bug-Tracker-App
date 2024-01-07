package com.example.bugtracker.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bugtracker.data.domain.Project
import com.example.bugtracker.data.network.models.NetworkProject

@Entity(tableName = "project")
data class DatabaseProject(
    @PrimaryKey
    @ColumnInfo(name = "project_id")
    val projectID: Int,
    @ColumnInfo(name = "name")
    val name: String
) : LocalModelConversion<DatabaseProject, NetworkProject, Project> {

    override fun asNetworkModel(): NetworkProject {
        return NetworkProject(
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
