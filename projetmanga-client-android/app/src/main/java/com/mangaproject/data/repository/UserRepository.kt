package com.mangaproject.data.repository

import android.content.Context
import android.net.Uri
import com.mangaproject.data.api.ApiService
import com.mangaproject.data.api.RetrofitInstance.apiService
import com.mangaproject.data.model.UpdateUserRequest
import com.mangaproject.data.model.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class UserRepository(private val api: ApiService,private val token: String? = null) {

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

    suspend fun uploadProfilePicture(context: Context, imageUri: Uri): Result<User> {
        return try {
            val file = uriToFile(context, imageUri)

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

            // ✅ Le nom "profilePicture" doit correspondre à upload.single('profilePicture')
            val body = MultipartBody.Part.createFormData(
                "profilePicture",
                file.name,
                requestFile
            )

            val response = apiService.uploadProfilePicture(body,"Bearer $token")

            file.delete() // Supprimer le fichier temporaire

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.user)
            } else {
                Result.failure(Exception("Erreur ${response.code()}: ${response.message()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProfilePicture(): Result<User> {
        return try {
            val response = apiService.deleteProfilePicture()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.user)
            } else {
                Result.failure(Exception("Erreur ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ Convertir URI en File temporaire
    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Impossible de lire l'image")

        val file = File(context.cacheDir, "profile_upload_${System.currentTimeMillis()}.jpg")

        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        inputStream.close()
        return file
    }
}
