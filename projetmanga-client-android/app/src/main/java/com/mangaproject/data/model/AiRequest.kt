package com.mangaproject.data.model

data class AiRequest(
    val message: String,
    val userId: String? = null,
    val messageId: String? = null
)
