package com.mangaproject.data.model

import com.google.gson.annotations.SerializedName

data class ReadingHistory(
    @SerializedName("_id")
    val id: String,
    @SerializedName("mangaId")
    val mangaId: String,
    @SerializedName("source")
    val source: String, // "local" ou "jikan"
    @SerializedName("currentChapterId")
    val currentChapterId: String?,
    @SerializedName("currentChapterNumber")
    val currentChapterNumber: Int?,
    @SerializedName("lastReadAt")
    val lastReadAt: String,
    @SerializedName("title")
    val title: String?,
    @SerializedName("imageUrl")
    val imageUrl: String?
)

data class UpdateReadingHistoryRequest(
    val mangaId: String,
    val source: String,
    val chapterId: String?,
    val chapterNumber: Int?,
    val title: String?,
    val imageUrl: String?
)

data class ReadingHistoryResponse(
    val history: List<ReadingHistory>
)

data class SingleReadingHistoryResponse(
    val history: ReadingHistory?
)

