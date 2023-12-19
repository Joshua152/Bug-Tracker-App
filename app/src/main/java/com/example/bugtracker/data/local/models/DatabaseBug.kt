package com.example.bugtracker.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bugtracker.data.domain.Bug

@Entity(tableName = "bug")
data class DatabaseBug(
    @ColumnInfo(name = "project_id")
    val projectID: Int,
    @PrimaryKey
    @ColumnInfo(name = "bug_id")
    val bugID: Int,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "time_amt")
    val timeAmt: Double,
    @ColumnInfo(name = "complexity")
    val complexity: Double
)

fun DatabaseBug.asDomainModel(): Bug {
    return Bug(
        projectID = projectID,
        bugID = bugID,
        title = title,
        description = description,
        timeAmt = timeAmt,
        complexity = complexity
    )
}

fun List<DatabaseBug>.asDomainModel(): List<Bug> {
    return map { it.asDomainModel() }
}

