package com.estholon.authentication.domain.usecases.google

import android.app.Activity

interface SignInGoogleCredentialManagerUseCase {
    suspend operator fun invoke(activity: Activity) : Result<Unit>
}