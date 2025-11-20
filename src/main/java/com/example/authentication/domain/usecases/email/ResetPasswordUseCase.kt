package com.example.authentication.domain.usecases.email

interface ResetPasswordUseCase {
    suspend operator fun invoke( email : String ) : Result<Unit>
}