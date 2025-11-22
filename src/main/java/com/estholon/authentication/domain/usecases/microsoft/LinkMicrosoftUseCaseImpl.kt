package com.estholon.authentication.domain.usecases.microsoft

import android.app.Activity
import com.estholon.authentication.domain.models.UserModel
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkMicrosoftUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkMicrosoftUseCase {

    override suspend operator fun invoke(activity: Activity) : Result<UserModel?> {
        return authenticationRepository.linkMicrosoft(activity)
    }

}