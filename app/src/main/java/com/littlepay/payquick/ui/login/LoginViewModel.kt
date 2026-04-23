package com.littlepay.payquick.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlepay.payquick.domain.model.User
import com.littlepay.payquick.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            authRepository.login(email, password)
                .onSuccess { (user, token) ->
                    _loginState.value = LoginUiState.Success(user, token)
                }
                .onFailure { error ->
                    _loginState.value = LoginUiState.Error(error.message ?: "Login failed")
                }
        }
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: User, val token: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
