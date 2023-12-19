package com.example.bugtracker.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bugtracker.data.domain.Project

@Entity(tableName = "project")
data class DatabaseProject(
    @PrimaryKey
    @ColumnInfo(name = "project_id")
    val projectID: Int,
    @ColumnInfo(name = "name")
    val name: String
)

fun List<DatabaseProject>.asDomainModel(): List<Project> {
    return map {
        Project(
            projectID = it.projectID,
            name = it.name
        )
    }
}