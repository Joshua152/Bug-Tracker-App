package com.example.bugtracker.data.network.models

import com.example.bugtracker.data.domain.Bug
import com.example.bugtracker.data.local.models.DatabaseBug
import kotlinx.serialization.Serializable

@Serializable
data class NetworkBug(
    val projectID: Int,
    val bugID: Int,
    val title: String,
    val description: String,
    val timeAmt: Double,
    val complexity: Double
)

fun NetworkBug.asDatabaseModel(): DatabaseBug {
    return DatabaseBug(
        projectID = projectID,
        bugID = bugID,
        title = title,
        description = description,
        timeAmt = timeAmt,
        complexity = complexity
    )
}

fun List<NetworkBug>.asDatabaseModel(): List<DatabaseBug> {
    return map { it.asDatabaseModel() }
}

fun NetworkBug.asDomainModel(): Bug {
    return Bug(
        projectID = projectID,
        bugID = bugID,
        title = title,
        description = description,
        timeAmt = timeAmt,
        complexity = complexity
    )
}

fun List<NetworkBug>.asDomainModel(): List<Bug> {
    return map { it.asDomainModel() }
}