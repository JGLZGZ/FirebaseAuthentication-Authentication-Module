package com.estholon.authentication.data.datasources.anonymously

import com.estholon.authentication.data.dtos.UserDto

interface AnonymouslyAuthenticationDataSource {
    // ANONYMOUSLY
    suspend fun signInAnonymously() : Result<UserDto?>
}