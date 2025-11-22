package com.estholon.authentication.domain.usecases.twitter

import android.app.Activity
import com.estholon.authentication.domain.models.UserModel
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkTwitterUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkTwitterUseCase {

    override suspend operator fun invoke(activity: Activity) : Result<UserModel?> {
        return authenticationRepository.linkTwitter(activity)
    }

}