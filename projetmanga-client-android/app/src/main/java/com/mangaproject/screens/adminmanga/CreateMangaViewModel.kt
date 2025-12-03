package com.mangaproject.screens.adminmanga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.model.Genre
import com.mangaproject.data.repository.GenreRepository
import com.mangaproject.data.repository.MangaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateMangaViewModel(
    private val mangaRepo: MangaRepository,
    private val genreRepo: GenreRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadGenres()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            try {
                _genres.value = genreRepo.getAllGenres()
            } catch (e: Exception) {
                _error.value = "Erreur chargement genres : ${e.message}"
            }
        }
    }

    fun createManga(
        nom: String,
        description: String?,
        dateDeSortie: String?,
        urlImage: String?,
        auteur: String,
        genresIds: List<String>
    ) {
        if (nom.isBlank() || auteur.isBlank()) {
            _error.value = "Nom et auteur obligatoires"
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _success.value = false

                mangaRepo.createManga(
                    nom = nom,
                    description = description,
                    dateDeSortie = dateDeSortie,
                    urlImage = urlImage,
                    auteur = auteur,
                    genres = genresIds
                )

                _success.value = true

            } catch (e: Exception) {
                _error.value = "Erreur : ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetState() {
        _success.value = false
        _error.value = null
    }
}
