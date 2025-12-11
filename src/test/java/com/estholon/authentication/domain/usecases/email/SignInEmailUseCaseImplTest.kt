package com.estholon.authentication.domain.usecases.email

import com.estholon.analytics.domain.models.AnalyticsModel
import com.estholon.analytics.domain.usecases.SendEventUseCase
import com.estholon.authentication.data.dtos.UserDto
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignInEmailUseCaseImplTest {

    private val authenticationRepository = mockk<AuthenticationRepository>()
    private val sendEventUseCase = mockk<SendEventUseCase>(relaxed = true)
    private val signInEmailUseCase = SignInEmailUseCaseImpl(authenticationRepository, sendEventUseCase)

    @Test
    fun `invoke calls repository and analytics on success`() = runTest {
        val email = "test@example.com"
        val password = "password"
        val userDto = UserDto("uid", "email", "name", "phone")

        coEvery { authenticationRepository.signInEmail(email, password) } returns Result.success(userDto)

        val result = signInEmailUseCase(email, password)

        assertTrue(result.isSuccess)
        coVerify { authenticationRepository.signInEmail(email, password) }

        val analyticsSlot = slot<AnalyticsModel>()
        coVerify { sendEventUseCase(capture(analyticsSlot)) }
        assertEquals("Sign In", analyticsSlot.captured.title)
        assertEquals("Successful Sign In", analyticsSlot.captured.analyticsString.first().second)
    }

    @Test
    fun `invoke calls repository and analytics on failure`() = runTest {
        val email = "test@example.com"
        val password = "password"
        val errorMessage = "Login failed"

        coEvery { authenticationRepository.signInEmail(email, password) } returns Result.failure(Exception(errorMessage))

        val result = signInEmailUseCase(email, password)

        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
        coVerify { authenticationRepository.signInEmail(email, password) }

        val analyticsSlot = slot<AnalyticsModel>()
        coVerify { sendEventUseCase(capture(analyticsSlot)) }
        assertEquals("Sign In", analyticsSlot.captured.title)
        assertEquals("Failed Sign In", analyticsSlot.captured.analyticsString.first().second)
    }
}
