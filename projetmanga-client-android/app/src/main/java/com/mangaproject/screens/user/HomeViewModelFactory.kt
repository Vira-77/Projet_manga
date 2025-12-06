package com.mangaproject.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.repository.MangaRepository
import com.mangaproject.data.repository.ReadingHistoryRepository
import com.mangaproject.data.repository.StoreRepository

class HomeViewModelFactory(
    private val mangaRepo: MangaRepository,
    private val storeRepo: StoreRepository,
    private val prefs: UserPreferences,
    private val readingHistoryRepo: ReadingHistoryRepository? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(mangaRepo, storeRepo, prefs, readingHistoryRepo) as T
    }
}
