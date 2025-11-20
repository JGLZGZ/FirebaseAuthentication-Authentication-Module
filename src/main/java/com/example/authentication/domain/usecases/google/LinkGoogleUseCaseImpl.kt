package com.example.authentication.domain.usecases.google

import android.app.Activity
import com.example.authentication.domain.models.UserModel
import com.example.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkGoogleUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkGoogleUseCase {

    override suspend operator fun invoke(activity: Activity): Result<UserModel?> {
        return authenticationRepository.linkGoogle(activity)
    }

}