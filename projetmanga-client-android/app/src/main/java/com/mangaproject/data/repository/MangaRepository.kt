package com.mangaproject.data.repository

import com.mangaproject.data.api.ApiService
import com.mangaproject.data.model.Chapter
import com.mangaproject.data.model.ChapterByIdResponse
import com.mangaproject.data.model.CreateMangaRequest
import com.mangaproject.data.model.JikanGenre
import com.mangaproject.data.model.JikanManga
import com.mangaproject.data.model.Manga
import com.mangaproject.data.model.MangaUpdateRequest
import retrofit2.Response
import com.mangaproject.data.local.LocalStorage
import com.mangaproject.data.model.*

class MangaRepository(
    private val api: ApiService,
    private val local: LocalStorage?=null
) {

    suspend fun getLocalMangas(): List<Manga> {
        val response = api.getAllLocalMangas()
        return response.mangas
    }

    suspend fun getUserFavorites(userId: String): List<Manga> {
        return api.getUserFavorites(userId).favorites
    }

     suspend fun getFavorites(): List<Manga> {
        return api.getFavorites().favorites
    }

    suspend fun getTrends(forceRefresh: Boolean = false): List<JikanManga> {

        // cache ?
        val cached = local?.loadTrending() ?: emptyList()
        if (!forceRefresh && cached.isNotEmpty()) {
            return cached
        }

        // sinon appelle l’API
        val fresh = api.getTopMangas().top

        // save
        local?.saveTrending(fresh)

        return fresh
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
        genres: List<String>,
        chapters: List<String>
    ) = api.updateManga(
        id,
        MangaUpdateRequest(nom, description, image, date, auteur, genres, chapters)
    )

    suspend fun deleteManga(id: String) = api.deleteManga(id) // api.deleteMangaPost(id)

    suspend fun getChapterById(id: String): Response<ChapterByIdResponse> {
        return api.getChapterById(id)
    }

}
