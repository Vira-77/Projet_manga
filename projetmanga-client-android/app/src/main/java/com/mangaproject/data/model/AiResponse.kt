package com.mangaproject.data.model

data class AiResponse(
    val reply: String,
    val messageId: String? = null,
    val status: String? = null
)

