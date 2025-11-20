package com.example.authentication.data.datasources.email

import com.example.authentication.data.dtos.UserDto

interface EmailAuthenticationDataSource {

    suspend fun signUpEmail( email: String, password: String ) : Result<UserDto?>
    suspend fun signInEmail( email: String, password: String ) : Result<UserDto?>
    suspend fun linkEmail( email: String, password: String ) : Result<UserDto?>
    suspend fun resetPassword( email : String ) : Result<Unit>


}