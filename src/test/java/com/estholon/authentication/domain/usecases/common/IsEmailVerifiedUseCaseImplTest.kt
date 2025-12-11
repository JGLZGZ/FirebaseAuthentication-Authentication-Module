package com.estholon.authentication.domain.usecases.common

import com.estholon.authentication.domain.repositories.AuthenticationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class IsEmailVerifiedUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var useCase: IsEmailVerifiedUseCaseImpl

    @Before
    fun setup() {
        repository = mockk()
        useCase = IsEmailVerifiedUseCaseImpl(repository)
    }

    @Test
    fun `invoke returns result from repository`() = runTest {
        // Given
        coEvery { repository.isEmailVerified() } returns Result.success(true)

        // When
        val result = useCase()

        // Then
        assertEquals(Result.success(true), result)
    }
}
