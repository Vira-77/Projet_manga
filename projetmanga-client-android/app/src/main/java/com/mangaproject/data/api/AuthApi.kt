package com.mangaproject.data.api

import com.mangaproject.data.model.LoginRequest
import com.mangaproject.data.model.LoginResponse
import com.mangaproject.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("/auth/register")
    suspend fun register(@Body body: RegisterRequest): LoginResponse
}
