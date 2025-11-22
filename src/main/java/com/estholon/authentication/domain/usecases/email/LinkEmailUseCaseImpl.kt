package com.estholon.authentication.domain.usecases.email

import com.estholon.authentication.domain.models.UserModel
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkEmailUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkEmailUseCase {

    override suspend operator fun invoke(email: String, password: String): Result<UserModel?> {
        return authenticationRepository.linkEmail(email, password)
    }

}