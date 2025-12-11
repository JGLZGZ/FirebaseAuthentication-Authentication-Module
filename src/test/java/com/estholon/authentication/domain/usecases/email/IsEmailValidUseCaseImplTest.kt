package com.estholon.authentication.domain.usecases.email

import org.junit.Assert.assertTrue
import org.junit.Test

class IsEmailValidUseCaseImplTest {

    private val isEmailValidUseCase = IsEmailValidUseCaseImpl()

    @Test
    fun `invoke returns success when email is valid`() {
        val result = isEmailValidUseCase("test@example.com")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke returns failure when email is empty`() {
        val result = isEmailValidUseCase("")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message == "El email es requerido")
    }

    @Test
    fun `invoke returns failure when email format is invalid`() {
        val result = isEmailValidUseCase("invalid-email")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message == "El formato del email no es válido")
    }

    @Test
    fun `invoke returns failure when email is missing domain`() {
        val result = isEmailValidUseCase("test@")
        assertTrue(result.isFailure)
    }
}
