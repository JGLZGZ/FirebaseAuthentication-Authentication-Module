package com.example.authentication.domain.usecases.google

import android.app.Activity
import com.example.authentication.domain.models.UserModel

interface LinkGoogleUseCase {
    suspend operator fun invoke(activity: Activity): Result<UserModel?>
}