package com.mangaproject.data.api

import com.mangaproject.data.model.CreateMangaRequest
import com.mangaproject.data.model.FavoriteResponse
import com.mangaproject.data.model.Genre
import com.mangaproject.data.model.GenreResponse
import com.mangaproject.data.model.GenresResponse
import com.mangaproject.data.model.Manga
import com.mangaproject.data.model.MangaByIdResponse
import com.mangaproject.data.model.MangaDetailResponse
import com.mangaproject.data.model.MangaResponse
import com.mangaproject.data.model.MangaUpdateRequest
import com.mangaproject.data.model.SearchResponse
import com.mangaproject.data.model.Store
import com.mangaproject.data.model.StoresResponse
import com.mangaproject.data.model.TopMangaResponse
import com.mangaproject.data.model.UpdateUserRequest
import com.mangaproject.data.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Communauté : mangas locaux
    @GET("/mangas")
    suspend fun getAllLocalMangas(): MangaResponse

    // Favoris
    @GET("/favorites/{userId}")
    suspend fun getUserFavorites(@Path("userId") userId: String): FavoriteResponse

    // Magasins
    @GET("/stores")
    suspend fun getStores(): StoresResponse

    // Tendances Jikan
    @GET("/external/top")
    suspend fun getTopMangas(): TopMangaResponse

    @GET("/external/search")
    suspend fun searchManga(@Query("q") query: String): SearchResponse

    @GET("/external/genres")
    suspend fun getJikanGenres(): GenresResponse

    @GET("/external/genres/{id}/manga")
    suspend fun getMangaByGenre(@Path("id") id: Int): SearchResponse

    @GET("/external/manga/{id}")
    suspend fun getMangaById(@Path("id") id: String): MangaByIdResponse

    @POST("/mangas")
    suspend fun createManga(
        @Body request: CreateMangaRequest
    )

    @GET("/genres")
    suspend fun getAllGenres(): GenreResponse

    @GET("/mangas/{id}")
    suspend fun getMangaLocalById(
        @Path("id") id: String
    ): MangaDetailResponse

    @PUT("/mangas/{id}")
    suspend fun updateManga(
        @Path("id") id: String,
        @Body body: MangaUpdateRequest
    ): Manga

    @POST("/mangas/{id}/delete")
    suspend fun deleteMangaPost(
        @Path("id") id: String
    )

    @HTTP(method = "DELETE", path = "/mangas/{id}", hasBody = true)
    suspend fun deleteManga(@Path("id") id: String)

    @GET("/users")
    suspend fun getAllUsers(): List<User>

    @PUT("/users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body body: UpdateUserRequest
    ): User

    @DELETE("/users/{id}")
    suspend fun deleteUser(
        @Path("id") id: String
    )

    @DELETE("/stores/{id}")
    suspend fun deleteStore(
        @Path("id") id: String
    )




}
