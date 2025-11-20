package com.example.authentication.domain.usecases.google

interface ClearCredentialStateUseCase {
    suspend operator fun invoke()
}