package com.mangaproject.data.websocket

import android.content.Context
import android.util.Log
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.datastore.UserPreferences
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException

class SocketService private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = "SocketService"
        private const val BASE_URL = "http://10.0.2.2:3000" // Pour émulateur
        // Pour appareil physique, utiliser l'IP de votre machine : "http://192.168.x.x:3000"
        
        @Volatile
        private var INSTANCE: SocketService? = null
        
        fun getInstance(context: Context): SocketService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SocketService(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private var socket: Socket? = null
    private val userPreferences = UserPreferences(context)
    private var isConnected = false
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private var currentRooms: List<String> = emptyList()
    
    // Callbacks pour les notifications
    var onNewChapter: ((mangaId: String, chapter: JSONObject) -> Unit)? = null
    var onChapterUpdated: ((mangaId: String, chapter: JSONObject) -> Unit)? = null
    var onMangaStatus: ((mangaId: String, status: JSONObject) -> Unit)? = null
    var onAIResponse: ((messageId: String, response: String) -> Unit)? = null
    
    /**
     * Connecte au serveur Socket.io et rejoint les rooms appropriées
     */
    suspend fun connect() {
        if (isConnected && socket?.connected() == true) {
            Log.d(TAG, "Déjà connecté")
            return
        }
        
        try {
            val token = userPreferences.token.first()
            if (token.isBlank()) {
                Log.e(TAG, "Token manquant, impossible de se connecter")
                return
            }
            
            // Obtenir les rooms à rejoindre
            val apiService = RetrofitInstance.authedApiService(token)
            val roomsResponse = apiService.getSocketRooms()
            currentRooms = roomsResponse.rooms
            
            Log.d(TAG, "Rooms à rejoindre: $currentRooms")
            
            // Configuration Socket.io
            val options = IO.Options().apply {
                this.forceNew = true
                this.reconnection = true
                this.reconnectionAttempts = maxReconnectAttempts
                this.reconnectionDelay = 1000
                this.reconnectionDelayMax = 5000
                this.timeout = 20000
            }
            
            socket = IO.socket(BASE_URL, options)
            
            // Écouter les événements de connexion
            socket?.on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "Connecté au serveur Socket.io")
                isConnected = true
                reconnectAttempts = 0
                
                // Rejoindre les rooms
                if (currentRooms.isNotEmpty()) {
                    socket?.emit("join", currentRooms)
                    Log.d(TAG, "Rejoint ${currentRooms.size} rooms")
                }
            }
            
            socket?.on(Socket.EVENT_DISCONNECT) { args ->
                Log.d(TAG, "Déconnecté: ${args?.getOrNull(0)}")
                isConnected = false
            }
            
            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e(TAG, "Erreur de connexion: ${args?.getOrNull(0)}")
                isConnected = false
                handleReconnection()
            }
            
            // Note: EVENT_RECONNECT n'existe pas dans cette version de Socket.io
            // La reconnexion est gérée automatiquement et déclenche EVENT_CONNECT
            
            // Écouter les notifications
            socket?.on("chapter:new") { args ->
                val data = args[0] as? JSONObject
                if (data != null) {
                    val mangaId = data.getString("mangaId")
                    val chapter = data.getJSONObject("chapter")
                    Log.d(TAG, "Nouveau chapitre pour manga: $mangaId")
                    onNewChapter?.invoke(mangaId, chapter)
                }
            }
            
            socket?.on("chapter:updated") { args ->
                val data = args[0] as? JSONObject
                if (data != null) {
                    val mangaId = data.getString("mangaId")
                    val chapter = data.getJSONObject("chapter")
                    Log.d(TAG, "Chapitre mis à jour pour manga: $mangaId")
                    onChapterUpdated?.invoke(mangaId, chapter)
                }
            }
            
            socket?.on("manga:status") { args ->
                val data = args[0] as? JSONObject
                if (data != null) {
                    val mangaId = data.getString("mangaId")
                    Log.d(TAG, "Statut manga mis à jour: $mangaId")
                    onMangaStatus?.invoke(mangaId, data)
                }
            }
            
            // Écouter les réponses IA
            socket?.on("ai:response") { args ->
                val data = args[0] as? JSONObject
                if (data != null) {
                    val messageId = data.optString("messageId", "")
                    val response = data.optString("response", "")
                    Log.d(TAG, "Réponse IA reçue pour message: $messageId")
                    onAIResponse?.invoke(messageId, response)
                }
            }
            
            // Connecter
            socket?.connect()
            
        } catch (e: URISyntaxException) {
            Log.e(TAG, "Erreur URI: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Erreur de connexion: ${e.message}")
        }
    }
    
    /**
     * Déconnecte du serveur
     */
    fun disconnect() {
        socket?.disconnect()
        socket = null
        isConnected = false
        Log.d(TAG, "Déconnecté du serveur")
    }
    
    /**
     * Rejoint une nouvelle room (ex: après avoir ajouté un favori)
     */
    fun joinRoom(room: String) {
        if (isConnected && socket?.connected() == true) {
            socket?.emit("join", listOf(room))
            Log.d(TAG, "Rejoint la room: $room")
        }
    }
    
    /**
     * Quitte une room (ex: après avoir retiré un favori)
     */
    fun leaveRoom(room: String) {
        if (isConnected && socket?.connected() == true) {
            socket?.emit("leave", listOf(room))
            Log.d(TAG, "Quitté la room: $room")
        }
    }
    
    /**
     * Gère la reconnexion automatique
     */
    private fun handleReconnection() {
        if (reconnectAttempts < maxReconnectAttempts) {
            reconnectAttempts++
            CoroutineScope(Dispatchers.IO).launch {
                kotlinx.coroutines.delay(2000L * reconnectAttempts)
                connect()
            }
        } else {
            Log.e(TAG, "Nombre maximum de tentatives de reconnexion atteint")
        }
    }
    
    /**
     * Vérifie si le socket est connecté
     */
    fun isConnected(): Boolean {
        return isConnected && socket?.connected() == true
    }
}

