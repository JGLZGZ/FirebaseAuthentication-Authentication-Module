package com.example.authentication.domain.usecases.facebook

import com.facebook.AccessToken

interface SignInFacebookUseCase {
    suspend operator fun invoke( accessToken: AccessToken) : Result<Unit>
}