package com.mangaproject.screens.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangaproject.data.datastore.ChatHistoryPreferences
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.model.ChatMessage
import com.mangaproject.data.repository.AiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChatViewModel(
    private val aiRepository: AiRepository,
    private val chatHistoryPreferences: ChatHistoryPreferences? = null,
    private val userPreferences: UserPreferences? = null
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val pendingMessages = mutableMapOf<String, String>() // messageId -> userMessage

    init {
        // Charger l'historique au démarrage
        loadChatHistory()
    }

    private fun loadChatHistory() {
        viewModelScope.launch {
            chatHistoryPreferences?.let {
                val history = it.loadChatHistory()
                _messages.value = history
            }
        }
    }

    fun sendMessage(text: String, messageId: String? = null, userId: String? = null) {
        if (text.isBlank()) return

        val currentMessageId = messageId ?: System.currentTimeMillis().toString()
        pendingMessages[currentMessageId] = text

        // Ajouter le message de l'utilisateur
        val userMessage = ChatMessage(
            text = text,
            fromUser = true
        )
        _messages.value = _messages.value + userMessage
        saveChatHistory()

        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Récupérer le userId si non fourni
                val finalUserId = userId ?: userPreferences?.userId?.first() ?: ""
                
                // Envoyer la requête (le serveur répondra immédiatement avec status: processing)
                val replyDto = aiRepository.chatWithAI(text, finalUserId, currentMessageId)
                
                // Si le serveur renvoie une réponse immédiate (mode synchrone), l'utiliser
                if (replyDto.reply.isNotBlank() && replyDto.status != "processing") {
                    val aiMessage = ChatMessage(
                        text = replyDto.reply,
                        fromUser = false
                    )
                    _messages.value = _messages.value + aiMessage
                    saveChatHistory()
                    pendingMessages.remove(currentMessageId)
                    _isLoading.value = false
                }
                // Sinon, on attendra la notification WebSocket
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    text = "Erreur : ${e.message}",
                    fromUser = false
                )
                _messages.value = _messages.value + errorMessage
                saveChatHistory()
                pendingMessages.remove(currentMessageId)
                _isLoading.value = false
            }
        }
    }

    fun addAIResponse(messageId: String, response: String) {
        // Ajouter une réponse qui est arrivée via WebSocket
        viewModelScope.launch {
            // Vérifier si on a déjà ajouté cette réponse
            val lastMessage = _messages.value.lastOrNull()
            if (lastMessage?.text == response && !lastMessage.fromUser) {
                return@launch // Déjà ajoutée
            }

            val aiMessage = ChatMessage(
                text = response,
                fromUser = false
            )
            _messages.value = _messages.value + aiMessage
            saveChatHistory()
            pendingMessages.remove(messageId)
            _isLoading.value = false
        }
    }

    private fun saveChatHistory() {
        viewModelScope.launch {
            chatHistoryPreferences?.saveChatHistory(_messages.value)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            _messages.value = emptyList()
            chatHistoryPreferences?.clearChatHistory()
        }
    }
}
