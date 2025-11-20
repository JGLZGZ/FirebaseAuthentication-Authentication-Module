package com.example.authentication.domain.usecases.yahoo

import android.app.Activity
import com.example.authentication.domain.models.UserModel
import com.example.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkYahooUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkYahooUseCase {

    override suspend operator fun invoke(activity: Activity) : Result<UserModel?> {
        return authenticationRepository.linkYahoo(activity)
    }

}