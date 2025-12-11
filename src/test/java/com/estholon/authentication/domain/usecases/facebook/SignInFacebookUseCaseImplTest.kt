package com.estholon.authentication.domain.usecases.facebook

import com.estholon.analytics.domain.usecases.SendEventUseCase
import com.estholon.authentication.domain.models.UserModel
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import com.facebook.AccessToken
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SignInFacebookUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var sendEventUseCase: SendEventUseCase
    private lateinit var useCase: SignInFacebookUseCaseImpl

    @Before
    fun setup() {
        repository = mockk()
        sendEventUseCase = mockk(relaxed = true)
        useCase = SignInFacebookUseCaseImpl(repository, sendEventUseCase)
    }

    @Test
    fun `invoke returns success and sends analytics`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>()
        val user = UserModel("uid", null, null, null)
        coEvery { repository.signInFacebook(accessToken) } returns Result.success(user)

        // When
        val result = useCase(accessToken)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.signInFacebook(accessToken) }
        coVerify(exactly = 1) { sendEventUseCase(any()) }
    }

    @Test
    fun `invoke returns failure and sends analytics`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>()
        val exception = Exception("Error")
        coEvery { repository.signInFacebook(accessToken) } returns Result.failure(exception)

        // When
        val result = useCase(accessToken)

        // Then
        assertTrue(result.isFailure)
        coVerify(exactly = 1) { repository.signInFacebook(accessToken) }
        coVerify(exactly = 1) { sendEventUseCase(any()) }
    }
}