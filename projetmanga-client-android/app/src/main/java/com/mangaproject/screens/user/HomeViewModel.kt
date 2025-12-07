package com.mangaproject.screens.user

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.local.DataStoreLocalStorage
import com.mangaproject.data.model.*
import com.mangaproject.data.repository.FavoriteRepository
import com.mangaproject.data.repository.MangaRepository
import com.mangaproject.data.repository.ReadingHistoryRepository
import com.mangaproject.data.repository.StoreRepository
import com.mangaproject.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val mangaRepo: MangaRepository,
    private val storeRepo: StoreRepository,
    private val userRepo: UserRepository,
    private val prefs: UserPreferences,
    private val readingHistoryRepo: ReadingHistoryRepository? = null
) : ViewModel() {

    // FAVORIS
    private val _favorites = MutableStateFlow<List<Manga>>(emptyList())
    val favorites: StateFlow<List<Manga>> = _favorites

    // TENDANCES
    private val _trends = MutableStateFlow<List<JikanManga>>(emptyList())
    val trends: StateFlow<List<JikanManga>> = _trends

    // MANGAS LOCAUX
    private val _localMangas = MutableStateFlow<List<Manga>>(emptyList())
    val localMangas: StateFlow<List<Manga>> = _localMangas

    // MAGASINS
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores

    // RECHERCHE PAR NOM (Jikan)
    private val _searchResults = MutableStateFlow<List<JikanManga>>(emptyList())
    val searchResults: StateFlow<List<JikanManga>> = _searchResults

    // LISTE DES GENRES JIKAN
    private val _genres = MutableStateFlow<List<JikanGenre>>(emptyList())
    val genres: StateFlow<List<JikanGenre>> = _genres

    // HISTORIQUE DE LECTURE
    private val _readingHistory = MutableStateFlow<List<ReadingHistory>>(emptyList())
    val readingHistory: StateFlow<List<ReadingHistory>> = _readingHistory

    // LOADING
    private val _searchLoading = MutableStateFlow(false)
    val searchLoading = _searchLoading.asStateFlow()

    // USER INFO
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _selectedProfilePictureUri = MutableStateFlow<Uri?>(null)
    val selectedProfilePictureUri: StateFlow<Uri?> = _selectedProfilePictureUri


    // manga

    private val _selectedManga = MutableStateFlow<Manga?>(null)
    val selectedManga = _selectedManga.asStateFlow()

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()



    fun updateSelectedProfilePictureUri(uri: Uri?) {
        _selectedProfilePictureUri.value = uri
    }

    init {
        refresh()
        loadGenres()
        loadUser()
        loadReadingHistory()
    }

    // Charger l'historique de lecture
    fun loadReadingHistory() {
        if (readingHistoryRepo == null) return
        viewModelScope.launch {
            try {
                _readingHistory.value = readingHistoryRepo.getReadingHistory()
            } catch (e: Exception) {
                println("⚠️ Error loadReadingHistory(): ${e.message}")
            }
        }
    }

    // Mettre à jour l'historique de lecture
    fun updateReadingHistory(
        mangaId: String,
        source: String,
        chapterId: String?,
        chapterNumber: Int?,
        title: String?,
        imageUrl: String?
    ) {
        if (readingHistoryRepo == null) return
        viewModelScope.launch {
            try {
                readingHistoryRepo.updateReadingHistory(
                    mangaId, source, chapterId, chapterNumber, title, imageUrl
                )
                loadReadingHistory() // Recharger après mise à jour
            } catch (e: Exception) {
                println("⚠️ Error updateReadingHistory(): ${e.message}")
            }
        }
    }

    // CHARGER TOUS LES GENRES (Jikan)
    fun loadGenres() {
        viewModelScope.launch {
            try {
                _genres.value = mangaRepo.getJikanGenres()
            } catch (e: Exception) {
                println("⚠️ Error loadGenres(): ${e.message}")
            }
        }
    }

    // Recherche par titre
    fun searchByTitle(query: String) {
        if (query.trim().isEmpty()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _searchLoading.value = true
            try {
                val result = mangaRepo.searchMangaJikan(query)
                _searchResults.value = result
            } catch (e: Exception) {
                println("⚠️ Error searchByTitle(): ${e.message}")
            }
            _searchLoading.value = false
        }
    }

    // Recherche par genre (ID Jikan)
    fun searchByGenre(genreId: Int) {
        viewModelScope.launch {
            _searchLoading.value = true
            try {
                _searchResults.value = mangaRepo.searchByGenreJikan(genreId)
            } catch (e: Exception) {
                println("⚠️ Error searchByGenre(): ${e.message}")
            }
            _searchLoading.value = false
        }
    }

    // obtenir information sur l'utilisateur connecté
    fun loadUser() {
        viewModelScope.launch {
            try {

                val userId = prefs.userId.firstOrNull() // get userId from DataStore
                if (!userId.isNullOrEmpty()) {
                    val userData = userRepo.getUser(userId) // call repository suspend function
                    _user.value = userData
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loadUser(): ${e.message}")
            }
        }
    }

    fun updateUser(
        id: String,
        name: String?,
        address: String?,
        bio: String?,
        profilePicture: String?
    ) {
        viewModelScope.launch {
            userRepo.updateUser(
                id = id,
                name = name,
                address = address,
                bio = bio,
                profilePicture = profilePicture
            )
            loadUser()
        }
    }

    // RAFFRAICHIR TOUT
    fun refresh() {
        viewModelScope.launch {
            val token = prefs.token.firstOrNull()

            // Charger les favoris avec l'API authentifiée
            if (token != null && token.isNotBlank()) {
                try {
                    val authedApi = RetrofitInstance.authedApiService(token)
                    val authedMangaRepo = MangaRepository(authedApi)
                    _favorites.value = authedMangaRepo.getFavorites()
                    Log.d("HomeViewModel", "Favoris chargés: ${_favorites.value.size}")
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Erreur récupération favoris: ${e.message}", e)
                    // Fallback : utiliser userId si disponible
                    try {
                        val userId = prefs.userId.firstOrNull()
                        if (userId != null) {
                            val authedApi = RetrofitInstance.authedApiService(token)
                            val authedMangaRepo = MangaRepository(authedApi)
                            _favorites.value = authedMangaRepo.getUserFavorites(userId)
                        }
                    } catch (e2: Exception) {
                        Log.e("HomeViewModel", "Erreur récupération favoris (fallback): ${e2.message}", e2)
                    }
                }
            } else {
                Log.w("HomeViewModel", "Token non disponible pour charger les favoris")
            }

            try { _trends.value = mangaRepo.getTrends() } catch (_: Exception) {}
            try {
                try {
                    val list = mangaRepo.getLocalMangas()
                    println("📘 LOCAL MANGAS = ${list.size}")
                    list.forEach { println(" - ${it.nom}") }
                    _localMangas.value = list
                } catch (e: Exception) {
                    println("❌ ERROR localMangas = ${e.message}")
                }


            } catch (_: Exception) {}
            try { _stores.value = storeRepo.getStores() } catch (_: Exception) {}
        }
    }

    fun loadStores() {
        viewModelScope.launch {
            try {
                _stores.value = storeRepo.getStores()
            } catch (e: Exception) {
                println(" ERROR loadStores(): ${e.message}")
            }
        }
    }


    // Ajouter un favori
    fun addFavorite(mangaId: String, source: String = "local") {
        viewModelScope.launch {
            try {
                val token = prefs.token.firstOrNull()
                if (token != null && token.isNotBlank()) {
                    val authedApi = RetrofitInstance.authedApiService(token)
                    val favoriteRepo = FavoriteRepository(authedApi)
                    favoriteRepo.addFavorite(mangaId, source)
                    // Recharger les favoris après ajout
                    refresh()
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erreur ajout favori: ${e.message}", e)
            }
        }
    }

    // Retirer un favori
    fun removeFavorite(mangaId: String) {
        viewModelScope.launch {
            try {
                val token = prefs.token.firstOrNull()
                if (token != null && token.isNotBlank()) {
                    val authedApi = RetrofitInstance.authedApiService(token)
                    val favoriteRepo = FavoriteRepository(authedApi)
                    favoriteRepo.removeFavorite(mangaId)
                    // Recharger les favoris après suppression
                    refresh()
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erreur suppression favori: ${e.message}", e)
            }
        }
    }

    // Vérifier si un manga est en favori
    fun isFavorite(mangaId: String): Boolean {
        return _favorites.value.any {
            it.id == mangaId ||
            (it.jikanId != null && it.jikanId.toString() == mangaId)
        }
    }

    fun uploadProfilePicture(context: Context) {
        val uri = _selectedProfilePictureUri.value
        if (uri == null) {
            _uploadState.value = UploadState.Error("Aucune image sélectionnée")
            return
        }

        viewModelScope.launch {
            _uploadState.value = UploadState.Loading

            val result = userRepo.uploadProfilePicture(context, uri)

            result.onSuccess { updatedUser ->
                _user.value = updatedUser
                _selectedProfilePictureUri.value = null // Réinitialiser la sélection
                _uploadState.value = UploadState.Success("Photo de profil mise à jour")

                // Recharger le profil complet
                loadUser()
            }.onFailure { error ->
                _uploadState.value = UploadState.Error(error.message ?: "Erreur inconnue")
                Log.e("HomeViewModel", "Error uploadProfilePicture(): ${error.message}")
            }
        }
    }

    fun deleteProfilePicture() {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading

            val result = userRepo.deleteProfilePicture()

            result.onSuccess { updatedUser ->
                _user.value = updatedUser
                _uploadState.value = UploadState.Success("Photo de profil supprimée")
                loadUser()
            }.onFailure { error ->
                _uploadState.value = UploadState.Error(error.message ?: "Erreur inconnue")
                Log.e("HomeViewModel", "Error deleteProfilePicture(): ${error.message}")
            }
        }
    }

    fun resetUploadState() {
        _uploadState.value = UploadState.Idle
    }

    // Dans HomeViewModel, ajouter une méthode unifiée
    fun saveProfile(
        context: Context,
        userId: String,
        name: String?,
        address: String?,
        bio: String?,
        hasImageToUpload: Boolean
    ) {
        viewModelScope.launch {
            try {
                _uploadState.value = UploadState.Loading

                // 1. Upload image si nécessaire
                if (hasImageToUpload && _selectedProfilePictureUri.value != null) {
                    Log.d("HomeViewModel", "📸 Upload image...")
                    val uploadResult = userRepo.uploadProfilePicture(
                        context,
                        _selectedProfilePictureUri.value!!
                    )

                    if (uploadResult.isFailure) {
                        _uploadState.value = UploadState.Error(
                            uploadResult.exceptionOrNull()?.message ?: "Erreur upload"
                        )
                        return@launch
                    }

                    _selectedProfilePictureUri.value = null
                    Log.d("HomeViewModel", "✅ Image uploadée")
                }

                // 2. Mise à jour des autres champs
                Log.d("HomeViewModel", "💾 Mise à jour profil...")
                userRepo.updateUser(
                    id = userId,
                    name = name,
                    address = address,
                    bio = bio,
                    profilePicture = null
                )

                // 3. Recharger
                loadUser()

                _uploadState.value = UploadState.Success("Profil mis à jour avec succès")

            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ Erreur saveProfile", e)
                _uploadState.value = UploadState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }
}


sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val message: String) : UploadState()
    data class Error(val message: String) : UploadState()
}
