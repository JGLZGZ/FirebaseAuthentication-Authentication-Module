package com.example.authentication.data.datasources.yahoo

import android.app.Activity
import com.example.authentication.data.dtos.UserDto

interface YahooAuthenticationDataSource {

    // YAHOO
    suspend fun signInYahoo(activity: Activity) : Result<UserDto?>
    suspend fun linkYahoo( activity: Activity) : Result<UserDto?>

}