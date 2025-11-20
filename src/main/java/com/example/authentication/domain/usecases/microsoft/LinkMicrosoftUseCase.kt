package com.example.authentication.domain.usecases.microsoft

import android.app.Activity
import com.example.authentication.domain.models.UserModel

interface LinkMicrosoftUseCase {
    suspend operator fun invoke(activity: Activity) : Result<UserModel?>
}