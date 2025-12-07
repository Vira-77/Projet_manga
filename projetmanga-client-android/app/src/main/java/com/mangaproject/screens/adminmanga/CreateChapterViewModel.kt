package com.mangaproject.screens.adminmanga

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangaproject.data.repository.ChapterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateChapterViewModel(
    private val chapterRepo: ChapterRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages

    private val _uploadProgress = MutableStateFlow(0)
    val uploadProgress: StateFlow<Int> = _uploadProgress

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    fun addImages(uris: List<Uri>) {
        _selectedImages.value = _selectedImages.value + uris
        Log.d("CreateChapterVM", "📸 Images ajoutées: ${uris.size}, Total: ${_selectedImages.value.size}")
    }

    fun removeImage(uri: Uri) {
        _selectedImages.value = _selectedImages.value - uri
        Log.d("CreateChapterVM", "🗑️ Image supprimée, Reste: ${_selectedImages.value.size}")
    }

    // ✅ Méthode comme saveProfile dans HomeViewModel
    fun createChapter(
        context: Context,
        mangaId: String,
        numero: Int,
        titre: String?
    ) {
        viewModelScope.launch {
            try {
                _uploadState.value = UploadState.Loading
                _loading.value = true
                _error.value = null
                _uploadProgress.value = 0

                Log.d("CreateChapterVM", "📖 Création chapitre $numero pour manga $mangaId")
                Log.d("CreateChapterVM", "📸 Nombre d'images: ${_selectedImages.value.size}")

                chapterRepo.createChapterWithImages(
                    context = context,
                    mangaId = mangaId,
                    numero = numero,
                    titre = titre,
                    imageUris = _selectedImages.value,
                    onProgress = { current, total ->
                        _uploadProgress.value = current
                        Log.d("CreateChapterVM", "📤 Upload: $current/$total")
                    }
                )

                Log.d("CreateChapterVM", "✅ Chapitre créé avec succès")
                _uploadState.value = UploadState.Success("Chapitre créé avec succès")
                _success.value = true

            } catch (e: Exception) {
                Log.e("CreateChapterVM", "❌ Erreur création chapitre", e)
                _uploadState.value = UploadState.Error(e.message ?: "Erreur inconnue")
                _error.value = e.message ?: "Erreur inconnue"
            } finally {
                _loading.value = false
            }
        }
    }

    fun resetUploadState() {
        _uploadState.value = UploadState.Idle
    }

    fun resetState() {
        _success.value = false
        _error.value = null
        _selectedImages.value = emptyList()
        _uploadProgress.value = 0
        _uploadState.value = UploadState.Idle
    }
}

// ✅ Réutiliser le même UploadState
sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val message: String) : UploadState()
    data class Error(val message: String) : UploadState()
}