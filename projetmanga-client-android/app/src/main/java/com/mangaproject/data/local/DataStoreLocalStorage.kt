package com.mangaproject.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.mangaproject.data.model.JikanManga
import kotlinx.coroutines.flow.first

private val Context.trendingDataStore: DataStore<List<JikanManga>> by dataStore(
    fileName = "trending_mangas.json",
    serializer = TrendingSerializer
)

class DataStoreLocalStorage(private val context: Context) : LocalStorage {

    override suspend fun saveTrending(mangas: List<JikanManga>) {
        context.trendingDataStore.updateData { mangas }
    }

    override suspend fun loadTrending(): List<JikanManga> {
        return context.trendingDataStore.data.first()
    }
}
