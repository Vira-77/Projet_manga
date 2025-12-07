package com.mangaproject.data.repository

import android.content.Context
import android.net.Uri
import com.mangaproject.data.api.ApiService
import com.mangaproject.data.model.Chapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class ChapterRepository(
    private val api: ApiService
) {

    suspend fun createChapterWithImages(
        context: Context,
        mangaId: String,
        numero: Int,
        titre: String?,
        imageUris: List<Uri>,
        onProgress: (current: Int, total: Int) -> Unit = { _, _ -> }
    ): Chapter {

        // Création du chapitre
        val chapterData = mapOf(
            "titre" to (titre ?: "Chapitre $numero"),
            "manga" to mangaId,
            "chapterNumber" to numero
        )

        val json = com.google.gson.Gson().toJson(chapterData)
        val jsonBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val createdChapter = api.createChapter(jsonBody)

        val chapterId = createdChapter.id
            ?: throw IllegalStateException("L'ID du chapitre créé est null ou vide")

        // Upload des images
        val total = imageUris.size

        imageUris.forEachIndexed { index, uri ->
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Cannot open URI: $uri")

            val tempFile = File(context.cacheDir, "temp_page_${System.currentTimeMillis()}.jpg")

            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()

            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
            val numeroPart = "${index + 1}".toRequestBody("text/plain".toMediaTypeOrNull())

            api.addPageToChapter(chapterId, imagePart, numeroPart)

            tempFile.delete()
            onProgress(index + 1, total)
        }

        return createdChapter
    }

    suspend fun getChaptersByManga(mangaId: String): List<Chapter> {
        return api.getChaptersByManga(mangaId)
    }

    suspend fun deleteChapter(chapterId: String) {
        api.deleteChapter(chapterId)
    }
}