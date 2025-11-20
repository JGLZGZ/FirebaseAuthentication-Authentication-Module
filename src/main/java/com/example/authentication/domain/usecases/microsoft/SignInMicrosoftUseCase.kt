package com.example.authentication.domain.usecases.microsoft

import android.app.Activity

interface SignInMicrosoftUseCase {
    suspend operator fun invoke( activity : Activity) : Result<Unit>
}