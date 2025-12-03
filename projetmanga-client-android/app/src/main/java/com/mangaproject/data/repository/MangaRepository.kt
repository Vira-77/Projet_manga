package com.mangaproject.data.repository

import com.mangaproject.data.api.ApiService
import com.mangaproject.data.model.CreateMangaRequest
import com.mangaproject.data.model.JikanGenre
import com.mangaproject.data.model.JikanManga
import com.mangaproject.data.model.Manga
import com.mangaproject.data.model.MangaUpdateRequest

class MangaRepository(private val api: ApiService) {

    suspend fun getLocalMangas(): List<Manga> {
        val response = api.getAllLocalMangas()
        return response.mangas
    }

    suspend fun getUserFavorites(userId: String): List<Manga> {
        return api.getUserFavorites(userId).favorites
    }

    suspend fun getTrends(): List<JikanManga> {
        return api.getTopMangas().top
    }

    suspend fun searchMangaJikan(query: String): List<JikanManga> {
        return api.searchManga(query).results
    }

    suspend fun getJikanGenres(): List<JikanGenre> {
        return api.getJikanGenres().genres
    }

    suspend fun searchByGenreJikan(genreId: Int): List<JikanManga> {
        return api.getMangaByGenre(genreId).results
    }

    suspend fun createManga(
        nom: String,
        description: String?,
        dateDeSortie: String?,
        urlImage: String?,
        auteur: String,
        genres: List<String>
    ) {
        val body = CreateMangaRequest(
            nom = nom,
            description = description,
            dateDeSortie = dateDeSortie,
            urlImage = urlImage,
            auteur = auteur,
            genres = genres
        )

        api.createManga(body)
    }

    suspend fun getMangaById(id: String): Manga {
        return api.getMangaLocalById(id).manga
    }

    suspend fun updateManga(
        id: String,
        nom: String,
        description: String?,
        image: String?,
        date: String?,
        auteur: String,
        genres: List<String>
    ) = api.updateManga(
        id,
        MangaUpdateRequest(nom, description, image, date, auteur, genres)
    )

    suspend fun deleteManga(id: String) = api.deleteManga(id) // api.deleteMangaPost(id)



}
