package com.estholon.authentication.data.datasources.google

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoogleCredentialOptionsFactoryImplTest {

    private lateinit var factory: GoogleCredentialOptionsFactoryImpl

    companion object {
        private const val TEST_SERVER_CLIENT_ID = "test-server-client-id.apps.googleusercontent.com"
        private const val TEST_NONCE = "test-nonce-12345"
    }

    @Before
    fun setUp() {
        factory = GoogleCredentialOptionsFactoryImpl()
    }

    // createGoogleIdOption tests

    @Test
    fun createGoogleIdOption_withFilterByAuthorizedAccountsTrue_returnsValidOption() {
        val result = factory.createGoogleIdOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE,
            filterByAuthorizedAccounts = true,
            autoSelectEnabled = true
        )

        assertNotNull(result)
        assertTrue(result is GetGoogleIdOption)
    }

    @Test
    fun createGoogleIdOption_withFilterByAuthorizedAccountsFalse_returnsValidOption() {
        val result = factory.createGoogleIdOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE,
            filterByAuthorizedAccounts = false,
            autoSelectEnabled = false
        )

        assertNotNull(result)
        assertTrue(result is GetGoogleIdOption)
    }

    @Test
    fun createGoogleIdOption_withAutoSelectEnabledTrue_returnsValidOption() {
        val result = factory.createGoogleIdOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE,
            filterByAuthorizedAccounts = false,
            autoSelectEnabled = true
        )

        assertNotNull(result)
        assertTrue(result is GetGoogleIdOption)
    }

    @Test
    fun createGoogleIdOption_withAutoSelectEnabledFalse_returnsValidOption() {
        val result = factory.createGoogleIdOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE,
            filterByAuthorizedAccounts = true,
            autoSelectEnabled = false
        )

        assertNotNull(result)
        assertTrue(result is GetGoogleIdOption)
    }

    @Test
    fun createGoogleIdOption_withDifferentNonce_returnsValidOption() {
        val customNonce = "custom-nonce-value-67890"
        val result = factory.createGoogleIdOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = customNonce,
            filterByAuthorizedAccounts = true,
            autoSelectEnabled = true
        )

        assertNotNull(result)
        assertTrue(result is GetGoogleIdOption)
    }

    // createSignInWithGoogleOption tests

    @Test
    fun createSignInWithGoogleOption_returnsValidOption() {
        val result = factory.createSignInWithGoogleOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE
        )

        assertNotNull(result)
        assertTrue(result is GetSignInWithGoogleOption)
    }

    @Test
    fun createSignInWithGoogleOption_withDifferentServerClientId_returnsValidOption() {
        val customServerId = "another-client-id.apps.googleusercontent.com"
        val result = factory.createSignInWithGoogleOption(
            serverClientId = customServerId,
            nonce = TEST_NONCE
        )

        assertNotNull(result)
        assertTrue(result is GetSignInWithGoogleOption)
    }

    @Test
    fun createSignInWithGoogleOption_withDifferentNonce_returnsValidOption() {
        val customNonce = "different-nonce-value"
        val result = factory.createSignInWithGoogleOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = customNonce
        )

        assertNotNull(result)
        assertTrue(result is GetSignInWithGoogleOption)
    }

    // createCredentialRequest tests

    @Test
    fun createCredentialRequest_withGoogleIdOption_returnsValidRequest() {
        val googleIdOption = factory.createGoogleIdOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE,
            filterByAuthorizedAccounts = true,
            autoSelectEnabled = true
        )

        val result = factory.createCredentialRequest(googleIdOption)

        assertNotNull(result)
        assertNotNull(result.credentialOptions)
        assertTrue(result.credentialOptions.isNotEmpty())
        assertEquals(1, result.credentialOptions.size)
    }

    @Test
    fun createCredentialRequest_containsGoogleIdOption() {
        val googleIdOption = factory.createGoogleIdOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE,
            filterByAuthorizedAccounts = false,
            autoSelectEnabled = false
        )

        val result = factory.createCredentialRequest(googleIdOption)

        val containedOption = result.credentialOptions.first()
        assertTrue(containedOption is GetGoogleIdOption)
    }

    // createCredentialRequestWithSignIn tests

    @Test
    fun createCredentialRequestWithSignIn_withSignInOption_returnsValidRequest() {
        val signInOption = factory.createSignInWithGoogleOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE
        )

        val result = factory.createCredentialRequestWithSignIn(signInOption)

        assertNotNull(result)
        assertNotNull(result.credentialOptions)
        assertTrue(result.credentialOptions.isNotEmpty())
        assertEquals(1, result.credentialOptions.size)
    }

    @Test
    fun createCredentialRequestWithSignIn_containsSignInOption() {
        val signInOption = factory.createSignInWithGoogleOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE
        )

        val result = factory.createCredentialRequestWithSignIn(signInOption)

        val containedOption = result.credentialOptions.first()
        assertTrue(containedOption is GetSignInWithGoogleOption)
    }

    // parseGoogleIdTokenCredential tests

    @Test
    fun parseGoogleIdTokenCredential_withValidBundle_returnsCredential() {
        val bundle = Bundle().apply {
            putString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID", "test-user@gmail.com")
            putString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN", "mock-id-token-value")
            putString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_DISPLAY_NAME", "Test User")
            putString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_GIVEN_NAME", "Test")
            putString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_FAMILY_NAME", "User")
            putString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_PROFILE_PICTURE_URI", "https://example.com/photo.jpg")
        }

        val result = factory.parseGoogleIdTokenCredential(bundle)

        assertNotNull(result)
        assertTrue(result is GoogleIdTokenCredential)
        assertEquals("test-user@gmail.com", result.id)
        assertEquals("mock-id-token-value", result.idToken)
        assertEquals("Test User", result.displayName)
        assertEquals("Test", result.givenName)
        assertEquals("User", result.familyName)
    }

    @Test
    fun parseGoogleIdTokenCredential_withMinimalBundle_returnsCredential() {
        val bundle = Bundle().apply {
            putString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID", "minimal@gmail.com")
            putString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN", "minimal-token")
        }

        val result = factory.parseGoogleIdTokenCredential(bundle)

        assertNotNull(result)
        assertEquals("minimal@gmail.com", result.id)
        assertEquals("minimal-token", result.idToken)
    }

    // Integration tests - combining multiple methods

    @Test
    fun fullFlow_createGoogleIdOptionAndCredentialRequest_works() {
        val googleIdOption = factory.createGoogleIdOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE,
            filterByAuthorizedAccounts = true,
            autoSelectEnabled = true
        )

        val request = factory.createCredentialRequest(googleIdOption)

        assertNotNull(request)
        assertEquals(1, request.credentialOptions.size)
        assertTrue(request.credentialOptions.first() is GetGoogleIdOption)
    }

    @Test
    fun fullFlow_createSignInOptionAndCredentialRequest_works() {
        val signInOption = factory.createSignInWithGoogleOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE
        )

        val request = factory.createCredentialRequestWithSignIn(signInOption)

        assertNotNull(request)
        assertEquals(1, request.credentialOptions.size)
        assertTrue(request.credentialOptions.first() is GetSignInWithGoogleOption)
    }
}
