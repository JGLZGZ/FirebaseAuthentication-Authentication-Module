package com.estholon.authentication.domain.usecases.multifactor

import com.estholon.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class SendVerificationEmailUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
) : SendVerificationEmailUseCase {

    override suspend fun invoke(): Result<Unit> {
        return authenticationRepository.sendEmailVerification()
    }

}