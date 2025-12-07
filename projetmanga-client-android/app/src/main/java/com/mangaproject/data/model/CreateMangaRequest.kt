package com.mangaproject.data.model

data class CreateMangaRequest(
    val nom: String,
    val auteur: String,
    val description: String?,
    val dateDeSortie: String?,
    val urlImage: String?,
    val genres: List<String>
)
