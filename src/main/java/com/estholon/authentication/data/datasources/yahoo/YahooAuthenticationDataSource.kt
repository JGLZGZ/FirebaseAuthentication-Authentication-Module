package com.estholon.authentication.data.datasources.yahoo

import android.app.Activity
import com.estholon.authentication.data.dtos.UserDto

interface YahooAuthenticationDataSource {

    // YAHOO
    suspend fun signInYahoo(activity: Activity) : Result<UserDto?>
    suspend fun linkYahoo( activity: Activity) : Result<UserDto?>

}