package com.example.authentication.data.datasources.github

import android.app.Activity
import com.example.authentication.data.dtos.UserDto

interface GitHubAuthenticationDataSource {

    // GITHUB
    suspend fun signInGitHub(activity: Activity) : Result<UserDto?>
    suspend fun linkGitHub( activity: Activity) : Result<UserDto?>

}