package com.estholon.authentication.domain.usecases.google

import android.app.Activity

interface SignInGoogleUseCase {
    suspend operator fun invoke(activity: Activity) : Result<Unit>
}