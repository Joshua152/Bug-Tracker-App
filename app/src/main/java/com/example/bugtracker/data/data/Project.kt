package com.example.bugtracker.data.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val projectID: Int,
    val name: String
)