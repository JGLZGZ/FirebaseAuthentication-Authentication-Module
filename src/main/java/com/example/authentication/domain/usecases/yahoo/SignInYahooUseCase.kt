package com.example.authentication.domain.usecases.yahoo

import android.app.Activity

interface SignInYahooUseCase {
    suspend operator fun invoke( activity : Activity) : Result<Unit>
}