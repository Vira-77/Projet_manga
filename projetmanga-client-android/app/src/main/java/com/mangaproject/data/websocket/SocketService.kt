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
            try {
                val apiService = RetrofitInstance.authedApiService(token)
                Log.d(TAG, "Récupération des rooms depuis le serveur...")
                val roomsResponse = apiService.getSocketRooms()
                currentRooms = roomsResponse.rooms
                
                Log.d(TAG, "Rooms à rejoindre: $currentRooms (${currentRooms.size} rooms)")
                if (currentRooms.isEmpty()) {
                    Log.w(TAG, "⚠️ Aucune room trouvée! Vérifiez que vous avez des favoris avec source='local'")
                }
            } catch (e: retrofit2.HttpException) {
                when (e.code()) {
                    404 -> Log.e(TAG, "❌ Route /socket/rooms non trouvée (404). Vérifiez que le serveur a été redémarré.")
                    401 -> Log.e(TAG, "❌ Token invalide ou expiré (401). Reconnectez-vous.")
                    else -> Log.e(TAG, "❌ Erreur HTTP ${e.code()}: ${e.message()}")
                }
                currentRooms = emptyList()
            } catch (e: Exception) {
                Log.e(TAG, "Erreur récupération rooms: ${e.message}", e)
                currentRooms = emptyList()
            }
            
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
                try {
                    Log.d(TAG, "🔔 Événement 'chapter:new' reçu!")
                    val data = args[0] as? JSONObject
                    if (data != null) {
                        val mangaId = data.getString("mangaId")
                        val chapter = data.getJSONObject("chapter")
                        Log.d(TAG, "✅ Nouveau chapitre pour manga: $mangaId")
                        Log.d(TAG, "📖 Détails chapitre: ${chapter.toString()}")
                        onNewChapter?.invoke(mangaId, chapter)
                        Log.d(TAG, "✅ Callback onNewChapter appelé")
                    } else {
                        Log.e(TAG, "❌ Données invalides dans chapter:new")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Erreur traitement notification chapter:new: ${e.message}", e)
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
     * Recharge les rooms depuis le serveur et rejoint les nouvelles rooms
     */
    suspend fun reloadRooms() {
        try {
            val token = userPreferences.token.first()
            if (token.isBlank()) {
                Log.e(TAG, "Token manquant, impossible de recharger les rooms")
                return
            }
            
            val apiService = RetrofitInstance.authedApiService(token)
            val roomsResponse = apiService.getSocketRooms()
            val newRooms = roomsResponse.rooms
            
            Log.d(TAG, "Rooms rechargées: $newRooms (anciennes: $currentRooms)")
            
            // Rejoindre les nouvelles rooms
            if (isConnected && socket?.connected() == true) {
                val roomsToJoin = newRooms.filter { it !in currentRooms }
                if (roomsToJoin.isNotEmpty()) {
                    socket?.emit("join", roomsToJoin)
                    Log.d(TAG, "Rejoint ${roomsToJoin.size} nouvelles rooms: $roomsToJoin")
                }
                
                // Quitter les rooms qui ne sont plus dans la liste
                val roomsToLeave = currentRooms.filter { it !in newRooms }
                if (roomsToLeave.isNotEmpty()) {
                    socket?.emit("leave", roomsToLeave)
                    Log.d(TAG, "Quitté ${roomsToLeave.size} rooms: $roomsToLeave")
                }
            }
            
            currentRooms = newRooms
        } catch (e: Exception) {
            Log.e(TAG, "Erreur rechargement rooms: ${e.message}")
        }
    }
    
    /**
     * Rejoint une nouvelle room (ex: après avoir ajouté un favori)
     */
    fun joinRoom(room: String) {
        if (isConnected && socket?.connected() == true) {
            socket?.emit("join", listOf(room))
            Log.d(TAG, "Rejoint la room: $room")
            if (room !in currentRooms) {
                currentRooms = currentRooms + room
            }
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

