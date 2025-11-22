package com.estholon.authentication.domain.usecases.yahoo

import android.app.Activity
import com.estholon.authentication.domain.models.UserModel

interface LinkYahooUseCase {
    suspend operator fun invoke(activity: Activity) : Result<UserModel?>
}