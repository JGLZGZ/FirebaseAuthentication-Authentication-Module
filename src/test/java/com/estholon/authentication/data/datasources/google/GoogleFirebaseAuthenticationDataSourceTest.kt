package com.estholon.authentication.data.datasources.google

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import com.estholon.authentication.R
import com.estholon.authentication.data.datasources.email.EmailAuthenticationDataSource
import com.estholon.authentication.data.dtos.UserDto
import com.google.firebase.auth.FirebaseAuth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GoogleFirebaseAuthenticationDataSource.
 *
 * Note: Many methods in this datasource create Android-specific objects (GetGoogleIdOption.Builder,
 * GetCredentialRequest.Builder) that require Android runtime to function. These methods must be
 * tested via Android instrumented tests.
 *
 * This test class covers:
 * - handleCredentialResponse: All credential type branches
 * - clearCredentialState: Success and error handling
 * - linkGoogle: Early return when no user is logged in
 *
 * For full coverage, create instrumented tests (androidTest) for:
 * - signInGoogleCredentialManager: All success/failure paths
 * - signInGoogle: All success/failure paths
 * - linkGoogle: Success path with credentials and all failure scenarios after user check
 */
class GoogleFirebaseAuthenticationDataSourceTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var emailAuthenticationDataSource: EmailAuthenticationDataSource
    private lateinit var context: Context
    private lateinit var credentialManager: CredentialManager
    private lateinit var dataSource: GoogleFirebaseAuthenticationDataSource
    private lateinit var activity: Activity

    @Before
    fun setup() {
        // Mock Android Log to avoid "not mocked" errors
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        firebaseAuth = mockk()
        emailAuthenticationDataSource = mockk()
        context = mockk(relaxed = true)
        credentialManager = mockk()
        activity = mockk(relaxed = true)

        // Mock context.getString for web client id
        every { context.getString(R.string.default_web_client_id) } returns "mock-web-client-id"

        dataSource = GoogleFirebaseAuthenticationDataSource(
            firebaseAuth,
            emailAuthenticationDataSource,
            context,
            credentialManager
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ==================== HANDLE CREDENTIAL RESPONSE TESTS ====================

    @Test
    fun `handleCredentialResponse handles PasswordCredential by delegating to emailDS`() = runTest {
        // Given
        val credentialResponse = mockk<GetCredentialResponse>()
        val passwordCredential = mockk<PasswordCredential>()
        every { credentialResponse.credential } returns passwordCredential
        every { passwordCredential.id } returns "email@test.com"
        every { passwordCredential.password } returns "password123"

        val userDto = UserDto("uid", "email@test.com", null, null)
        coEvery { emailAuthenticationDataSource.signInEmail("email@test.com", "password123") } returns Result.success(userDto)

        // When
        val result = dataSource.handleCredentialResponse(credentialResponse)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(userDto, result.getOrNull())
        coVerify { emailAuthenticationDataSource.signInEmail("email@test.com", "password123") }
    }

    @Test
    fun `handleCredentialResponse returns failure when email signIn fails`() = runTest {
        // Given
        val credentialResponse = mockk<GetCredentialResponse>()
        val passwordCredential = mockk<PasswordCredential>()
        every { credentialResponse.credential } returns passwordCredential
        every { passwordCredential.id } returns "email@test.com"
        every { passwordCredential.password } returns "password123"

        coEvery { emailAuthenticationDataSource.signInEmail("email@test.com", "password123") } returns
            Result.failure(Exception("Email sign in failed"))

        // When
        val result = dataSource.handleCredentialResponse(credentialResponse)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Email sign in failed") == true)
    }

    @Test
    fun `handleCredentialResponse returns failure for PublicKeyCredential`() = runTest {
        // Given
        val credentialResponse = mockk<GetCredentialResponse>()
        val publicKeyCredential = mockk<PublicKeyCredential>()
        every { credentialResponse.credential } returns publicKeyCredential

        // When
        val result = dataSource.handleCredentialResponse(credentialResponse)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Passkeys no implementadas aún", result.exceptionOrNull()?.message)
    }

    @Test
    fun `handleCredentialResponse returns failure for unsupported CustomCredential type`() = runTest {
        // Given
        val credentialResponse = mockk<GetCredentialResponse>()
        val customCredential = mockk<CustomCredential>()
        every { credentialResponse.credential } returns customCredential
        every { customCredential.type } returns "unsupported.credential.type"

        // When
        val result = dataSource.handleCredentialResponse(credentialResponse)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Tipo de credencial no soportada", result.exceptionOrNull()?.message)
    }

    @Test
    fun `handleCredentialResponse returns failure for unknown credential type`() = runTest {
        // Given
        val credentialResponse = mockk<GetCredentialResponse>()
        val unknownCredential = mockk<androidx.credentials.Credential>()
        every { credentialResponse.credential } returns unknownCredential

        // When
        val result = dataSource.handleCredentialResponse(credentialResponse)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Tipo de credencial no reconocida", result.exceptionOrNull()?.message)
    }

    // ==================== CLEAR CREDENTIAL STATE TESTS ====================

    @Test
    fun `clearCredentialState calls credentialManager successfully`() = runTest {
        // Given
        coEvery { credentialManager.clearCredentialState(any()) } returns Unit

        // When
        dataSource.clearCredentialState()

        // Then
        coVerify { credentialManager.clearCredentialState(any()) }
    }

    @Test
    fun `clearCredentialState handles exception gracefully`() = runTest {
        // Given
        coEvery { credentialManager.clearCredentialState(any()) } throws Exception("Clear failed")

        // When - should not throw
        dataSource.clearCredentialState()

        // Then - just verify it was called (exception is caught internally)
        coVerify { credentialManager.clearCredentialState(any()) }
    }

    // ==================== LINK GOOGLE TESTS ====================

    @Test
    fun `linkGoogle returns failure when no current user`() = runTest {
        // Given
        every { firebaseAuth.currentUser } returns null

        // When
        val result = dataSource.linkGoogle(activity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("No hay usuario autenticado", result.exceptionOrNull()?.message)
    }

    // Note: Additional linkGoogle tests require Android instrumented tests because
    // the method creates GetGoogleIdOption.Builder() which uses Android Bundle internally.
    // Tests for success paths, credential type handling during linking, and exception
    // handling after the user check should be in androidTest.

    // ==================== SIGN IN METHODS ====================

    // Note: signInGoogleCredentialManager and signInGoogle tests require Android instrumented
    // tests because these methods create GetGoogleIdOption.Builder() and GetSignInWithGoogleOption.Builder()
    // which use Android-specific classes that cannot be mocked in unit tests.
    //
    // Recommended instrumented test coverage:
    // - signInGoogleCredentialManager: success, fallback to signup, failure scenarios
    // - signInGoogle: success, various GetCredentialException types
    // - linkGoogle: success, credential type handling, linking failures
    // - handleCredentialResponse: GoogleIdTokenCredential success and parsing errors
}
