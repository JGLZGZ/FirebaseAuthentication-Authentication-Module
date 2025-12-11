package com.estholon.authentication.domain.usecases.email

import com.estholon.analytics.domain.usecases.SendEventUseCase
import com.estholon.authentication.domain.models.UserModel
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SignInEmailUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var sendEventUseCase: SendEventUseCase
    private lateinit var useCase: SignInEmailUseCaseImpl

    @Before
    fun setup() {
        repository = mockk()
        sendEventUseCase = mockk(relaxed = true)
        useCase = SignInEmailUseCaseImpl(repository, sendEventUseCase)
    }

    @Test
    fun `invoke returns success and sends analytics`() = runTest {
        // Given
        val user = UserModel("uid", "email", null, null)
        coEvery { repository.signInEmail("email", "pwd") } returns Result.success(user)

        // When
        val result = useCase("email", "pwd")

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.signInEmail("email", "pwd") }
        coVerify(exactly = 1) { sendEventUseCase(any()) }
    }

    @Test
    fun `invoke returns failure and sends analytics`() = runTest {
        // Given
        val exception = Exception("Error")
        coEvery { repository.signInEmail("email", "pwd") } returns Result.failure(exception)

        // When
        val result = useCase("email", "pwd")

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 1) { repository.signInEmail("email", "pwd") }
        coVerify(exactly = 1) { sendEventUseCase(any()) }
    }
}