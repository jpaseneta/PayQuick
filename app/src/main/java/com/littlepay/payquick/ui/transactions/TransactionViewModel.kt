package com.littlepay.payquick.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlepay.payquick.data.repository.TokenManager
import com.littlepay.payquick.domain.model.AuthorizationException
import com.littlepay.payquick.domain.model.Transaction
import com.littlepay.payquick.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Loading)
    val uiState: StateFlow<TransactionUiState> = _uiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private var currentPage = 1
    private val allTransactions = mutableListOf<Transaction>()
    private var isLastPage = false

    init {
        loadTransactions(token = getAuthToken())
    }

    fun getAuthToken() = tokenManager.accessToken

    fun loadTransactions(token: String, isRefresh: Boolean = false) {
        if (isRefresh) {
            _isRefreshing.value = true
            currentPage = 1
            isLastPage = false
        }

        if (isLastPage) return

        if (!isRefresh && allTransactions.isNotEmpty()) { // flag to display loading more progress
            _isLoadingMore.value = true
        }

        viewModelScope.launch {
            if (allTransactions.isEmpty() && !isRefresh) { //initial loading state
                _uiState.value = TransactionUiState.Loading
            }

            transactionRepository.getTransactions(token, currentPage)
                .onSuccess { transactionsList ->
                    if (isRefresh) {
                        allTransactions.clear()
                        _isRefreshing.value = false
                    }
                    _isLoadingMore.value = false

                    allTransactions.addAll(transactionsList.transactions)

                    if (transactionsList.pagination.currentPage >= transactionsList.pagination.totalPages) {
                        isLastPage = true
                    } else {
                        currentPage++
                    }
                    _uiState.value = TransactionUiState.Success(allTransactions.toList())
                }
                .onFailure { error ->
                    _isRefreshing.value = false
                    _isLoadingMore.value = false
                    when (error) { //created an AuthError to handle token expiration
                        is AuthorizationException -> _uiState.value =
                            TransactionUiState.AuthError(error.message)

                        else -> _uiState.value =
                            TransactionUiState.Error(error.message ?: "An unknown error occurred")
                    }
                }
        }
    }

    fun logout() {
        tokenManager.clearTokens()
    }
}

sealed class TransactionUiState {
    object Loading : TransactionUiState()
    data class Success(val transactions: List<Transaction>) : TransactionUiState()
    data class Error(val message: String) : TransactionUiState()
    data class AuthError(val message: String) : TransactionUiState()
}
