package com.mangaproject.ui.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mangaproject.data.datastore.UserPreferences

class LogoutViewModelFactory(
    private val prefs: UserPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LogoutViewModel(prefs) as T
    }
}
