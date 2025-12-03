package com.mangaproject.data.model

data class GenresResponse(
    val genres: List<JikanGenre>
)

data class JikanGenre(
    val mal_id: Int,
    val name: String,
    val url: String?
)
