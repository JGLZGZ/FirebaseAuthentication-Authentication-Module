package com.estholon.authentication.domain.usecases.google

import com.estholon.authentication.domain.repositories.AuthenticationRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ClearCredentialStateUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var useCase: ClearCredentialStateUseCaseImpl

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = ClearCredentialStateUseCaseImpl(repository)
    }

    @Test
    fun `invoke calls clearCredentialState`() = runTest {
        // When
        useCase()

        // Then
        coVerify(exactly = 1) { repository.clearCredentialState() }
    }
}