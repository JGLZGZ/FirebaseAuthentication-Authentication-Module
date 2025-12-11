package com.estholon.authentication.domain.usecases.email

import com.estholon.authentication.domain.repositories.AuthenticationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ResetPasswordUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var useCase: ResetPasswordUseCaseImpl

    @Before
    fun setup() {
        repository = mockk()
        useCase = ResetPasswordUseCaseImpl(repository)
    }

    @Test
    fun `invoke returns result from repository`() = runTest {
        // Given
        coEvery { repository.resetPassword("email") } returns Result.success(Unit)

        // When
        val result = useCase("email")

        // Then
        assertEquals(Result.success(Unit), result)
    }
}