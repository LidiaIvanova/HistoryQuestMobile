package com.tsu.alotofquestions.data.model

data class Task(
    val id: Int,
    val title: String,
    val text: String,
    val lat: Double?,
    val lon: Double?,
    val type: String,
    val assignee: String,
    val buttonDateTime: Int
)