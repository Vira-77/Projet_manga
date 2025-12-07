package com.mangaproject.data.model

import com.google.gson.annotations.SerializedName

data class Chapter(
    @SerializedName("_id")
    val id: String,

    @SerializedName("titre")
    val titre: String,

    @SerializedName("manga")
    val manga: String, // ID du manga

    @SerializedName("pages")
    val pages: List<Page>? = null, // Nullable car pas toujours inclus

    @SerializedName("chapterNumber")
    val chapterNumber: Int? = null,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)

data class Page(
    @SerializedName("numero")
    val numero: Int,

    @SerializedName("urlImage")
    val urlImage: String
)