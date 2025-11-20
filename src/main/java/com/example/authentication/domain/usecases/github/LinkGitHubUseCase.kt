package com.example.authentication.domain.usecases.github

import android.app.Activity
import com.example.authentication.domain.models.UserModel

interface LinkGitHubUseCase {
    suspend operator fun invoke(activity: Activity): Result<UserModel?>
}