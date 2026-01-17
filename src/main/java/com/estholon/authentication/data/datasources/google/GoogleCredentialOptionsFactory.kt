package com.estholon.authentication.data.datasources.google

import android.os.Bundle
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import javax.inject.Inject

/**
 * Factory interface for creating Google credential-related objects.
 * This abstraction allows for easier testing by enabling mocking of Android-specific classes.
 */
interface GoogleCredentialOptionsFactory {

    /**
     * Creates a GetGoogleIdOption for sign-in with authorized accounts filter.
     */
    fun createGoogleIdOption(
        serverClientId: String,
        nonce: String,
        filterByAuthorizedAccounts: Boolean,
        autoSelectEnabled: Boolean
    ): GetGoogleIdOption

    /**
     * Creates a GetSignInWithGoogleOption for direct Google sign-in.
     */
    fun createSignInWithGoogleOption(
        serverClientId: String,
        nonce: String
    ): GetSignInWithGoogleOption

    /**
     * Creates a GetCredentialRequest with the given Google ID option.
     */
    fun createCredentialRequest(googleIdOption: GetGoogleIdOption): GetCredentialRequest

    /**
     * Creates a GetCredentialRequest with the given sign-in option.
     */
    fun createCredentialRequestWithSignIn(signInOption: GetSignInWithGoogleOption): GetCredentialRequest

    /**
     * Parses GoogleIdTokenCredential from Bundle data.
     */
    fun parseGoogleIdTokenCredential(data: Bundle): GoogleIdTokenCredential
}

/**
 * Default implementation of GoogleCredentialOptionsFactory.
 */
class GoogleCredentialOptionsFactoryImpl @Inject constructor() : GoogleCredentialOptionsFactory {

    override fun createGoogleIdOption(
        serverClientId: String,
        nonce: String,
        filterByAuthorizedAccounts: Boolean,
        autoSelectEnabled: Boolean
    ): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(autoSelectEnabled)
            .setNonce(nonce)
            .build()
    }

    override fun createSignInWithGoogleOption(
        serverClientId: String,
        nonce: String
    ): GetSignInWithGoogleOption {
        return GetSignInWithGoogleOption.Builder(serverClientId)
            .setNonce(nonce)
            .build()
    }

    override fun createCredentialRequest(googleIdOption: GetGoogleIdOption): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    override fun createCredentialRequestWithSignIn(signInOption: GetSignInWithGoogleOption): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()
    }

    override fun parseGoogleIdTokenCredential(data: Bundle): GoogleIdTokenCredential {
        return GoogleIdTokenCredential.createFrom(data)
    }
}