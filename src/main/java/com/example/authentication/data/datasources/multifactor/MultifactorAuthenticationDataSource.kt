package com.example.authentication.data.datasources.multifactor

import com.google.firebase.auth.MultiFactorSession

interface MultifactorAuthenticationDataSource {

    suspend fun getMultifactorSession(): MultiFactorSession
    suspend fun enrollMfaSendSms(session: MultiFactorSession, phoneNumber: String): Result<String>
    suspend fun verifySmsForEnroll(
        verificationId: String,
        verificationCode: String
    ) : Result<Unit>

}