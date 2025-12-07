package com.mangaproject.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangaproject.data.model.AiRequest
import com.mangaproject.data.model.ChatMessage
import com.mangaproject.data.repository.AiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // ajouter le message de l'utilisateur
        _messages.value = _messages.value + ChatMessage(
            text = text,
            fromUser = true
        )

        viewModelScope.launch {
            try {
                // si tu utilises DTO API
                val replyDto = aiRepository.chatWithAI(text)
                val replyText = replyDto.reply

                _messages.value = _messages.value + ChatMessage(
                    text = replyText,
                    fromUser = false
                )
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(
                    text = "Erreur : ${e.message}",
                    fromUser = false
                )
            }
        }
    }
}
