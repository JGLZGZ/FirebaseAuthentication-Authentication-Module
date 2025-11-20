package com.example.authentication.domain.usecases.github

import android.app.Activity
import com.example.authentication.domain.models.UserModel
import com.example.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkGitHubUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkGitHubUseCase {

    override suspend operator fun invoke(activity: Activity): Result<UserModel?> {
        return authenticationRepository.linkGitHub(activity)
    }

}