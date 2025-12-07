package com.mangaproject

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.notifications.NotificationService
import com.mangaproject.data.websocket.SocketService
import androidx.navigation.compose.rememberNavController
import com.mangaproject.ui.navigation.AppNav

class MainActivity : ComponentActivity() {

    private lateinit var socketService: SocketService
    private lateinit var notificationService: NotificationService

    // Launcher pour les permissions de géolocalisation
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Juste pour debug
            permissions.forEach {
                println(" Permission géolocalisation ${it.key} = ${it.value}")
            }
            // Après la géolocalisation, demander les notifications
            requestNotificationPermission()
        }

    // Launcher pour les permissions de notifications
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            println(" Permission notifications POST_NOTIFICATIONS = $isGranted")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialiser les services
        socketService = SocketService.getInstance(this)
        notificationService = NotificationService(this)

        // Configurer les callbacks de notifications
        setupNotificationCallbacks()

        // Demander d'abord les permissions de géolocalisation
        requestLocationPermission()

        setContent {
            val context = LocalContext.current
            val prefs = UserPreferences(context)
            val token by prefs.token.collectAsState(initial = "")
            val navController = rememberNavController()

            // Connecter au WebSocket si l'utilisateur est connecté
            LaunchedEffect(token) {
                if (token.isNotBlank()) {
                    socketService.connect()
                } else {
                    socketService.disconnect()
                }
            }

            AppNav(navController)
        }
    }

    private fun requestLocationPermission() {
        requestLocationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun requestNotificationPermission() {
        // POST_NOTIFICATIONS est requis uniquement pour Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun setupNotificationCallbacks() {
        socketService.onNewChapter = { mangaId, chapter ->
            notificationService.showNewChapterNotification(mangaId, chapter)
        }

        socketService.onChapterUpdated = { mangaId, chapter ->
            notificationService.showChapterUpdatedNotification(mangaId, chapter)
        }

        socketService.onMangaStatus = { mangaId, status ->
            notificationService.showMangaStatusNotification(mangaId, status)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socketService.disconnect()
    }
}