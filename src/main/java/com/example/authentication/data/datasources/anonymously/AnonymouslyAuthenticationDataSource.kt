package com.example.authentication.data.datasources.anonymously

import com.example.authentication.data.dtos.UserDto

interface AnonymouslyAuthenticationDataSource {
    // ANONYMOUSLY
    suspend fun signInAnonymously() : Result<UserDto?>
}