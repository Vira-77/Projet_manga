package com.mangaproject.utils

import com.mangaproject.data.repository.ReadingHistoryRepository
import com.mangaproject.screens.user.HomeViewModel

/**
 * Helper pour mettre à jour l'historique de lecture
 * 
 * Utilisation:
 * - Quand un utilisateur ouvre un chapitre pour le lire, appeler updateHistoryWhenReadingChapter()
 * - Cette fonction doit être appelée depuis l'écran de lecture ou depuis MangaDetailScreen
 *   quand l'utilisateur sélectionne un chapitre
 */
object ReadingHistoryHelper {
    
    /**
     * Met à jour l'historique de lecture quand un utilisateur ouvre un chapitre
     * 
     * @param viewModel Le HomeViewModel qui contient la méthode updateReadingHistory
     * @param mangaId L'ID du manga (peut être local ou Jikan)
     * @param source "local" ou "jikan"
     * @param chapterId L'ID du chapitre (optionnel)
     * @param chapterNumber Le numéro du chapitre (optionnel mais recommandé)
     * @param mangaTitle Le titre du manga (optionnel, pour l'affichage)
     * @param mangaImageUrl L'URL de l'image du manga (optionnel, pour l'affichage)
     */
    fun updateHistoryWhenReadingChapter(
        viewModel: HomeViewModel,
        mangaId: String,
        source: String,
        chapterId: String? = null,
        chapterNumber: Int? = null,
        mangaTitle: String? = null,
        mangaImageUrl: String? = null
    ) {
        viewModel.updateReadingHistory(
            mangaId = mangaId,
            source = source,
            chapterId = chapterId,
            chapterNumber = chapterNumber,
            title = mangaTitle,
            imageUrl = mangaImageUrl
        )
    }
}

