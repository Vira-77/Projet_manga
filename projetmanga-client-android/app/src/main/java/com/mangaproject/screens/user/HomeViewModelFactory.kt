package com.mangaproject.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.repository.MangaRepository
import com.mangaproject.data.repository.StoreRepository
import com.mangaproject.data.repository.UserRepository

class HomeViewModelFactory(
    private val mangaRepo: MangaRepository,
    private val storeRepo: StoreRepository,
    private val userRepo: UserRepository,
    private val prefs: UserPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(mangaRepo, storeRepo,userRepo,prefs) as T
    }
}
