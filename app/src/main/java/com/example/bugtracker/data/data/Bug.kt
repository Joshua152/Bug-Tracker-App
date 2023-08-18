package com.example.bugtracker.data.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Bug(
    val projectID: Int,
    val bugID: Int,
    val title: String,
    val description: String,
    val timeAmt: Double,
    val complexity: Double
)