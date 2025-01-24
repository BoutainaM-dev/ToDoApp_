package com.boutaina.todo.data

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val name: String,
    @SerializedName("avatar_medium") val avatar: String? = null
)

data class UserUpdate(val name: String)

data class Commands(val user_update: UserUpdate)
