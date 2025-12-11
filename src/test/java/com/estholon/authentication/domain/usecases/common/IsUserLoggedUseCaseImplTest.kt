package com.estholon.authentication.domain.usecases.common

import com.estholon.authentication.domain.repositories.AuthenticationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class IsUserLoggedUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var useCase: IsUserLoggedUseCaseImpl

    @Before
    fun setup() {
        repository = mockk()
        useCase = IsUserLoggedUseCaseImpl(repository)
    }

    @Test
    fun `invoke returns flow with result from repository`() = runTest {
        // Given
        coEvery { repository.isUserLogged() } returns Result.success(true)

        // When
        val result = useCase().first()

        // Then
        assertEquals(Result.success(true), result)
    }
}
