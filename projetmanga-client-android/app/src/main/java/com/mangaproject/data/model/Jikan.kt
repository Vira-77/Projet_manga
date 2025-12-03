package com.mangaproject.data.model

import android.content.SyncInfo

data class JikanImageJpg(
    val image_url: String?
)

data class JikanImages(
    val jpg: JikanImageJpg?
)

data class JikanManga(
    val mal_id: Int,
    val title: String,
    val score: Double?,
    val images: JikanImages?,
    val synopsis: String?
)

data class TopMangaResponse(
    val top: List<JikanManga>
)
