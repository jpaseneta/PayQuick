package com.littlepay.payquick.ui.login

import com.littlepay.payquick.domain.model.User
import com.littlepay.payquick.domain.repository.AuthRepository
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
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Call login successfully returns state to Success UiState`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password"
        val user = User("1", "Test User", email)
        val token = "access_token"
        
        `when`(authRepository.login(email, password)).thenReturn(Result.success(Pair(user, token)))

        // Act
        viewModel.login(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.loginState.value is LoginUiState.Success)
        val successState = viewModel.loginState.value as LoginUiState.Success
        assertEquals(user, successState.user)
        assertEquals(token, successState.token)
    }

    @Test
    fun `Call login fails and returns state to Error UiState`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password"
        val errorMessage = "Invalid credentials"
        
        `when`(authRepository.login(email, password)).thenReturn(Result.failure(Exception(errorMessage)))

        // Act
        viewModel.login(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.loginState.value is LoginUiState.Error)
        assertEquals(errorMessage, (viewModel.loginState.value as LoginUiState.Error).message)
    }
}
