package com.mangaproject.data.model

data class UpdateUserRequest(
    val role: String? = null,
    val name: String? = null,
    val address: String? = null,
    val bio: String? = null,
    val profilePicture: String? = null
)
