package com.example.authentication.data.datasources.facebook

import com.example.authentication.data.dtos.UserDto
import com.facebook.AccessToken

interface FacebookAuthenticationDataSource {

    // FACEBOOK
    suspend fun signInFacebook(accessToken: AccessToken) : Result<UserDto?>
    suspend fun linkFacebook(accessToken: AccessToken) : Result<UserDto?>

}