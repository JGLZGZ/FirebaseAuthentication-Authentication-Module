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

class SignUpEmailUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var sendEventUseCase: SendEventUseCase
    private lateinit var useCase: SignUpEmailUseCaseImpl

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        sendEventUseCase = mockk(relaxed = true)
        useCase = SignUpEmailUseCaseImpl(repository, sendEventUseCase)
    }

    @Test
    fun `invoke returns success, sends verification email and analytics`() = runTest {
        // Given
        val user = UserModel("uid", "email", null, null)
        coEvery { repository.signUpEmail("email", "pwd") } returns Result.success(user)
        coEvery { repository.sendEmailVerification() } returns Result.success(Unit)

        // When
        val result = useCase("email", "pwd")

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.signUpEmail("email", "pwd") }
        coVerify(exactly = 1) { repository.sendEmailVerification() }
        coVerify(exactly = 1) { sendEventUseCase(any()) }
    }

    @Test
    fun `invoke returns failure and sends analytics`() = runTest {
        // Given
        val exception = Exception("Error")
        coEvery { repository.signUpEmail("email", "pwd") } returns Result.failure(exception)

        // When
        val result = useCase("email", "pwd")

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 1) { repository.signUpEmail("email", "pwd") }
        coVerify(exactly = 0) { repository.sendEmailVerification() }
        coVerify(exactly = 1) { sendEventUseCase(any()) }
    }
}