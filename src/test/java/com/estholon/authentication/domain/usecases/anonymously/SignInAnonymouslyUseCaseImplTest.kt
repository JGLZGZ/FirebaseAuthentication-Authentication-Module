package com.estholon.authentication.domain.usecases.anonymously

import com.estholon.analytics.domain.models.AnalyticsModel
import com.estholon.analytics.domain.usecases.SendEventUseCase
import com.estholon.authentication.data.dtos.UserDto
import com.estholon.authentication.domain.models.UserModel
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SignInAnonymouslyUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var sendEventUseCase: SendEventUseCase
    private lateinit var useCase: SignInAnonymouslyUseCaseImpl

    @Before
    fun setup() {
        repository = mockk()
        sendEventUseCase = mockk(relaxed = true)
        useCase = SignInAnonymouslyUseCaseImpl(repository, sendEventUseCase)
    }

    @Test
    fun `invoke returns success and sends analytics event`() = runTest {
        // Given
        val user = UserModel("uid", null, null, null)
        coEvery { repository.signInAnonymously() } returns Result.success(user)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.signInAnonymously() }
        coVerify(exactly = 1) { sendEventUseCase(any()) }
    }

    @Test
    fun `invoke returns failure and sends analytics event`() = runTest {
        // Given
        val exception = Exception("Error")
        coEvery { repository.signInAnonymously() } returns Result.failure(exception)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 1) { repository.signInAnonymously() }
        coVerify(exactly = 1) { sendEventUseCase(any()) }
    }
}