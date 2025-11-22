package com.estholon.authentication.domain.usecases.microsoft

import android.app.Activity
import com.estholon.authentication.domain.models.UserModel

interface LinkMicrosoftUseCase {
    suspend operator fun invoke(activity: Activity) : Result<UserModel?>
}