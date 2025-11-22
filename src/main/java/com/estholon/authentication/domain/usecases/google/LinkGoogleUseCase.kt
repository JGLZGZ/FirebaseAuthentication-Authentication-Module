package com.estholon.authentication.domain.usecases.google

import android.app.Activity
import com.estholon.authentication.domain.models.UserModel

interface LinkGoogleUseCase {
    suspend operator fun invoke(activity: Activity): Result<UserModel?>
}