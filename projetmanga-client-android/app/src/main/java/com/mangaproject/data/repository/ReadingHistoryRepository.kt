package com.mangaproject.data.repository

import com.mangaproject.data.api.ApiService
import com.mangaproject.data.model.ReadingHistory
import com.mangaproject.data.model.ReadingHistoryResponse
import com.mangaproject.data.model.SingleReadingHistoryResponse
import com.mangaproject.data.model.UpdateReadingHistoryRequest

class ReadingHistoryRepository(private val apiService: ApiService) {
    
    suspend fun getReadingHistory(): List<ReadingHistory> {
        return try {
            val response = apiService.getReadingHistory()
            response.history
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun updateReadingHistory(
        mangaId: String,
        source: String,
        chapterId: String?,
        chapterNumber: Int?,
        title: String?,
        imageUrl: String?
    ): ReadingHistory? {
        return try {
            val request = UpdateReadingHistoryRequest(
                mangaId = mangaId,
                source = source,
                chapterId = chapterId,
                chapterNumber = chapterNumber,
                title = title,
                imageUrl = imageUrl
            )
            val response = apiService.updateReadingHistory(request)
            response.history
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getMangaReadingHistory(mangaId: String): ReadingHistory? {
        return try {
            val response = apiService.getMangaReadingHistory(mangaId)
            response.history
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun deleteReadingHistory(mangaId: String): Boolean {
        return try {
            apiService.deleteReadingHistory(mangaId)
            true
        } catch (e: Exception) {
            false
        }
    }
}

