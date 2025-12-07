package com.mangaproject.screens.adminmanga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mangaproject.data.repository.GenreRepository
import com.mangaproject.data.repository.MangaRepository

class EditMangaViewModelFactory(
    private val mangaRepo: MangaRepository,
    private val genreRepo: GenreRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditMangaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditMangaViewModel(mangaRepo, genreRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
