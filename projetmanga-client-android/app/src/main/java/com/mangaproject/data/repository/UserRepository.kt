package com.mangaproject.data.repository

import com.mangaproject.data.api.ApiService
import com.mangaproject.data.model.UpdateUserRequest

class UserRepository(private val api: ApiService) {

    suspend fun getUsers() = api.getAllUsers()

    suspend fun updateUserRole(id: String, newRole: String) =
        api.updateUser(id, UpdateUserRequest(role = newRole))

    suspend fun deleteUser(id: String) =
        api.deleteUser(id)
}
