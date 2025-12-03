package com.mangaproject.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val email: String,
    val role: String
)
