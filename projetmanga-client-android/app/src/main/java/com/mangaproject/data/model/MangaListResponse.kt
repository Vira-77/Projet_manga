package com.mangaproject.data.model

data class MangaResponse(
    val message: String,
    val mangas: List<Manga>,
    val count: Int,
    val page: Int
)

