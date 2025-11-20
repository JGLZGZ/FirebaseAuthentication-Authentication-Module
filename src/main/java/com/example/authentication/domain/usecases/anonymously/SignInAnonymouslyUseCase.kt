package com.example.authentication.domain.usecases.anonymously

interface SignInAnonymouslyUseCase {

    suspend operator fun invoke() : Result<Unit>

}