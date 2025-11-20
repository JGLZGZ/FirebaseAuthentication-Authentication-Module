package com.example.authentication.domain.usecases.facebook

import com.example.authentication.domain.models.UserModel
import com.example.authentication.domain.repositories.AuthenticationRepository
import com.facebook.AccessToken
import javax.inject.Inject

class LinkFacebookUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkFacebookUseCase {

    override suspend operator fun invoke(accessToken: AccessToken): Result<UserModel?> {
        return authenticationRepository.linkFacebook(accessToken)
    }

}