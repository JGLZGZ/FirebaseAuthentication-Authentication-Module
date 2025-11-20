package com.example.authentication.domain.usecases.yahoo

import android.app.Activity
import com.example.authentication.domain.models.UserModel

interface LinkYahooUseCase {
    suspend operator fun invoke(activity: Activity) : Result<UserModel?>
}