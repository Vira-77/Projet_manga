package com.mangaproject.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangaproject.data.model.Manga
import com.mangaproject.data.model.Store
import com.mangaproject.data.model.User
import com.mangaproject.data.repository.MangaRepository
import com.mangaproject.data.repository.StoreRepository
import com.mangaproject.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.filterNot

class AdminHomeViewModel(
    private val userRepo: UserRepository,
    private val mangaRepo: MangaRepository,
    private val storeRepo: StoreRepository
) : ViewModel() {

    // USERS
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    // MANGAS LOCAUX
    private val _mangas = MutableStateFlow<List<Manga>>(emptyList())
    val mangas: StateFlow<List<Manga>> = _mangas

    // STORES
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        refreshAll()
    }

    fun refreshAll() {
        refreshUsers()
        refreshMangas()
        refreshStores()
    }

    fun refreshUsers() {
        viewModelScope.launch {
            try {
                _users.value = userRepo.getUsers()
            } catch (e: Exception) {
                _error.value = "Erreur chargement utilisateurs : ${e.message}"
            }
        }
    }

    fun refreshMangas() {
        viewModelScope.launch {
            try {
                _mangas.value = mangaRepo.getLocalMangas()
            } catch (e: Exception) {
                _error.value = "Erreur chargement mangas : ${e.message}"
            }
        }
    }

    fun refreshStores() {
        viewModelScope.launch {
            try {
                _stores.value = storeRepo.getStores()
            } catch (e: Exception) {
                _error.value = "Erreur chargement magasins : ${e.message}"
            }
        }
    }

    fun changeUserRole(userId: String, newRole: String) {
        viewModelScope.launch {
            try {
                userRepo.updateUserRole(userId, newRole)
                refreshUsers()
            } catch (e: Exception) {
                _error.value = "Erreur changement de rôle : ${e.message}"
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepo.deleteUser(userId)
                _users.value = _users.value.filterNot { it.id == userId }
            } catch (e: Exception) {
                _error.value = "Erreur suppression utilisateur : ${e.message}"
            }
        }
    }

    fun deleteManga(mangaId: String) {
        viewModelScope.launch {
            try {
                mangaRepo.deleteManga(mangaId)
                _mangas.value = _mangas.value.filterNot { it.id == mangaId }
            } catch (e: Exception) {
                _error.value = "Erreur suppression manga : ${e.message}"
            }
        }
    }

    fun deleteStore(storeId: String) {
        viewModelScope.launch {
            try {
                storeRepo.deleteStore(storeId)
                _stores.value = _stores.value.filterNot { it.id == storeId }
            } catch (e: Exception) {
                _error.value = "Erreur suppression magasin : ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
