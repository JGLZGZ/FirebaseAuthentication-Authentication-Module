package com.estholon.authentication.domain.usecases.github

import android.app.Activity
import com.estholon.authentication.domain.models.UserModel

interface LinkGitHubUseCase {
    suspend operator fun invoke(activity: Activity): Result<UserModel?>
}