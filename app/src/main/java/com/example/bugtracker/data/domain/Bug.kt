package com.example.bugtracker.data.domain

import com.example.bugtracker.data.local.models.DatabaseBug
import com.example.bugtracker.data.network.models.NetworkBug

data class Bug(
    val projectID: Int,
    val bugID: Int,
    val title: String,
    val description: String,
    val timeAmt: Double,
    val complexity: Double
) : DomainModelConversions<Bug, DatabaseBug, NetworkBug> {

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

    override fun asNetworkModel(): NetworkBug {
        return NetworkBug(
            projectID = projectID,
            bugID = bugID,
            title = title,
            description = description,
            timeAmt = timeAmt,
            complexity = complexity
        )
    }
}
