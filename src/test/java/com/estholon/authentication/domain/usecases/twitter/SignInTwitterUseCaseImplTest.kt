package com.estholon.authentication.domain.usecases.twitter

import android.app.Activity
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

class SignInTwitterUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var sendEventUseCase: SendEventUseCase
    private lateinit var useCase: SignInTwitterUseCaseImpl

    @Before
    fun setup() {
        repository = mockk()
        sendEventUseCase = mockk(relaxed = true)
        useCase = SignInTwitterUseCaseImpl(repository, sendEventUseCase)
    }

    @Test
    fun `invoke returns success and sends analytics`() = runTest {
        // Given
        val activity = mockk<Activity>()
        val user = UserModel("uid", null, null, null)
        coEvery { repository.signInTwitter(activity) } returns Result.success(user)

        // When
        val result = useCase(activity)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.signInTwitter(activity) }
        coVerify(exactly = 1) { sendEventUseCase(any()) }
    }

    @Test
    fun `invoke returns failure and sends analytics`() = runTest {
        // Given
        val activity = mockk<Activity>()
        val exception = Exception("Error")
        coEvery { repository.signInTwitter(activity) } returns Result.failure(exception)

        // When
        val result = useCase(activity)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 1) { repository.signInTwitter(activity) }
        coVerify(exactly = 1) { sendEventUseCase(any()) }
    }
}