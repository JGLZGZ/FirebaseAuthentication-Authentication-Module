package com.example.authentication.domain.usecases.twitter

import android.app.Activity
import com.example.authentication.domain.models.UserModel

interface LinkTwitterUseCase {
    suspend operator fun invoke(activity: Activity) : Result<UserModel?>
}