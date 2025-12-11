package com.estholon.authentication.domain.usecases.email

import javax.inject.Inject

class IsEmailValidUseCaseImpl @Inject constructor(

) : IsEmailValidUseCase {

    // A more robust regex that requires at least one dot in the domain part, similar to Patterns.EMAIL_ADDRESS behavior
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    override operator fun invoke( email : String ) : Result<Unit> {
        return when {
            email.isEmpty() -> Result.failure(Exception("El email es requerido"))
            !emailRegex.matches(email) -> Result.failure(Exception("El formato del email no es válido"))
            else -> Result.success(Unit)
        }
    }
}