package com.estholon.authentication.domain.usecases.facebook

import com.estholon.authentication.domain.models.UserModel
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import com.facebook.AccessToken
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LinkFacebookUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var useCase: LinkFacebookUseCaseImpl

    @Before
    fun setup() {
        repository = mockk()
        useCase = LinkFacebookUseCaseImpl(repository)
    }

    @Test
    fun `invoke returns result from repository`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>()
        val user = UserModel("uid", null, null, null)
        coEvery { repository.linkFacebook(accessToken) } returns Result.success(user)

        // When
        val result = useCase(accessToken)

        // Then
        assertEquals(Result.success(user), result)
    }
}