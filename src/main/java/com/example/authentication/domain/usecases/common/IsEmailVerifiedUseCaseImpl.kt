package com.example.authentication.domain.usecases.common

import com.example.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class IsEmailVerifiedUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
): IsEmailVerifiedUseCase {
    override suspend fun invoke(): Result<Boolean> {
        return authenticationRepository.isEmailVerified()
    }
}