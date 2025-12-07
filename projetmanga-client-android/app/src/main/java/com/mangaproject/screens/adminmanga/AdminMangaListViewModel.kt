package com.mangaproject.screens.adminmanga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangaproject.data.model.Manga
import com.mangaproject.data.repository.MangaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminMangaListViewModel(
    private val mangaRepo: MangaRepository
) : ViewModel() {

    private val _mangas = MutableStateFlow<List<Manga>>(emptyList())
    val mangas: StateFlow<List<Manga>> = _mangas

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadMangas()
    }

    fun loadMangas() {
        viewModelScope.launch {
            try {
                _mangas.value = mangaRepo.getLocalMangas()
            } catch (e: Exception) {
                _error.value = "Erreur chargement mangas : ${e.message}"
            }
        }
    }

    fun deleteManga(id: String) {
        viewModelScope.launch {
            try {
                mangaRepo.deleteManga(id)
                loadMangas()
            } catch (e: Exception) {
                _error.value = "Erreur suppression : ${e.message}"
            }
        }
    }
}
