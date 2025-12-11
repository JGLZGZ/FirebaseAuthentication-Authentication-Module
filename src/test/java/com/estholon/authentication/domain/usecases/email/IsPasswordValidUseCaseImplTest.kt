package com.estholon.authentication.domain.usecases.email

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IsPasswordValidUseCaseImplTest {

    private lateinit var useCase: IsPasswordValidUseCaseImpl

    @Before
    fun setup() {
        useCase = IsPasswordValidUseCaseImpl()
    }

    @Test
    fun `invoke returns failure when password is blank`() {
        val result = useCase("")
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke returns failure when password is short`() {
        val result = useCase("12345")
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke returns success when password is valid`() {
        val result = useCase("123456")
        assertTrue(result.isSuccess)
    }
}