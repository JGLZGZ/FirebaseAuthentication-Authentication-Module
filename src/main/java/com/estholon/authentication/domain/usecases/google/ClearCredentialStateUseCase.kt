package com.estholon.authentication.domain.usecases.google

interface ClearCredentialStateUseCase {
    suspend operator fun invoke()
}