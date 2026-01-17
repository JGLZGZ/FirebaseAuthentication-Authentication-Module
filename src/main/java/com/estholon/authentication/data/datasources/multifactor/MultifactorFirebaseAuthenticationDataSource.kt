package com.estholon.authentication.data.datasources.multifactor

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.MultiFactorSession
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MultifactorFirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val phoneAuthOptionsFactory: PhoneAuthOptionsFactory
): MultifactorAuthenticationDataSource {

    override suspend fun getMultifactorSession(): MultiFactorSession {
        val user = firebaseAuth.currentUser ?: throw Exception("No hay usuario autenticado")
        return user.multiFactor.session.await()
    }

    override suspend fun enrollMfaSendSms(
        session: MultiFactorSession,
        phoneNumber: String
    ): Result<String> = runCatching {
        sendSmsForEnroll(session,phoneNumber)
    }

    internal suspend fun sendSmsForEnroll(
        session: MultiFactorSession,
        phoneNumber: String,
    ) = suspendCancellableCoroutine<String> { continuation ->
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                continuation.resume(verificationId)
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-verification completed - not used for MFA enrollment
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                continuation.resumeWithException(p0)
            }
        }

        val options = phoneAuthOptionsFactory.createPhoneAuthOptions(
            firebaseAuth = firebaseAuth,
            phoneNumber = phoneNumber,
            timeout = 60,
            timeUnit = TimeUnit.SECONDS,
            session = session,
            callbacks = callback
        )

        phoneAuthOptionsFactory.verifyPhoneNumber(options)
    }


    override suspend fun verifySmsForEnroll(
        verificationId: String,
        verificationCode: String
    ): Result<Unit> = runCatching {
        val credential = phoneAuthOptionsFactory.getCredential(verificationId, verificationCode)
        val multiFactorAssertion = phoneAuthOptionsFactory.getMultiFactorAssertion(credential)
        firebaseAuth.currentUser?.multiFactor?.enroll(multiFactorAssertion, "Personal number")
            ?.await()
    }

}