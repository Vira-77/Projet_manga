package com.mangaproject.screens.adminmanga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangaproject.data.model.Genre
import com.mangaproject.data.model.Manga
import com.mangaproject.data.repository.GenreRepository
import com.mangaproject.data.repository.MangaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditMangaViewModel(
    private val mangaRepo: MangaRepository,
    private val genreRepo: GenreRepository
) : ViewModel() {

    private val _manga = MutableStateFlow<Manga?>(null)
    val manga: StateFlow<Manga?> = _manga

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess


    fun load(mangaId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true

                _manga.value = mangaRepo.getMangaById(mangaId)
                _genres.value = genreRepo.getAllGenres()

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }


    fun update(
        id: String,
        nom: String,
        auteur: String,
        description: String?,
        date: String?,
        image: String?,
        genresIds: List<String>
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                _success.value = false

                mangaRepo.updateManga(
                    id = id,
                    nom = nom,
                    auteur = auteur,
                    description = description,
                    date = date,
                    image = image,
                    genres = genresIds
                )

                _success.value = true

            } catch (e: Exception) {
                _error.value = "Erreur : ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                _deleteSuccess.value = false

                mangaRepo.deleteManga(id)

                _deleteSuccess.value = true
            } catch (e: Exception) {
                _error.value = "Erreur : ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun resetState() {
        _success.value = false
        _error.value = null
    }
}
