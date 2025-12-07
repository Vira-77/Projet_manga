package com.mangaproject.data.repository

import com.mangaproject.data.api.ApiService
import com.mangaproject.data.model.AiRequest
import com.mangaproject.data.model.AiResponse

class AiRepository(private val api: ApiService) {

    suspend fun chatWithAI(message: String): AiResponse {
        val result = api.chatWithAI(AiRequest(message))

        return result
    }
}
