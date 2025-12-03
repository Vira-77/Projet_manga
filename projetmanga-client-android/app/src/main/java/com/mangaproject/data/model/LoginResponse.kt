package com.mangaproject.data.model

data class LoginResponse(
    val token: String,
    val user: UserData
)

data class UserData(
    val id: String,
    val name: String,
    val email: String,
    val role: String
)
