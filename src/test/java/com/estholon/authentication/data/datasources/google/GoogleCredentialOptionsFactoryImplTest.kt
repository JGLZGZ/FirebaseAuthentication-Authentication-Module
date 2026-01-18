package com.estholon.authentication.data.datasources.google

import android.os.Bundle
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class GoogleCredentialOptionsFactoryImplTest {

    private lateinit var factory: GoogleCredentialOptionsFactoryImpl

    companion object {
        private const val TEST_SERVER_CLIENT_ID = "test-server-client-id.apps.googleusercontent.com"
        private const val TEST_NONCE = "test-nonce-12345"
        private const val BUNDLE_KEY_ID = "com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID"
        private const val BUNDLE_KEY_ID_TOKEN = "com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN"
        private const val BUNDLE_KEY_DISPLAY_NAME = "com.google.android.libraries.identity.googleid.BUNDLE_KEY_DISPLAY_NAME"
        private const val BUNDLE_KEY_GIVEN_NAME = "com.google.android.libraries.identity.googleid.BUNDLE_KEY_GIVEN_NAME"
        private const val BUNDLE_KEY_FAMILY_NAME = "com.google.android.libraries.identity.googleid.BUNDLE_KEY_FAMILY_NAME"
        private const val BUNDLE_KEY_PROFILE_PICTURE_URI = "com.google.android.libraries.identity.googleid.BUNDLE_KEY_PROFILE_PICTURE_URI"
    }

    @Before
    fun setUp() {
        factory = GoogleCredentialOptionsFactoryImpl()
    }

    // ==================== CONSTRUCTOR TESTS ====================

    @Test
    fun `factory can be instantiated`() {
        val newFactory = GoogleCredentialOptionsFactoryImpl()
        assertNotNull(newFactory)
    }

    // ==================== createGoogleIdOption TESTS ====================

    @Test
    fun `createGoogleIdOption with filterByAuthorizedAccounts true returns valid option`() {
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
    fun `createGoogleIdOption with filterByAuthorizedAccounts false returns valid option`() {
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
    fun `createGoogleIdOption with autoSelectEnabled true returns valid option`() {
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
    fun `createGoogleIdOption with autoSelectEnabled false returns valid option`() {
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
    fun `createGoogleIdOption with different nonce returns valid option`() {
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

    @Test
    fun `createGoogleIdOption with empty nonce returns valid option`() {
        val result = factory.createGoogleIdOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = "",
            filterByAuthorizedAccounts = true,
            autoSelectEnabled = true
        )

        assertNotNull(result)
        assertTrue(result is GetGoogleIdOption)
    }

    @Test
    fun `createGoogleIdOption with different serverClientId returns valid option`() {
        val customClientId = "another-client-id.apps.googleusercontent.com"
        val result = factory.createGoogleIdOption(
            serverClientId = customClientId,
            nonce = TEST_NONCE,
            filterByAuthorizedAccounts = true,
            autoSelectEnabled = true
        )

        assertNotNull(result)
        assertTrue(result is GetGoogleIdOption)
    }

    @Test
    fun `createGoogleIdOption with all parameter combinations works`() {
        val combinations = listOf(
            Pair(true, true),
            Pair(true, false),
            Pair(false, true),
            Pair(false, false)
        )

        combinations.forEach { (filterByAuthorized, autoSelect) ->
            val result = factory.createGoogleIdOption(
                serverClientId = TEST_SERVER_CLIENT_ID,
                nonce = TEST_NONCE,
                filterByAuthorizedAccounts = filterByAuthorized,
                autoSelectEnabled = autoSelect
            )
            assertNotNull("Failed for filterByAuthorized=$filterByAuthorized, autoSelect=$autoSelect", result)
        }
    }

    // ==================== createSignInWithGoogleOption TESTS ====================

    @Test
    fun `createSignInWithGoogleOption returns valid option`() {
        val result = factory.createSignInWithGoogleOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE
        )

        assertNotNull(result)
        assertTrue(result is GetSignInWithGoogleOption)
    }

    @Test
    fun `createSignInWithGoogleOption with different serverClientId returns valid option`() {
        val customServerId = "another-client-id.apps.googleusercontent.com"
        val result = factory.createSignInWithGoogleOption(
            serverClientId = customServerId,
            nonce = TEST_NONCE
        )

        assertNotNull(result)
        assertTrue(result is GetSignInWithGoogleOption)
    }

    @Test
    fun `createSignInWithGoogleOption with different nonce returns valid option`() {
        val customNonce = "different-nonce-value"
        val result = factory.createSignInWithGoogleOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = customNonce
        )

        assertNotNull(result)
        assertTrue(result is GetSignInWithGoogleOption)
    }

    @Test
    fun `createSignInWithGoogleOption with empty nonce returns valid option`() {
        val result = factory.createSignInWithGoogleOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = ""
        )

        assertNotNull(result)
        assertTrue(result is GetSignInWithGoogleOption)
    }

    // ==================== createCredentialRequest TESTS ====================

    @Test
    fun `createCredentialRequest with GoogleIdOption returns valid request`() {
        val googleIdOption = factory.createGoogleIdOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE,
            filterByAuthorizedAccounts = true,
            autoSelectEnabled = true
        )

        val result = factory.createCredentialRequest(googleIdOption)

        assertNotNull(result)
        assertTrue(result is GetCredentialRequest)
        assertNotNull(result.credentialOptions)
        assertTrue(result.credentialOptions.isNotEmpty())
        assertEquals(1, result.credentialOptions.size)
    }

    @Test
    fun `createCredentialRequest contains GoogleIdOption`() {
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

    @Test
    fun `createCredentialRequest with different GoogleIdOption configurations`() {
        val option1 = factory.createGoogleIdOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE,
            filterByAuthorizedAccounts = true,
            autoSelectEnabled = true
        )
        val option2 = factory.createGoogleIdOption(
            serverClientId = "different-id",
            nonce = "different-nonce",
            filterByAuthorizedAccounts = false,
            autoSelectEnabled = false
        )

        val request1 = factory.createCredentialRequest(option1)
        val request2 = factory.createCredentialRequest(option2)

        assertNotNull(request1)
        assertNotNull(request2)
        assertEquals(1, request1.credentialOptions.size)
        assertEquals(1, request2.credentialOptions.size)
    }

    // ==================== createCredentialRequestWithSignIn TESTS ====================

    @Test
    fun `createCredentialRequestWithSignIn with SignInOption returns valid request`() {
        val signInOption = factory.createSignInWithGoogleOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE
        )

        val result = factory.createCredentialRequestWithSignIn(signInOption)

        assertNotNull(result)
        assertTrue(result is GetCredentialRequest)
        assertNotNull(result.credentialOptions)
        assertTrue(result.credentialOptions.isNotEmpty())
        assertEquals(1, result.credentialOptions.size)
    }

    @Test
    fun `createCredentialRequestWithSignIn contains SignInOption`() {
        val signInOption = factory.createSignInWithGoogleOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE
        )

        val result = factory.createCredentialRequestWithSignIn(signInOption)

        val containedOption = result.credentialOptions.first()
        assertTrue(containedOption is GetSignInWithGoogleOption)
    }

    @Test
    fun `createCredentialRequestWithSignIn with different configurations`() {
        val option1 = factory.createSignInWithGoogleOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE
        )
        val option2 = factory.createSignInWithGoogleOption(
            serverClientId = "different-id",
            nonce = "different-nonce"
        )

        val request1 = factory.createCredentialRequestWithSignIn(option1)
        val request2 = factory.createCredentialRequestWithSignIn(option2)

        assertNotNull(request1)
        assertNotNull(request2)
        assertEquals(1, request1.credentialOptions.size)
        assertEquals(1, request2.credentialOptions.size)
    }

    // ==================== parseGoogleIdTokenCredential TESTS ====================

    @Test
    fun `parseGoogleIdTokenCredential with valid bundle returns credential`() {
        val bundle = Bundle().apply {
            putString(BUNDLE_KEY_ID, "test-user@gmail.com")
            putString(BUNDLE_KEY_ID_TOKEN, "mock-id-token-value")
            putString(BUNDLE_KEY_DISPLAY_NAME, "Test User")
            putString(BUNDLE_KEY_GIVEN_NAME, "Test")
            putString(BUNDLE_KEY_FAMILY_NAME, "User")
            putString(BUNDLE_KEY_PROFILE_PICTURE_URI, "https://example.com/photo.jpg")
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
    fun `parseGoogleIdTokenCredential with minimal bundle returns credential`() {
        val bundle = Bundle().apply {
            putString(BUNDLE_KEY_ID, "minimal@gmail.com")
            putString(BUNDLE_KEY_ID_TOKEN, "minimal-token")
        }

        val result = factory.parseGoogleIdTokenCredential(bundle)

        assertNotNull(result)
        assertEquals("minimal@gmail.com", result.id)
        assertEquals("minimal-token", result.idToken)
    }

    @Test
    fun `parseGoogleIdTokenCredential with optional fields null returns credential with null fields`() {
        val bundle = Bundle().apply {
            putString(BUNDLE_KEY_ID, "user@gmail.com")
            putString(BUNDLE_KEY_ID_TOKEN, "token-value")
        }

        val result = factory.parseGoogleIdTokenCredential(bundle)

        assertNotNull(result)
        assertEquals("user@gmail.com", result.id)
        assertEquals("token-value", result.idToken)
        assertNull(result.displayName)
        assertNull(result.givenName)
        assertNull(result.familyName)
    }

    @Test(expected = GoogleIdTokenParsingException::class)
    fun `parseGoogleIdTokenCredential with empty bundle throws exception`() {
        val emptyBundle = Bundle()
        factory.parseGoogleIdTokenCredential(emptyBundle)
    }

    @Test(expected = GoogleIdTokenParsingException::class)
    fun `parseGoogleIdTokenCredential with missing id throws exception`() {
        val bundle = Bundle().apply {
            putString(BUNDLE_KEY_ID_TOKEN, "token-only")
        }
        factory.parseGoogleIdTokenCredential(bundle)
    }

    @Test(expected = GoogleIdTokenParsingException::class)
    fun `parseGoogleIdTokenCredential with missing token throws exception`() {
        val bundle = Bundle().apply {
            putString(BUNDLE_KEY_ID, "id-only@gmail.com")
        }
        factory.parseGoogleIdTokenCredential(bundle)
    }

    @Test(expected = GoogleIdTokenParsingException::class)
    fun `parseGoogleIdTokenCredential with null id throws exception`() {
        val bundle = Bundle().apply {
            putString(BUNDLE_KEY_ID, null)
            putString(BUNDLE_KEY_ID_TOKEN, "token-value")
        }
        factory.parseGoogleIdTokenCredential(bundle)
    }

    @Test(expected = GoogleIdTokenParsingException::class)
    fun `parseGoogleIdTokenCredential with null token throws exception`() {
        val bundle = Bundle().apply {
            putString(BUNDLE_KEY_ID, "user@gmail.com")
            putString(BUNDLE_KEY_ID_TOKEN, null)
        }
        factory.parseGoogleIdTokenCredential(bundle)
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    fun `full flow with createGoogleIdOption and createCredentialRequest works`() {
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
    fun `full flow with createSignInOption and createCredentialRequestWithSignIn works`() {
        val signInOption = factory.createSignInWithGoogleOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE
        )

        val request = factory.createCredentialRequestWithSignIn(signInOption)

        assertNotNull(request)
        assertEquals(1, request.credentialOptions.size)
        assertTrue(request.credentialOptions.first() is GetSignInWithGoogleOption)
    }

    @Test
    fun `multiple requests can be created independently`() {
        val option1 = factory.createGoogleIdOption(
            serverClientId = TEST_SERVER_CLIENT_ID,
            nonce = TEST_NONCE,
            filterByAuthorizedAccounts = true,
            autoSelectEnabled = true
        )
        val option2 = factory.createGoogleIdOption(
            serverClientId = "other-client",
            nonce = "other-nonce",
            filterByAuthorizedAccounts = false,
            autoSelectEnabled = false
        )

        val request1 = factory.createCredentialRequest(option1)
        val request2 = factory.createCredentialRequest(option2)

        assertNotNull(request1)
        assertNotNull(request2)
    }
}
