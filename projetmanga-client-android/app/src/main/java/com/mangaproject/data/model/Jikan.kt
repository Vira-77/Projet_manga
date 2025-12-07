package com.mangaproject.data.model

import android.content.SyncInfo
import kotlinx.serialization.Serializable

@Serializable
data class JikanImageJpg(
    val image_url: String?= null
)

@Serializable
data class JikanImages(
    val jpg: JikanImageJpg?= null
)

@Serializable
data class JikanManga(
    val mal_id: Int,
    val title: String,
    val score: Double?= null,
    val images: JikanImages?= null,
    val synopsis: String?= null
)

@Serializable
data class TopMangaResponse(
    val top: List<JikanManga>
)
