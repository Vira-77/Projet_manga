package com.mangaproject.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.model.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _state.value = RegisterState(isLoading = true)

                RetrofitInstance.api.register(
                    RegisterRequest(name, email, password, "utilisateur")
                )

                _state.value = RegisterState(success = true)

            } catch (e: Exception) {
                _state.value = RegisterState(error = e.message ?: "Erreur inconnue")
            }
        }
    }
}

data class RegisterState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)
