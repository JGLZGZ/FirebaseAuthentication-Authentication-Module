package com.example.authentication.domain.usecases.email

import com.example.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class ResetPasswordUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ResetPasswordUseCase {

    override suspend operator fun invoke( email : String ) : Result<Unit> {
        return authenticationRepository.resetPassword( email )
    }

}