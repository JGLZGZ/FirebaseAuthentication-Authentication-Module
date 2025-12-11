package com.estholon.authentication.domain.usecases.common

import com.estholon.authentication.domain.repositories.AuthenticationRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SignOutUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var useCase: SignOutUseCaseImpl

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = SignOutUseCaseImpl(repository)
    }

    @Test
    fun `invoke calls signOut on repository`() = runTest {
        // When
        useCase()

        // Then
        coVerify { repository.signOut() }
    }
}
