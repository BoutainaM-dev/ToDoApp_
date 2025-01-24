package com.boutaina.todo.list

import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String? = null
)
