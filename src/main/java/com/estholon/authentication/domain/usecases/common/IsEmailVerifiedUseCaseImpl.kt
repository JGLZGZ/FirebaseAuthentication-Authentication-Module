package com.estholon.authentication.domain.usecases.common

import com.estholon.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class IsEmailVerifiedUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
): IsEmailVerifiedUseCase {
    override suspend fun invoke(): Result<Boolean> {
        return authenticationRepository.isEmailVerified()
    }
}