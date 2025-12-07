package com.mangaproject.data.model

import com.google.gson.annotations.SerializedName

data class Genre(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val description: String?
)

data class GenreResponse(
    val genres: List<Genre>
)
