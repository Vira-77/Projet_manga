package com.mangaproject.screens.adminmanga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mangaproject.data.repository.MangaRepository

class AdminMangaListViewModelFactory(
    private val mangaRepo: MangaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminMangaListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminMangaListViewModel(mangaRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
