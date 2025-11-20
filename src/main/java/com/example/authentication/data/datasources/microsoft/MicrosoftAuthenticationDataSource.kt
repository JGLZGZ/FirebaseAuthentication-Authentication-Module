package com.example.authentication.data.datasources.microsoft

import android.app.Activity
import com.example.authentication.data.dtos.UserDto

interface MicrosoftAuthenticationDataSource {

    // MICROSOFT
    suspend fun signInMicrosoft(activity: Activity) : Result<UserDto?>
    suspend fun linkMicrosoft( activity: Activity) : Result<UserDto?>

}