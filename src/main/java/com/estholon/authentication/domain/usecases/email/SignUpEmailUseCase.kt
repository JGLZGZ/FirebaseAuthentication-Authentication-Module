package com.estholon.authentication.domain.usecases.email

interface SignUpEmailUseCase {
    suspend operator fun invoke(email: String, password: String) : Result<Unit>
}