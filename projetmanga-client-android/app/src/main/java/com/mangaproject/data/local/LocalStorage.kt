package com.mangaproject.data.local

import com.mangaproject.data.model.JikanManga

interface LocalStorage {
    suspend fun saveTrending(mangas: List<JikanManga>)
    suspend fun loadTrending(): List<JikanManga>
}
