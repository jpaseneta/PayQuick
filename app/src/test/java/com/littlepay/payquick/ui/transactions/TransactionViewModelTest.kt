package com.littlepay.payquick.ui.transactions

import com.littlepay.payquick.data.repository.TokenManager
import com.littlepay.payquick.domain.model.AuthorizationException
import com.littlepay.payquick.domain.model.Pagination
import com.littlepay.payquick.domain.model.Transaction
import com.littlepay.payquick.domain.model.TransactionsList
import com.littlepay.payquick.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class TransactionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    @Mock
    private lateinit var tokenManager: TokenManager

    private lateinit var viewModel: TransactionViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Mock token for init call
        `when`(tokenManager.accessToken).thenReturn("test_token")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `call getTransactions and return a SuccessFul UiState containing transactions`() = runTest {
        // Arrange
        val token = "test_token"
        val transactions = listOf(Transaction("1", 1000, "USD", "Test", "2023-10-01", "completed"))
        val pagination = Pagination(1, 1, 1, 1)
        val transactionsList = TransactionsList(transactions, pagination)
        
        `when`(transactionRepository.getTransactions(token, 1)).thenReturn(Result.success(transactionsList))

        // Act
        viewModel = TransactionViewModel(transactionRepository, tokenManager)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState.value is TransactionUiState.Success)
        assertEquals(transactions, (viewModel.uiState.value as TransactionUiState.Success).transactions)
    }

    @Test
    fun `call getTransactions and return Error UiState containing error message`() = runTest {
        // Arrange
        val token = "test_token"
        `when`(transactionRepository.getTransactions(token, 1)).thenReturn(Result.failure(Exception("Network Error")))

        // Act
        viewModel = TransactionViewModel(transactionRepository, tokenManager)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState.value is TransactionUiState.Error)
        assertEquals("Network Error", (viewModel.uiState.value as TransactionUiState.Error).message)
    }

    @Test
    fun `simulate expired token and call getTransactions and return AuthError UiState containing error message`() = runTest {
        // Arrange
        val token = "test_token"
        `when`(transactionRepository.getTransactions(token, 1)).thenReturn(Result.failure(AuthorizationException("Expired")))

        // Act
        viewModel = TransactionViewModel(transactionRepository, tokenManager)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState.value is TransactionUiState.AuthError)
        assertEquals("Expired", (viewModel.uiState.value as TransactionUiState.AuthError).message)
    }

    @Test
    fun `Logout calls TokenManager clearTokens()`() = runTest {
        // Act
        viewModel = TransactionViewModel(transactionRepository, tokenManager)
        viewModel.logout()

        // Assert
        verify(tokenManager).clearTokens()
    }
}
