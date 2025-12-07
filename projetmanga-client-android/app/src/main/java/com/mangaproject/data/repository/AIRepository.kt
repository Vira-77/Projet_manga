package com.mangaproject.data.repository

import com.mangaproject.data.api.ApiService
import com.mangaproject.data.model.AiRequest
import com.mangaproject.data.model.AiResponse

class AiRepository(private val api: ApiService) {

    suspend fun chatWithAI(message: String, userId: String? = null, messageId: String? = null): AiResponse {
        val result = api.chatWithAI(AiRequest(message, userId, messageId))
        return result
    }
}
