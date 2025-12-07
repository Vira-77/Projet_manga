package com.mangaproject.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.repository.AuthRepository
import com.mangaproject.data.model.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val prefs: UserPreferences, private val repo: AuthRepository = AuthRepository()) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {

            _state.value = LoginState(isLoading = true)

            try {
                val result = repo.login(email, password)
                Log.d("TOKEN", "Saved token = ${result.token}")

                prefs.saveUser(
                    token = result.token,
                    role = result.user.role,
                    id = result.user.id
                )

                _state.value = LoginState(success = true)

            } catch (e: Exception) {
                _state.value = LoginState(error = e.message)
            }
        }
    }

}

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

