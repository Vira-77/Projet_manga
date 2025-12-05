package com.mangaproject.data.repository


import com.mangaproject.data.api.ApiService
import com.mangaproject.data.model.UpdateUserRequest

class UserRepository(private val api: ApiService) {

    suspend fun getUsers() = api.getAllUsers()

    suspend fun getUser(id: String)= api.getUser(id)

    suspend fun updateUserRole(id: String, newRole: String) =
        api.updateUser(id, UpdateUserRequest(role = newRole))

    suspend fun updateUser(id: String,name: String? = null,address: String? = null,
                           bio: String? = null, profilePicture: String? = null) =
        api.updateUser(id, UpdateUserRequest(name = name, address = address, bio = bio,
            profilePicture = profilePicture))


    suspend fun deleteUser(id: String) =
        api.deleteUser(id)
}
