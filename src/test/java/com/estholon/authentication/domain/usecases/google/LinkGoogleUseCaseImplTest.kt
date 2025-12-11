package com.estholon.authentication.domain.usecases.google

import android.app.Activity
import com.estholon.authentication.domain.models.UserModel
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LinkGoogleUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var useCase: LinkGoogleUseCaseImpl

    @Before
    fun setup() {
        repository = mockk()
        useCase = LinkGoogleUseCaseImpl(repository)
    }

    @Test
    fun `invoke returns result from repository`() = runTest {
        // Given
        val activity = mockk<Activity>()
        val user = UserModel("uid", null, null, null)
        coEvery { repository.linkGoogle(activity) } returns Result.success(user)

        // When
        val result = useCase(activity)

        // Then
        assertEquals(Result.success(user), result)
    }
}