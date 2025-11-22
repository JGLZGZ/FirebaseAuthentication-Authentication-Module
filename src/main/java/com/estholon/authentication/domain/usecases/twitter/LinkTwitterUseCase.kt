package com.estholon.authentication.domain.usecases.twitter

import android.app.Activity
import com.estholon.authentication.domain.models.UserModel

interface LinkTwitterUseCase {
    suspend operator fun invoke(activity: Activity) : Result<UserModel?>
}