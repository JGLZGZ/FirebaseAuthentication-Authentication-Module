package com.estholon.authentication.domain.usecases.multifactor

import com.estholon.analytics.domain.usecases.SendEventUseCase
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import com.google.firebase.auth.MultiFactorSession
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StartEnrollPhoneUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var sendEventUseCase: SendEventUseCase
    private lateinit var useCase: StartEnrollPhoneUseCaseImpl

    @Before
    fun setup() {
        repository = mockk()
        sendEventUseCase = mockk(relaxed = true)
        useCase = StartEnrollPhoneUseCaseImpl(repository, sendEventUseCase)
    }

    @Test
    fun `invoke returns flow with result from repository`() = runTest {
        // Given
        val session = mockk<MultiFactorSession>()
        val phoneNumber = "+1234567890"
        coEvery { repository.getMultifactorSession() } returns session
        coEvery { repository.enrollMfaSendSms(session, phoneNumber) } returns Result.success("verificationId")

        // When
        val result = useCase(phoneNumber).first()

        // Then
        assertEquals(Result.success("verificationId"), result)
    }
}