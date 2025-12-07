package com.mangaproject.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mangaproject.data.model.ChatMessage
import kotlinx.coroutines.flow.first

val Context.chatDataStore by preferencesDataStore("chat_history")

class ChatHistoryPreferences(private val context: Context) {

    companion object {
        val CHAT_HISTORY_KEY = stringPreferencesKey("chat_history")
        private val gson = Gson()
    }

    suspend fun saveChatHistory(messages: List<ChatMessage>) {
        context.chatDataStore.edit { prefs ->
            val json = gson.toJson(messages)
            prefs[CHAT_HISTORY_KEY] = json
        }
    }

    suspend fun loadChatHistory(): List<ChatMessage> {
        return try {
            val json = context.chatDataStore.data.first()[CHAT_HISTORY_KEY] ?: return emptyList()
            val type = object : TypeToken<List<ChatMessage>>() {}.type
            gson.fromJson<List<ChatMessage>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun clearChatHistory() {
        context.chatDataStore.edit { prefs ->
            prefs.remove(CHAT_HISTORY_KEY)
        }
    }
}
