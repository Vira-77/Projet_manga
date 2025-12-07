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
    val chapter: Chapter,

    @SerializedName("navigation")
    val navigation: ChapterNavigation? = null // ✅ Infos de navigation
)

data class ChapterNavigation(
    @SerializedName("previous")
    val previous: ChapterInfo? = null,

    @SerializedName("next")
    val next: ChapterInfo? = null
)

data class ChapterInfo(
    @SerializedName("_id")
    val id: String,

    @SerializedName("chapterNumber")
    val chapterNumber: Int,

    @SerializedName("titre")
    val titre: String
)

// DTO pour créer un chapitre
data class CreateChapterRequest(
    val titre: String?,
    val manga: String, // ID du manga
    val chapterNumber: Int,
    val pages: List<PageData> = emptyList() // Vide au départ
)

data class PageData(
    val numero: Int,
    val urlImage: String
)