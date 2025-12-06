package com.mangaproject.data.model

import com.google.gson.annotations.SerializedName

data class ChaptersByMangaResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("chapters")
    val chapters: List<Chapter>,

    @SerializedName("count")
    val count: Int,

    @SerializedName("mangaId")
    val mangaId: String
)

data class ChapterByIdResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("chapter")
    val chapter: Chapter
)