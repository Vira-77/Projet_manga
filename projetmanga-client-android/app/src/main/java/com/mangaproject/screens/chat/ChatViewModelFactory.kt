package com.mangaproject.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mangaproject.data.datastore.ChatHistoryPreferences
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.repository.AiRepository

class ChatViewModelFactory(
    private val aiRepository: AiRepository,
    private val chatHistoryPreferences: ChatHistoryPreferences? = null,
    private val userPreferences: UserPreferences? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(aiRepository, chatHistoryPreferences, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
