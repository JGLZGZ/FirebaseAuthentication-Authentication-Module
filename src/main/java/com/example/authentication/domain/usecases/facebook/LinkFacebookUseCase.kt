package com.example.authentication.domain.usecases.facebook

import com.example.authentication.domain.models.UserModel
import com.facebook.AccessToken

interface LinkFacebookUseCase {
    suspend operator fun invoke(accessToken: AccessToken): Result<UserModel?>
}