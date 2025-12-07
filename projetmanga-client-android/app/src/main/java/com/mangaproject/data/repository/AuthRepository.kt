package com.mangaproject.data.repository

import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.model.LoginRequest
import com.mangaproject.data.model.LoginResponse

class AuthRepository {

    suspend fun login(email: String, password: String): LoginResponse {
        return RetrofitInstance.api.login(LoginRequest(email, password))
    }
}
