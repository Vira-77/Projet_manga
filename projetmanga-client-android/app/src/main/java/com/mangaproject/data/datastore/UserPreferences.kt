package com.mangaproject.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

val Context.userDataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val ROLE_KEY = stringPreferencesKey("role")
        val USER_ID = stringPreferencesKey("user_id")
    }

    suspend fun saveUser(token: String, role: String, id: String) {
        context.userDataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[ROLE_KEY] = role
            prefs[USER_ID] = id
        }
    }

    val role: Flow<String> = context.userDataStore.data.map { prefs ->
        prefs[ROLE_KEY] ?: ""
    }

    val token: Flow<String> = context.userDataStore.data.map { prefs ->
        prefs[TOKEN_KEY] ?: ""
    }
    val userId: Flow<String> = context.userDataStore.data.map { prefs ->
        prefs[USER_ID] ?: ""
    }

    suspend fun clear() {
        context.userDataStore.edit { it.clear() }
    }

    suspend fun logout() {
        context.userDataStore.edit { prefs ->
            prefs.clear()
        }
    }

}
