package com.estholon.authentication.domain.usecases.yahoo

import android.app.Activity
import com.estholon.authentication.domain.models.UserModel
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class LinkYahooUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : LinkYahooUseCase {

    override suspend operator fun invoke(activity: Activity) : Result<UserModel?> {
        return authenticationRepository.linkYahoo(activity)
    }

}