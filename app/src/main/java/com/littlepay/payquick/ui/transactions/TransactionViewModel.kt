package com.littlepay.payquick.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlepay.payquick.domain.model.Transaction
import com.littlepay.payquick.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Loading)
    val uiState: StateFlow<TransactionUiState> = _uiState

    private var currentPage = 1
    private val allTransactions = mutableListOf<Transaction>()
    private var isLastPage = false

    fun loadTransactions(token: String, isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 1
            allTransactions.clear()
            isLastPage = false
        }

        if (isLastPage) return

        viewModelScope.launch {
            if (allTransactions.isEmpty()) {
                _uiState.value = TransactionUiState.Loading
            }
            
            transactionRepository.getTransactions(token, currentPage)
                .onSuccess { newTransactions ->
                    if (newTransactions.isEmpty()) {
                        isLastPage = true
                    } else {
                        allTransactions.addAll(newTransactions)
                        currentPage++
                    }
                    _uiState.value = TransactionUiState.Success(allTransactions.toList())
                }
                .onFailure { error ->
                    _uiState.value = TransactionUiState.Error(error.message ?: "An unknown error occurred")
                }
        }
    }
}

sealed class TransactionUiState {
    object Loading : TransactionUiState()
    data class Success(val transactions: List<Transaction>) : TransactionUiState()
    data class Error(val message: String) : TransactionUiState()
}
