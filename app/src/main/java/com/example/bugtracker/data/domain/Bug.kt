package com.example.bugtracker.data.domain

data class Bug(
    val projectID: Int,
    val bugID: Int,
    val title: String,
    val description: String,
    val timeAmt: Double,
    val complexity: Double
)