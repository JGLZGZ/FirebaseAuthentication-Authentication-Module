package com.example.authentication.domain.usecases.twitter

import android.app.Activity

interface SignInTwitterUseCase {
    suspend operator fun invoke( activity : Activity) : Result<Unit>
}