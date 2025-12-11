package com.estholon.authentication.domain.usecases.multifactor

import com.estholon.authentication.domain.repositories.AuthenticationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SendVerificationEmailUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var useCase: SendVerificationEmailUseCaseImpl

    @Before
    fun setup() {
        repository = mockk()
        useCase = SendVerificationEmailUseCaseImpl(repository)
    }

    @Test
    fun `invoke returns result from repository`() = runTest {
        // Given
        coEvery { repository.sendEmailVerification() } returns Result.success(Unit)

        // When
        val result = useCase()

        // Then
        assertEquals(Result.success(Unit), result)
    }
}