package com.mangaproject.screens.adminmanga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.repository.GenreRepository
import com.mangaproject.data.repository.MangaRepository

class CreateMangaViewModelFactory(
    private val mangaRepo: MangaRepository,
    private val genreRepo: GenreRepository,
    private val prefs: UserPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateMangaViewModel::class.java)) {
            return CreateMangaViewModel(mangaRepo, genreRepo, prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
