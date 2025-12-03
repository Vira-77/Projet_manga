package com.mangaproject.ui.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangaproject.data.datastore.UserPreferences
import kotlinx.coroutines.launch

class LogoutViewModel(private val prefs: UserPreferences) : ViewModel() {

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            prefs.logout()
            onDone()
        }
    }
}
