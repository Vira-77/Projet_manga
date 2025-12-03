package com.mangaproject.screens.user

import android.util.Log
import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.model.*
import com.mangaproject.data.repository.MangaRepository
import com.mangaproject.data.repository.StoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val mangaRepo: MangaRepository,
    private val storeRepo: StoreRepository,
    private val prefs: UserPreferences
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

    // LOADING
    private val _searchLoading = MutableStateFlow(false)
    val searchLoading = _searchLoading.asStateFlow()

    init {
        refresh()
        loadGenres()
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

    // RAFFRAICHIR TOUT
    fun refresh() {
        viewModelScope.launch {
            val userId = prefs.userId.firstOrNull()

            try { if (userId != null) _favorites.value = mangaRepo.getUserFavorites(userId) } catch (_: Exception) {}
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
}
