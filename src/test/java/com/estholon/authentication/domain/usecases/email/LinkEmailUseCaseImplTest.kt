package com.estholon.authentication.domain.usecases.email

import com.estholon.authentication.domain.models.UserModel
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LinkEmailUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var useCase: LinkEmailUseCaseImpl

    @Before
    fun setup() {
        repository = mockk()
        useCase = LinkEmailUseCaseImpl(repository)
    }

    @Test
    fun `invoke returns result from repository`() = runTest {
        // Given
        val user = UserModel("uid", "email", null, null)
        coEvery { repository.linkEmail("email", "pwd") } returns Result.success(user)

        // When
        val result = useCase("email", "pwd")

        // Then
        assertEquals(Result.success(user), result)
    }
}