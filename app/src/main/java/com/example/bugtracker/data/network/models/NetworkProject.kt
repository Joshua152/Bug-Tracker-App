package com.example.bugtracker.data.network.models

import kotlinx.serialization.Serializable

@Serializable
data class NetworkProject(
    val projectID: Int,
    val name: String
)