package com.mangaproject.screens.adminmanga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mangaproject.data.repository.ChapterRepository

class CreateChapterViewModelFactory(
    private val chapterRepo: ChapterRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateChapterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateChapterViewModel(chapterRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}