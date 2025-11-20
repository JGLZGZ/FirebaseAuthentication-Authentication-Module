package com.example.authentication.domain.usecases.microsoft

import android.app.Activity
import com.example.authentication.domain.models.UserModel
import com.example.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkMicrosoftUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkMicrosoftUseCase {

    override suspend operator fun invoke(activity: Activity) : Result<UserModel?> {
        return authenticationRepository.linkMicrosoft(activity)
    }

}