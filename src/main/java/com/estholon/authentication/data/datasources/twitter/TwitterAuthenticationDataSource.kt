package com.estholon.authentication.data.datasources.twitter

import android.app.Activity
import com.estholon.authentication.data.dtos.UserDto

interface TwitterAuthenticationDataSource {

    // TWITTER
    suspend fun signInTwitter(activity: Activity) : Result<UserDto?>
    suspend fun linkTwitter( activity: Activity) : Result<UserDto?>

}