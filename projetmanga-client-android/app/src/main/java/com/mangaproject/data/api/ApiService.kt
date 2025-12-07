package com.mangaproject.data.api

import com.mangaproject.data.model.AddFavoriteRequest
import com.mangaproject.data.model.ChapterByIdResponse
import com.mangaproject.data.model.ChaptersByMangaResponse
import com.mangaproject.data.model.CreateMangaRequest
import com.mangaproject.data.model.FavoriteResponse
import com.mangaproject.data.model.GenreResponse
import com.mangaproject.data.model.GenresResponse
import com.mangaproject.data.model.Manga
import com.mangaproject.data.model.MangaByIdResponse
import com.mangaproject.data.model.MangaDetailResponse
import com.mangaproject.data.model.MangaResponse
import com.mangaproject.data.model.MangaUpdateRequest
import com.mangaproject.data.model.SearchResponse
import com.mangaproject.data.model.AiRequest
import com.mangaproject.data.model.AiResponse
import com.mangaproject.data.model.Chapter
import com.mangaproject.data.model.CreateChapterRequest
import com.mangaproject.data.model.Store
import com.mangaproject.data.model.StoresResponse
import com.mangaproject.data.model.TopMangaResponse
import com.mangaproject.data.model.UpdateUserRequest
import com.mangaproject.data.model.UploadProfilePictureResponse
import com.mangaproject.data.model.User
import com.mangaproject.data.model.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import com.mangaproject.data.model.ReadingHistoryResponse
import com.mangaproject.data.model.SingleReadingHistoryResponse
import com.mangaproject.data.model.UpdateReadingHistoryRequest
import com.mangaproject.data.model.SocketRoomsResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Communauté : mangas locaux
    @GET("/mangas")
    suspend fun getAllLocalMangas(): MangaResponse

    // Favoris
    @GET("/favorites")
    suspend fun getFavorites(): FavoriteResponse

    @GET("/favorites/{userId}")
    suspend fun getUserFavorites(@Path("userId") userId: String): FavoriteResponse

    @POST("/favorites")
    suspend fun addFavorite(@Body request: AddFavoriteRequest): FavoriteResponse

    @DELETE("/favorites/{mangaId}")
    suspend fun removeFavorite(@Path("mangaId") mangaId: String)

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


    @GET("/users/{id}")
    suspend fun getUser(
        @Path("id") id: String
    ): User

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

    @POST("/ai/chat")
    suspend fun chatWithAI(
        @Body body: AiRequest
    ): AiResponse
    // Historique de lecture
    @GET("/reading-history")
    suspend fun getReadingHistory(): ReadingHistoryResponse

    @PUT("/reading-history")
    suspend fun updateReadingHistory(
        @Body request: UpdateReadingHistoryRequest
    ): SingleReadingHistoryResponse

    @GET("/reading-history/{mangaId}")
    suspend fun getMangaReadingHistory(
        @Path("mangaId") mangaId: String
    ): SingleReadingHistoryResponse

    @DELETE("/reading-history/{mangaId}")
    suspend fun deleteReadingHistory(
        @Path("mangaId") mangaId: String
    )

    // Socket.io rooms
    @GET("/socket/rooms")
    suspend fun getSocketRooms(): SocketRoomsResponse
    @GET("/chapters/mangaDetail/{id}")
    suspend fun getAllChapterById(
        @Path("id") id: String
    ): Response<ChaptersByMangaResponse>

    @GET("/chapters/{id}")
    suspend fun getChapterById(
        @Path("id") id: String
    ): Response<ChapterByIdResponse>


    @Multipart
    @POST("users/profile/picture")
    suspend fun uploadProfilePicture(
        @Part profilePicture: MultipartBody.Part,
        @Header("Authorization") authorization: String?
    ): Response<UploadProfilePictureResponse>

    @DELETE("users/profile/picture")
    suspend fun deleteProfilePicture(): Response<UserResponse>

    // CHAPTERS

    // ✅ CHANGER Map en RequestBody
    @POST("chapters")
    suspend fun createChapter(
        @Body chapterData: RequestBody
    ): Chapter

    @Multipart
    @POST("chapters/{chapterId}/pages")
    suspend fun addPageToChapter(
        @Path("chapterId") chapterId: String,
        @Part image: MultipartBody.Part,
        @Part("numero") numero: RequestBody
    )

    @GET("chapters/manga/{mangaId}")
    suspend fun getChaptersByManga(
        @Path("mangaId") mangaId: String
    ): List<Chapter>

    @DELETE("chapters/{id}")
    suspend fun deleteChapter(
        @Path("id") chapterId: String
    )

}
