package com.estholon.authentication.domain.usecases.google

import android.app.Activity
import com.estholon.authentication.domain.models.UserModel
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkGoogleUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkGoogleUseCase {

    override suspend operator fun invoke(activity: Activity): Result<UserModel?> {
        return authenticationRepository.linkGoogle(activity)
    }

}