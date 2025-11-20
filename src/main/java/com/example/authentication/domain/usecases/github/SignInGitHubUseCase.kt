package com.example.authentication.domain.usecases.github

import android.app.Activity

interface SignInGitHubUseCase {
    suspend operator fun invoke( activity : Activity) : Result<Unit>
}