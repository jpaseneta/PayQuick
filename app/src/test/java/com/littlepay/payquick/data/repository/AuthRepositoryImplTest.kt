package com.littlepay.payquick.data.repository

import com.littlepay.payquick.data.api.AuthService
import com.littlepay.payquick.data.model.LoginDataDto
import com.littlepay.payquick.data.model.LoginRequest
import com.littlepay.payquick.data.model.LoginResponseDto
import com.littlepay.payquick.data.model.UserDto
import com.littlepay.payquick.domain.model.User
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
class AuthRepositoryImplTest {

    @Mock
    private lateinit var authService: AuthService

    @Mock
    private lateinit var tokenManager: TokenManager

    private lateinit var authRepository: AuthRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authRepository = AuthRepositoryImpl(authService, tokenManager)
    }

    @Test
    fun `login success returns user and updates tokens`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password"
        val accessToken = "access_token"
        val refreshToken = "refresh_token"
        val userDto = UserDto("1", "Test User", email)
        val loginDataDto = LoginDataDto(accessToken, refreshToken, 3600, "Bearer", userDto)
        val loginResponseDto = LoginResponseDto("success", "Login successful", loginDataDto)
        
        `when`(authService.login(LoginRequest(email, password))).thenReturn(Response.success(loginResponseDto))

        // Act
        val result = authRepository.login(email, password)

        // Assert
        assertTrue(result.isSuccess)
        val expectedUser = User("1", "Test User", email)
        assertEquals(Pair(expectedUser, accessToken), result.getOrNull())
    }

    @Test
    fun `login failure returns error`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password"
        `when`(authService.login(LoginRequest(email, password))).thenReturn(Response.error(401, "".toResponseBody()))

        // Act
        val result = authRepository.login(email, password)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Error: 401", result.exceptionOrNull()?.message)
    }
}
