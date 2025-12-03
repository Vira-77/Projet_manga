// GenreRepository.kt
package com.mangaproject.data.repository

import com.mangaproject.data.api.ApiService
import com.mangaproject.data.model.Genre

class GenreRepository(
    private val api: ApiService
) {

    // Récupère tous les genres locaux (Mongo)
    suspend fun getAllGenres(): List<Genre> {
        return api.getAllGenres().genres
    }

}
