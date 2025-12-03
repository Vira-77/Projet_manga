package com.mangaproject.data.model

import com.google.gson.annotations.SerializedName

data class Manga(
    @SerializedName("_id")
    val id: String,
    val nom: String,
    val description: String?,
    val dateDeSortie: String?,
    val urlImage: String?,
    val auteur: String,
    val genres: List<String>?
)

data class MangaUpdateRequest(
    val nom: String,
    val description: String?,
    val urlImage: String?,
    val dateDeSortie: String?,
    val auteur: String,
    val genres: List<String>
)

data class MangaDetailResponse(
    val message: String?,
    val manga: Manga
)