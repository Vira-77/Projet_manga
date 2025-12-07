package com.mangaproject.data.model

data class AddFavoriteRequest(
    val mangaId: String,
    val source: String // "local" ou "jikan"
)

