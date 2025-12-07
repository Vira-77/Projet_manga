package com.mangaproject.utils

object ImageUtils {
    private const val BASE_URL = "http://10.0.2.2:3000"

    fun String.toFullImageUrl(): String {
        return if (this.startsWith("http")) {
            this
        } else {
            "$BASE_URL$this"
        }
    }
}