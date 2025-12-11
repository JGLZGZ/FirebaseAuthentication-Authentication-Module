package com.estholon.authentication.domain.usecases.email

import org.junit.Assert.assertTrue
import org.junit.Test

class IsPasswordValidUseCaseImplTest {

    private val isPasswordValidUseCase = IsPasswordValidUseCaseImpl()

    @Test
    fun `invoke returns success when password is valid`() {
        val result = isPasswordValidUseCase("123456")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke returns failure when password is empty`() {
        val result = isPasswordValidUseCase("")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message == "La contraseña es requerida")
    }

    @Test
    fun `invoke returns failure when password is too short`() {
        val result = isPasswordValidUseCase("12345")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message == "La contraseña debe tener al menos 6 caracteres")
    }
}
