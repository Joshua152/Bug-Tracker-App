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
) : NetworkModelConversion<NetworkBug, DatabaseBug, Bug> {
    override fun asDatabaseModel(): DatabaseBug {
        return DatabaseBug(
            projectID = projectID,
            bugID = bugID,
            title = title,
            description = description,
            timeAmt = timeAmt,
            complexity = complexity
        )
    }

    override fun asDomainModel(): Bug {
        return Bug(
            projectID = projectID,
            bugID = bugID,
            title = title,
            description = description,
            timeAmt = timeAmt,
            complexity = complexity
        )
    }
}
