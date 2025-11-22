package com.estholon.authentication.data.datasources.github

import android.app.Activity
import com.estholon.authentication.data.dtos.UserDto

interface GitHubAuthenticationDataSource {

    // GITHUB
    suspend fun signInGitHub(activity: Activity) : Result<UserDto?>
    suspend fun linkGitHub( activity: Activity) : Result<UserDto?>

}