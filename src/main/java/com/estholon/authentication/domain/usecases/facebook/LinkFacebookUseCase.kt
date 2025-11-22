package com.estholon.authentication.domain.usecases.facebook

import com.estholon.authentication.domain.models.UserModel
import com.facebook.AccessToken

interface LinkFacebookUseCase {
    suspend operator fun invoke(accessToken: AccessToken): Result<UserModel?>
}