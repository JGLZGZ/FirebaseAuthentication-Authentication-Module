package com.example.authentication.data.datasources.google

import android.app.Activity
import androidx.credentials.GetCredentialResponse
import com.example.authentication.data.dtos.UserDto

interface GoogleAuthenticationDataSource {

    // GOOGLE
    suspend fun signInGoogleCredentialManager(activity: Activity): Result<UserDto?>
    suspend fun signInGoogle(activity: Activity) : Result<UserDto?>
    suspend fun handleCredentialResponse(result: GetCredentialResponse):Result<UserDto?>
    suspend fun clearCredentialState()
    suspend fun linkGoogle( activity: Activity) : Result<UserDto?>

}