package com.example.authentication.domain.usecases.email

import com.example.authentication.domain.models.UserModel
import com.example.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkEmailUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkEmailUseCase {

    override suspend operator fun invoke(email: String, password: String): Result<UserModel?> {
        return authenticationRepository.linkEmail(email, password)
    }

}