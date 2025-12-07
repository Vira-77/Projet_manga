package com.mangaproject.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mangaproject.data.repository.MangaRepository
import com.mangaproject.data.repository.StoreRepository
import com.mangaproject.data.repository.UserRepository

class AdminHomeViewModelFactory(
    private val userRepo: UserRepository,
    private val mangaRepo: MangaRepository,
    private val storeRepo: StoreRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminHomeViewModel(userRepo, mangaRepo, storeRepo) as T
    }
}
