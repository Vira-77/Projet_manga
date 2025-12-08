package com.mangaproject.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mangaproject.MainActivity
import com.mangaproject.R
import org.json.JSONObject

class NotificationService(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID = "manga_notifications"
        private const val CHANNEL_NAME = "Notifications Manga"
        private const val CHANNEL_DESCRIPTION = "Notifications pour les nouveaux chapitres et mises à jour"
        
        private const val NOTIFICATION_ID_NEW_CHAPTER = 1001
        private const val NOTIFICATION_ID_CHAPTER_UPDATED = 1002
        private const val NOTIFICATION_ID_MANGA_STATUS = 1003
        private const val NOTIFICATION_ID_AI_RESPONSE = 1004
    }
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannel()
    }
    
    /**
     * Crée le canal de notifications (requis pour Android 8.0+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Affiche une notification pour un nouveau chapitre
     */
    fun showNewChapterNotification(mangaId: String, chapter: JSONObject) {
        try {
            android.util.Log.d("NotificationService", "🔔 Affichage notification nouveau chapitre pour manga: $mangaId")
            val chapterTitle = chapter.optString("titre", "Nouveau chapitre")
            val mangaTitle = chapter.optString("mangaTitle", "Manga")
            android.util.Log.d("NotificationService", "📖 Titre: $mangaTitle - $chapterTitle")
            
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("mangaId", mangaId)
                putExtra("action", "open_chapter")
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Nouveau chapitre disponible")
                .setContentText("$mangaTitle - $chapterTitle")
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("Un nouveau chapitre est disponible pour $mangaTitle: $chapterTitle"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            
            notificationManager.notify(NOTIFICATION_ID_NEW_CHAPTER, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Affiche une notification pour une mise à jour de chapitre
     */
    fun showChapterUpdatedNotification(mangaId: String, chapter: JSONObject) {
        try {
            val chapterTitle = chapter.optString("titre", "Chapitre mis à jour")
            val mangaTitle = chapter.optString("mangaTitle", "Manga")
            
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("mangaId", mangaId)
                putExtra("action", "open_chapter")
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Chapitre mis à jour")
                .setContentText("$mangaTitle - $chapterTitle")
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("Le chapitre $chapterTitle de $mangaTitle a été mis à jour"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            
            notificationManager.notify(NOTIFICATION_ID_CHAPTER_UPDATED, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Affiche une notification pour un changement de statut de manga
     */
    fun showMangaStatusNotification(mangaId: String, status: JSONObject) {
        try {
            val statusText = status.optString("status", "Statut mis à jour")
            val mangaTitle = status.optString("mangaTitle", "Manga")
            
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("mangaId", mangaId)
                putExtra("action", "open_manga")
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Statut manga mis à jour")
                .setContentText("$mangaTitle - $statusText")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            
            notificationManager.notify(NOTIFICATION_ID_MANGA_STATUS, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Affiche une notification pour une réponse IA
     */
    fun showAIResponseNotification(response: String) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("action", "open_chat")
            }
            
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("🤖 Réponse de l'IA")
                .setContentText(response.take(100) + if (response.length > 100) "..." else "")
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(response))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            
            notificationManager.notify(NOTIFICATION_ID_AI_RESPONSE, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

