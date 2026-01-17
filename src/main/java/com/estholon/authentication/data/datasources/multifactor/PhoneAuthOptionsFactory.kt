package com.estholon.authentication.data.datasources.multifactor

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.MultiFactorSession
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneMultiFactorAssertion
import com.google.firebase.auth.PhoneMultiFactorGenerator
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Factory interface for creating phone authentication-related objects.
 * This abstraction allows for easier testing by enabling mocking of Firebase-specific classes.
 */
interface PhoneAuthOptionsFactory {

    /**
     * Creates PhoneAuthOptions for MFA enrollment.
     */
    fun createPhoneAuthOptions(
        firebaseAuth: FirebaseAuth,
        phoneNumber: String,
        timeout: Long,
        timeUnit: TimeUnit,
        session: MultiFactorSession,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ): PhoneAuthOptions

    /**
     * Initiates phone number verification.
     */
    fun verifyPhoneNumber(options: PhoneAuthOptions)

    /**
     * Gets PhoneAuthCredential from verification ID and code.
     */
    fun getCredential(verificationId: String, verificationCode: String): PhoneAuthCredential

    /**
     * Gets PhoneMultiFactorAssertion from credential.
     */
    fun getMultiFactorAssertion(credential: PhoneAuthCredential): PhoneMultiFactorAssertion
}

/**
 * Default implementation of PhoneAuthOptionsFactory.
 */
class PhoneAuthOptionsFactoryImpl @Inject constructor() : PhoneAuthOptionsFactory {

    override fun createPhoneAuthOptions(
        firebaseAuth: FirebaseAuth,
        phoneNumber: String,
        timeout: Long,
        timeUnit: TimeUnit,
        session: MultiFactorSession,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ): PhoneAuthOptions {
        return PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(timeout, timeUnit)
            .setMultiFactorSession(session)
            .setCallbacks(callbacks)
            .build()
    }

    override fun verifyPhoneNumber(options: PhoneAuthOptions) {
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun getCredential(verificationId: String, verificationCode: String): PhoneAuthCredential {
        return PhoneAuthProvider.getCredential(verificationId, verificationCode)
    }

    override fun getMultiFactorAssertion(credential: PhoneAuthCredential): PhoneMultiFactorAssertion {
        return PhoneMultiFactorGenerator.getAssertion(credential)
    }
}
