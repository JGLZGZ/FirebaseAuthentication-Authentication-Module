package com.example.authentication.domain.usecases.email

interface SignInEmailUseCase {
    suspend operator fun invoke( email: String, password: String ) : Result<Unit>
}