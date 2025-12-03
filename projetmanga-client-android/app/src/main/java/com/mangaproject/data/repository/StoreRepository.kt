package com.mangaproject.data.repository

import com.mangaproject.data.api.ApiService
import com.mangaproject.data.model.Store

class StoreRepository(private val api: ApiService) {

    suspend fun getStores(): List<Store> =
        api.getStores().stores

    suspend fun deleteStore(id: String) =
        api.deleteStore(id)
}
