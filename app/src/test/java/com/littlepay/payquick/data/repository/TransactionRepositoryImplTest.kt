package com.littlepay.payquick.data.repository

import com.littlepay.payquick.data.api.TransactionService
import com.littlepay.payquick.data.model.PaginationDto
import com.littlepay.payquick.data.model.TransactionDto
import com.littlepay.payquick.data.model.TransactionResponseDto
import com.littlepay.payquick.domain.model.AuthorizationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

@ExperimentalCoroutinesApi
class TransactionRepositoryImplTest {

    @Mock
    private lateinit var transactionService: TransactionService

    @Mock
    private lateinit var authRepository: AuthRepositoryImpl

    @Mock
    private lateinit var tokenManager: TokenManager

    private lateinit var transactionRepository: TransactionRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        transactionRepository = TransactionRepositoryImpl(transactionService, authRepository, tokenManager)
    }

    @Test
    fun `getTransactions success returns transaction list`() = runTest {
        // Arrange
        val token = "access_token"
        val page = 1
        val transactionDto = TransactionDto("1", 1000, "USD", "Payment", "2023-10-01", "completed")
        val paginationDto = PaginationDto(1, 2, 10, 5)
        val responseDto = TransactionResponseDto("success", listOf(transactionDto), paginationDto)

        `when`(tokenManager.accessToken).thenReturn(token)
        `when`(transactionService.getTransactions("Bearer $token", page)).thenReturn(Response.success(responseDto))

        // Act
        val result = transactionRepository.getTransactions(token, page)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.transactions?.size)
        assertEquals("Payment", result.getOrNull()?.transactions?.get(0)?.description)
    }

    @Test
    fun `getTransactions unauthorized triggers refresh and retries`() = runTest {
        // Arrange
        val token = "old_token"
        val newToken = "new_token"
        val page = 1
        val transactionDto = TransactionDto("1", 1000, "USD", "Payment", "2023-10-01", "completed")
        val paginationDto = PaginationDto(1, 1, 1, 1)
        val responseDto = TransactionResponseDto("success", listOf(transactionDto), paginationDto)

        `when`(tokenManager.accessToken).thenReturn(token).thenReturn(newToken)
        `when`(transactionService.getTransactions("Bearer $token", page)).thenReturn(Response.error(401, "".toResponseBody()))
        `when`(authRepository.refreshToken()).thenReturn(Result.success(newToken))
        `when`(transactionService.getTransactions("Bearer $newToken", page)).thenReturn(Response.success(responseDto))

        // Act
        val result = transactionRepository.getTransactions(token, page)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.transactions?.size)
    }

    @Test
    fun `getTransactions refresh failure returns AuthorizationException`() = runTest {
        // Arrange
        val token = "old_token"
        val page = 1
        `when`(tokenManager.accessToken).thenReturn(token)
        `when`(transactionService.getTransactions("Bearer $token", page)).thenReturn(Response.error(401, "".toResponseBody()))
        `when`(authRepository.refreshToken()).thenReturn(Result.failure(Exception("Refresh failed")))

        // Act
        val result = transactionRepository.getTransactions(token, page)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AuthorizationException)
    }
}
