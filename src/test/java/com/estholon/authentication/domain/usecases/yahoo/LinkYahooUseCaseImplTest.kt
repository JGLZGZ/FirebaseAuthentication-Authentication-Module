package com.estholon.authentication.domain.usecases.yahoo

import android.app.Activity
import com.estholon.authentication.domain.models.UserModel
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LinkYahooUseCaseImplTest {

    private lateinit var repository: AuthenticationRepository
    private lateinit var useCase: LinkYahooUseCaseImpl

    @Before
    fun setup() {
        repository = mockk()
        useCase = LinkYahooUseCaseImpl(repository)
    }

    @Test
    fun `invoke returns result from repository`() = runTest {
        // Given
        val activity = mockk<Activity>()
        val user = UserModel("uid", null, null, null)
        coEvery { repository.linkYahoo(activity) } returns Result.success(user)

        // When
        val result = useCase(activity)

        // Then
        assertEquals(Result.success(user), result)
    }
}