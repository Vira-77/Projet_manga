package com.mangaproject.data.repository

import com.mangaproject.data.api.ApiService
import com.mangaproject.data.model.AddFavoriteRequest

class FavoriteRepository(private val api: ApiService) {
    
    suspend fun addFavorite(mangaId: String, source: String) {
        api.addFavorite(AddFavoriteRequest(mangaId, source))
    }
    
    suspend fun removeFavorite(mangaId: String) {
        api.removeFavorite(mangaId)
    }
}

