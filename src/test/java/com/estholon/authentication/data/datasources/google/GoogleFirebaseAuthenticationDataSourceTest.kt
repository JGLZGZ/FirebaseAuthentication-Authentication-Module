package com.estholon.authentication.data.datasources.google

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import com.estholon.authentication.R
import com.estholon.authentication.data.datasources.email.EmailAuthenticationDataSource
import com.estholon.authentication.data.dtos.UserDto
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GoogleFirebaseAuthenticationDataSourceTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var emailAuthenticationDataSource: EmailAuthenticationDataSource
    private lateinit var context: Context
    private lateinit var credentialManager: CredentialManager
    private lateinit var credentialOptionsFactory: GoogleCredentialOptionsFactory
    private lateinit var dataSource: GoogleFirebaseAuthenticationDataSource
    private lateinit var activity: Activity

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        mockkStatic(GoogleAuthProvider::class)

        firebaseAuth = mockk()
        emailAuthenticationDataSource = mockk()
        context = mockk(relaxed = true)
        credentialManager = mockk()
        credentialOptionsFactory = mockk()
        activity = mockk(relaxed = true)

        every { context.getString(R.string.default_web_client_id) } returns "mock-web-client-id"

        dataSource = GoogleFirebaseAuthenticationDataSource(
            firebaseAuth,
            emailAuthenticationDataSource,
            context,
            credentialManager,
            credentialOptionsFactory
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ==================== GENERATE NONCE TESTS ====================

    @Test
    fun `generateNonce returns 64 character hex string`() {
        val nonce = dataSource.generateNonce()
        assertEquals(64, nonce.length)
        assertTrue(nonce.all { it in '0'..'9' || it in 'a'..'f' })
    }

    // ==================== HANDLE CREDENTIAL RESPONSE TESTS ====================

    @Test
    fun `handleCredentialResponse handles PasswordCredential successfully`() = runTest {
        val credentialResponse = mockk<GetCredentialResponse>()
        val passwordCredential = mockk<PasswordCredential>()
        every { credentialResponse.credential } returns passwordCredential
        every { passwordCredential.id } returns "email@test.com"
        every { passwordCredential.password } returns "password123"

        val userDto = UserDto("uid", "email@test.com", null, null)
        coEvery { emailAuthenticationDataSource.signInEmail("email@test.com", "password123") } returns Result.success(userDto)

        val result = dataSource.handleCredentialResponse(credentialResponse)

        assertTrue(result.isSuccess)
        assertEquals(userDto, result.getOrNull())
    }

    @Test
    fun `handleCredentialResponse handles PasswordCredential failure`() = runTest {
        val credentialResponse = mockk<GetCredentialResponse>()
        val passwordCredential = mockk<PasswordCredential>()
        every { credentialResponse.credential } returns passwordCredential
        every { passwordCredential.id } returns "email@test.com"
        every { passwordCredential.password } returns "password123"

        coEvery { emailAuthenticationDataSource.signInEmail("email@test.com", "password123") } returns
            Result.failure(Exception("Email sign in failed"))

        val result = dataSource.handleCredentialResponse(credentialResponse)

        assertTrue(result.isFailure)
    }

    @Test
    fun `handleCredentialResponse returns failure for PublicKeyCredential`() = runTest {
        val credentialResponse = mockk<GetCredentialResponse>()
        val publicKeyCredential = mockk<PublicKeyCredential>()
        every { credentialResponse.credential } returns publicKeyCredential

        val result = dataSource.handleCredentialResponse(credentialResponse)

        assertTrue(result.isFailure)
        assertEquals("Passkeys no implementadas aún", result.exceptionOrNull()?.message)
    }

    @Test
    fun `handleCredentialResponse returns failure for unsupported CustomCredential`() = runTest {
        val credentialResponse = mockk<GetCredentialResponse>()
        val customCredential = mockk<CustomCredential>()
        every { credentialResponse.credential } returns customCredential
        every { customCredential.type } returns "unsupported.type"

        val result = dataSource.handleCredentialResponse(credentialResponse)

        assertTrue(result.isFailure)
        assertEquals("Tipo de credencial no soportada", result.exceptionOrNull()?.message)
    }

    @Test
    fun `handleCredentialResponse returns failure for unknown credential`() = runTest {
        val credentialResponse = mockk<GetCredentialResponse>()
        val unknownCredential = mockk<androidx.credentials.Credential>()
        every { credentialResponse.credential } returns unknownCredential

        val result = dataSource.handleCredentialResponse(credentialResponse)

        assertTrue(result.isFailure)
        assertEquals("Tipo de credencial no reconocida", result.exceptionOrNull()?.message)
    }

    @Test
    fun `handleCredentialResponse handles GoogleIdTokenCredential successfully`() = runTest {
        val credentialResponse = mockk<GetCredentialResponse>()
        val customCredential = mockk<CustomCredential>()
        val mockBundle = mockk<Bundle>(relaxed = true)
        val googleIdTokenCredential = mockk<GoogleIdTokenCredential>()

        every { credentialResponse.credential } returns customCredential
        every { customCredential.type } returns GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        every { customCredential.data } returns mockBundle
        every { credentialOptionsFactory.parseGoogleIdTokenCredential(mockBundle) } returns googleIdTokenCredential
        every { googleIdTokenCredential.idToken } returns "mock-id-token"
        every { googleIdTokenCredential.id } returns "user-id"
        every { googleIdTokenCredential.displayName } returns "Test User"
        every { googleIdTokenCredential.givenName } returns "test@example.com"

        val authCredential = mockk<AuthCredential>()
        every { GoogleAuthProvider.getCredential("mock-id-token", null) } returns authCredential

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()
        val firebaseUser = mockk<FirebaseUser>()

        every { firebaseAuth.signInWithCredential(authCredential) } returns task
        every { authResult.user } returns firebaseUser
        every { firebaseUser.uid } returns "firebase-uid"
        every { firebaseUser.email } returns "test@example.com"
        every { firebaseUser.displayName } returns "Test User"
        every { firebaseUser.phoneNumber } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        val result = dataSource.handleCredentialResponse(credentialResponse)

        assertTrue(result.isSuccess)
        assertEquals("firebase-uid", result.getOrNull()?.uid)
    }

    @Test
    fun `handleCredentialResponse returns failure on GoogleIdTokenParsingException`() = runTest {
        val credentialResponse = mockk<GetCredentialResponse>()
        val customCredential = mockk<CustomCredential>()
        val mockBundle = mockk<Bundle>(relaxed = true)

        every { credentialResponse.credential } returns customCredential
        every { customCredential.type } returns GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        every { customCredential.data } returns mockBundle
        every { credentialOptionsFactory.parseGoogleIdTokenCredential(mockBundle) } throws
            GoogleIdTokenParsingException(Exception("Parse error"))

        val result = dataSource.handleCredentialResponse(credentialResponse)

        assertTrue(result.isFailure)
        assertEquals("Token de Google inválido", result.exceptionOrNull()?.message)
    }

    // ==================== CLEAR CREDENTIAL STATE TESTS ====================

    @Test
    fun `clearCredentialState calls credentialManager successfully`() = runTest {
        coEvery { credentialManager.clearCredentialState(any()) } returns Unit

        dataSource.clearCredentialState()

        coVerify { credentialManager.clearCredentialState(any()) }
    }

    @Test
    fun `clearCredentialState handles exception gracefully`() = runTest {
        coEvery { credentialManager.clearCredentialState(any()) } throws Exception("Clear failed")

        dataSource.clearCredentialState()

        coVerify { credentialManager.clearCredentialState(any()) }
    }

    // ==================== SIGN IN WITH GOOGLE ID TOKEN TESTS ====================

    @Test
    fun `signInWithGoogleIdToken returns success when user exists`() = runTest {
        val authCredential = mockk<AuthCredential>()
        every { GoogleAuthProvider.getCredential("test-token", null) } returns authCredential

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()
        val firebaseUser = mockk<FirebaseUser>()

        every { firebaseAuth.signInWithCredential(authCredential) } returns task
        every { authResult.user } returns firebaseUser
        every { firebaseUser.uid } returns "uid"
        every { firebaseUser.email } returns "email@test.com"
        every { firebaseUser.displayName } returns "Test User"
        every { firebaseUser.phoneNumber } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        val result = dataSource.signInWithGoogleIdToken("test-token")

        assertTrue(result.isSuccess)
        assertEquals("uid", result.getOrNull()?.uid)
    }

    @Test
    fun `signInWithGoogleIdToken returns failure when user is null`() = runTest {
        val authCredential = mockk<AuthCredential>()
        every { GoogleAuthProvider.getCredential("test-token", null) } returns authCredential

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()

        every { firebaseAuth.signInWithCredential(authCredential) } returns task
        every { authResult.user } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        val result = dataSource.signInWithGoogleIdToken("test-token")

        assertTrue(result.isFailure)
        assertEquals("Error al iniciar sesión", result.exceptionOrNull()?.message)
    }

    @Test
    fun `signInWithGoogleIdToken returns failure on Firebase error`() = runTest {
        val authCredential = mockk<AuthCredential>()
        every { GoogleAuthProvider.getCredential("test-token", null) } returns authCredential

        val task = mockk<Task<AuthResult>>()
        every { firebaseAuth.signInWithCredential(authCredential) } returns task

        every { task.addOnSuccessListener(any()) } returns task
        val failureSlot = slot<OnFailureListener>()
        every { task.addOnFailureListener(capture(failureSlot)) } answers {
            failureSlot.captured.onFailure(Exception("Firebase error"))
            task
        }

        val result = dataSource.signInWithGoogleIdToken("test-token")

        assertTrue(result.isFailure)
    }

    // ==================== SIGN IN GOOGLE CREDENTIAL MANAGER TESTS ====================

    @Test
    fun `signInGoogleCredentialManager returns success on first try`() = runTest {
        val googleIdOption = mockk<GetGoogleIdOption>()
        val request = mockk<GetCredentialRequest>()
        val credentialResponse = mockk<GetCredentialResponse>()
        val passwordCredential = mockk<PasswordCredential>()

        every { credentialOptionsFactory.createGoogleIdOption(any(), any(), true, true) } returns googleIdOption
        every { credentialOptionsFactory.createCredentialRequest(googleIdOption) } returns request
        coEvery { credentialManager.getCredential(activity, request) } returns credentialResponse
        every { credentialResponse.credential } returns passwordCredential
        every { passwordCredential.id } returns "email@test.com"
        every { passwordCredential.password } returns "password"

        val userDto = UserDto("uid", "email@test.com", null, null)
        coEvery { emailAuthenticationDataSource.signInEmail(any(), any()) } returns Result.success(userDto)

        val result = dataSource.signInGoogleCredentialManager(activity)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `signInGoogleCredentialManager falls back to signUp on GetCredentialException`() = runTest {
        val googleIdOption = mockk<GetGoogleIdOption>()
        val request = mockk<GetCredentialRequest>()
        val credentialResponse = mockk<GetCredentialResponse>()
        val passwordCredential = mockk<PasswordCredential>()

        every { credentialOptionsFactory.createGoogleIdOption(any(), any(), true, true) } returns googleIdOption
        every { credentialOptionsFactory.createGoogleIdOption(any(), any(), false, false) } returns googleIdOption
        every { credentialOptionsFactory.createCredentialRequest(googleIdOption) } returns request

        var callCount = 0
        coEvery { credentialManager.getCredential(activity, request) } answers {
            callCount++
            if (callCount == 1) {
                throw mockk<GetCredentialException>(relaxed = true)
            } else {
                credentialResponse
            }
        }

        every { credentialResponse.credential } returns passwordCredential
        every { passwordCredential.id } returns "email@test.com"
        every { passwordCredential.password } returns "password"

        val userDto = UserDto("uid", "email@test.com", null, null)
        coEvery { emailAuthenticationDataSource.signInEmail(any(), any()) } returns Result.success(userDto)

        val result = dataSource.signInGoogleCredentialManager(activity)

        assertTrue(result.isSuccess)
        assertEquals(2, callCount)
    }

    @Test
    fun `signInGoogleCredentialManager returns failure when both attempts fail`() = runTest {
        val googleIdOption = mockk<GetGoogleIdOption>()
        val request = mockk<GetCredentialRequest>()

        every { credentialOptionsFactory.createGoogleIdOption(any(), any(), any(), any()) } returns googleIdOption
        every { credentialOptionsFactory.createCredentialRequest(googleIdOption) } returns request
        coEvery { credentialManager.getCredential(activity, request) } throws mockk<GetCredentialException>(relaxed = true)

        val result = dataSource.signInGoogleCredentialManager(activity)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Error al registrarse") == true)
    }

    @Test
    fun `signInGoogleCredentialManager returns failure on general exception`() = runTest {
        val googleIdOption = mockk<GetGoogleIdOption>()
        every { credentialOptionsFactory.createGoogleIdOption(any(), any(), any(), any()) } returns googleIdOption
        every { credentialOptionsFactory.createCredentialRequest(googleIdOption) } throws RuntimeException("Unexpected error")

        val result = dataSource.signInGoogleCredentialManager(activity)

        assertTrue(result.isFailure)
    }

    // ==================== SIGN IN GOOGLE TESTS ====================

    @Test
    fun `signInGoogle returns success`() = runTest {
        val signInOption = mockk<GetSignInWithGoogleOption>()
        val request = mockk<GetCredentialRequest>()
        val credentialResponse = mockk<GetCredentialResponse>()
        val passwordCredential = mockk<PasswordCredential>()

        every { credentialOptionsFactory.createSignInWithGoogleOption(any(), any()) } returns signInOption
        every { credentialOptionsFactory.createCredentialRequestWithSignIn(signInOption) } returns request
        coEvery { credentialManager.getCredential(activity, request) } returns credentialResponse
        every { credentialResponse.credential } returns passwordCredential
        every { passwordCredential.id } returns "email@test.com"
        every { passwordCredential.password } returns "password"

        val userDto = UserDto("uid", "email@test.com", null, null)
        coEvery { emailAuthenticationDataSource.signInEmail(any(), any()) } returns Result.success(userDto)

        val result = dataSource.signInGoogle(activity)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `signInGoogle returns failure on GetCredentialException`() = runTest {
        val signInOption = mockk<GetSignInWithGoogleOption>()
        val request = mockk<GetCredentialRequest>()

        every { credentialOptionsFactory.createSignInWithGoogleOption(any(), any()) } returns signInOption
        every { credentialOptionsFactory.createCredentialRequestWithSignIn(signInOption) } returns request
        coEvery { credentialManager.getCredential(activity, request) } throws mockk<GetCredentialException>(relaxed = true)

        val result = dataSource.signInGoogle(activity)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Error al iniciar sesión con Google") == true)
    }

    // ==================== LINK GOOGLE TESTS ====================

    @Test
    fun `linkGoogle returns failure when no current user`() = runTest {
        every { firebaseAuth.currentUser } returns null

        val result = dataSource.linkGoogle(activity)

        assertTrue(result.isFailure)
        assertEquals("No hay usuario autenticado", result.exceptionOrNull()?.message)
    }

    @Test
    fun `linkGoogle returns success on first try with GoogleIdTokenCredential`() = runTest {
        val currentUser = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns currentUser

        val googleIdOption = mockk<GetGoogleIdOption>()
        val request = mockk<GetCredentialRequest>()
        val credentialResponse = mockk<GetCredentialResponse>()
        val customCredential = mockk<CustomCredential>()
        val mockBundle = mockk<Bundle>(relaxed = true)
        val googleIdTokenCredential = mockk<GoogleIdTokenCredential>()

        every { credentialOptionsFactory.createGoogleIdOption(any(), any(), true, false) } returns googleIdOption
        every { credentialOptionsFactory.createCredentialRequest(googleIdOption) } returns request
        coEvery { credentialManager.getCredential(activity, request) } returns credentialResponse
        every { credentialResponse.credential } returns customCredential
        every { customCredential.type } returns GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        every { customCredential.data } returns mockBundle
        every { credentialOptionsFactory.parseGoogleIdTokenCredential(mockBundle) } returns googleIdTokenCredential
        every { googleIdTokenCredential.idToken } returns "mock-id-token"
        every { googleIdTokenCredential.id } returns "user-id"
        every { googleIdTokenCredential.displayName } returns "Test User"
        every { googleIdTokenCredential.givenName } returns "test@example.com"

        val authCredential = mockk<AuthCredential>()
        every { GoogleAuthProvider.getCredential("mock-id-token", null) } returns authCredential

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()
        val linkedUser = mockk<FirebaseUser>()

        every { currentUser.linkWithCredential(authCredential) } returns task
        every { authResult.user } returns linkedUser
        every { linkedUser.uid } returns "linked-uid"
        every { linkedUser.email } returns "test@example.com"
        every { linkedUser.displayName } returns "Test User"
        every { linkedUser.phoneNumber } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        val result = dataSource.linkGoogle(activity)

        assertTrue(result.isSuccess)
        assertEquals("linked-uid", result.getOrNull()?.uid)
    }

    @Test
    fun `linkGoogle falls back to all accounts on GetCredentialException`() = runTest {
        val currentUser = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns currentUser

        val googleIdOption = mockk<GetGoogleIdOption>()
        val request = mockk<GetCredentialRequest>()
        val credentialResponse = mockk<GetCredentialResponse>()
        val customCredential = mockk<CustomCredential>()
        val mockBundle = mockk<Bundle>(relaxed = true)
        val googleIdTokenCredential = mockk<GoogleIdTokenCredential>()

        every { credentialOptionsFactory.createGoogleIdOption(any(), any(), true, false) } returns googleIdOption
        every { credentialOptionsFactory.createGoogleIdOption(any(), any(), false, false) } returns googleIdOption
        every { credentialOptionsFactory.createCredentialRequest(googleIdOption) } returns request

        var callCount = 0
        coEvery { credentialManager.getCredential(activity, request) } answers {
            callCount++
            if (callCount == 1) {
                throw mockk<GetCredentialException>(relaxed = true)
            } else {
                credentialResponse
            }
        }

        every { credentialResponse.credential } returns customCredential
        every { customCredential.type } returns GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        every { customCredential.data } returns mockBundle
        every { credentialOptionsFactory.parseGoogleIdTokenCredential(mockBundle) } returns googleIdTokenCredential
        every { googleIdTokenCredential.idToken } returns "mock-id-token"
        every { googleIdTokenCredential.id } returns "user-id"
        every { googleIdTokenCredential.displayName } returns "Test User"
        every { googleIdTokenCredential.givenName } returns "test@example.com"

        val authCredential = mockk<AuthCredential>()
        every { GoogleAuthProvider.getCredential("mock-id-token", null) } returns authCredential

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()
        val linkedUser = mockk<FirebaseUser>()

        every { currentUser.linkWithCredential(authCredential) } returns task
        every { authResult.user } returns linkedUser
        every { linkedUser.uid } returns "linked-uid"
        every { linkedUser.email } returns "test@example.com"
        every { linkedUser.displayName } returns "Test User"
        every { linkedUser.phoneNumber } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        val result = dataSource.linkGoogle(activity)

        assertTrue(result.isSuccess)
        assertEquals(2, callCount)
    }

    @Test
    fun `linkGoogle returns failure when both attempts fail`() = runTest {
        val currentUser = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns currentUser

        val googleIdOption = mockk<GetGoogleIdOption>()
        val request = mockk<GetCredentialRequest>()

        every { credentialOptionsFactory.createGoogleIdOption(any(), any(), any(), any()) } returns googleIdOption
        every { credentialOptionsFactory.createCredentialRequest(googleIdOption) } returns request
        coEvery { credentialManager.getCredential(activity, request) } throws mockk<GetCredentialException>(relaxed = true)

        val result = dataSource.linkGoogle(activity)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Error al vincular con Google") == true)
    }

    @Test
    fun `linkGoogle returns failure on general exception`() = runTest {
        val currentUser = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns currentUser

        every { credentialOptionsFactory.createGoogleIdOption(any(), any(), any(), any()) } throws RuntimeException("Unexpected")

        val result = dataSource.linkGoogle(activity)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Error al vincular con Google") == true)
    }

    // ==================== HANDLE CREDENTIAL RESPONSE FOR LINKING TESTS ====================

    @Test
    fun `handleCredentialResponseForLinking returns failure for unsupported CustomCredential`() = runTest {
        val currentUser = mockk<FirebaseUser>()
        val credentialResponse = mockk<GetCredentialResponse>()
        val customCredential = mockk<CustomCredential>()

        every { credentialResponse.credential } returns customCredential
        every { customCredential.type } returns "unsupported.type"

        val result = dataSource.handleCredentialResponseForLinking(credentialResponse, currentUser)

        assertTrue(result.isFailure)
        assertEquals("Tipo de credencial no soportada para vinculación", result.exceptionOrNull()?.message)
    }

    @Test
    fun `handleCredentialResponseForLinking returns failure for unknown credential`() = runTest {
        val currentUser = mockk<FirebaseUser>()
        val credentialResponse = mockk<GetCredentialResponse>()
        val unknownCredential = mockk<androidx.credentials.Credential>()

        every { credentialResponse.credential } returns unknownCredential

        val result = dataSource.handleCredentialResponseForLinking(credentialResponse, currentUser)

        assertTrue(result.isFailure)
        assertEquals("Tipo de credencial no reconocida para vinculación", result.exceptionOrNull()?.message)
    }

    @Test
    fun `handleCredentialResponseForLinking returns failure on GoogleIdTokenParsingException`() = runTest {
        val currentUser = mockk<FirebaseUser>()
        val credentialResponse = mockk<GetCredentialResponse>()
        val customCredential = mockk<CustomCredential>()
        val mockBundle = mockk<Bundle>(relaxed = true)

        every { credentialResponse.credential } returns customCredential
        every { customCredential.type } returns GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        every { customCredential.data } returns mockBundle
        every { credentialOptionsFactory.parseGoogleIdTokenCredential(mockBundle) } throws
            GoogleIdTokenParsingException(Exception("Parse error"))

        val result = dataSource.handleCredentialResponseForLinking(credentialResponse, currentUser)

        assertTrue(result.isFailure)
        assertEquals("Token de Google inválido", result.exceptionOrNull()?.message)
    }

    // ==================== LINK WITH GOOGLE ID TOKEN TESTS ====================

    @Test
    fun `linkWithGoogleIdToken returns success when user exists`() = runTest {
        val currentUser = mockk<FirebaseUser>()
        val authCredential = mockk<AuthCredential>()
        every { GoogleAuthProvider.getCredential("test-token", null) } returns authCredential

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()
        val linkedUser = mockk<FirebaseUser>()

        every { currentUser.linkWithCredential(authCredential) } returns task
        every { authResult.user } returns linkedUser
        every { linkedUser.uid } returns "uid"
        every { linkedUser.email } returns "email@test.com"
        every { linkedUser.displayName } returns "Test User"
        every { linkedUser.phoneNumber } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        val result = dataSource.linkWithGoogleIdToken(currentUser, "test-token")

        assertTrue(result.isSuccess)
        assertEquals("uid", result.getOrNull()?.uid)
    }

    @Test
    fun `linkWithGoogleIdToken returns failure when user is null`() = runTest {
        val currentUser = mockk<FirebaseUser>()
        val authCredential = mockk<AuthCredential>()
        every { GoogleAuthProvider.getCredential("test-token", null) } returns authCredential

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()

        every { currentUser.linkWithCredential(authCredential) } returns task
        every { authResult.user } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        val result = dataSource.linkWithGoogleIdToken(currentUser, "test-token")

        assertTrue(result.isFailure)
        assertEquals("Error al vincular cuenta de Google", result.exceptionOrNull()?.message)
    }

    @Test
    fun `linkWithGoogleIdToken returns failure on Firebase error`() = runTest {
        val currentUser = mockk<FirebaseUser>()
        val authCredential = mockk<AuthCredential>()
        every { GoogleAuthProvider.getCredential("test-token", null) } returns authCredential

        val task = mockk<Task<AuthResult>>()
        every { currentUser.linkWithCredential(authCredential) } returns task

        every { task.addOnSuccessListener(any()) } returns task
        val failureSlot = slot<OnFailureListener>()
        every { task.addOnFailureListener(capture(failureSlot)) } answers {
            failureSlot.captured.onFailure(Exception("Link error"))
            task
        }

        val result = dataSource.linkWithGoogleIdToken(currentUser, "test-token")

        assertTrue(result.isFailure)
    }

    // ==================== COMPLETE REGISTER WITH CREDENTIAL TESTS ====================

    @Test
    fun `completeRegisterWithCredential returns success when user exists`() = runTest {
        val authCredential = mockk<AuthCredential>()
        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()
        val firebaseUser = mockk<FirebaseUser>()

        every { firebaseAuth.signInWithCredential(authCredential) } returns task
        every { authResult.user } returns firebaseUser
        every { firebaseUser.uid } returns "uid"
        every { firebaseUser.email } returns "email@test.com"
        every { firebaseUser.displayName } returns "Test User"
        every { firebaseUser.phoneNumber } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        val result = dataSource.completeRegisterWithCredential(authCredential)

        assertTrue(result.isSuccess)
        assertEquals("uid", result.getOrNull()?.uid)
    }

    @Test
    fun `completeRegisterWithCredential returns failure when user is null`() = runTest {
        val authCredential = mockk<AuthCredential>()
        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()

        every { firebaseAuth.signInWithCredential(authCredential) } returns task
        every { authResult.user } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        val result = dataSource.completeRegisterWithCredential(authCredential)

        assertTrue(result.isFailure)
        assertEquals("Error al iniciar sesión", result.exceptionOrNull()?.message)
    }

    @Test
    fun `completeRegisterWithCredential returns failure on Firebase error`() = runTest {
        val authCredential = mockk<AuthCredential>()
        val task = mockk<Task<AuthResult>>()

        every { firebaseAuth.signInWithCredential(authCredential) } returns task

        every { task.addOnSuccessListener(any()) } returns task
        val failureSlot = slot<OnFailureListener>()
        every { task.addOnFailureListener(capture(failureSlot)) } answers {
            failureSlot.captured.onFailure(Exception("Firebase error"))
            task
        }

        val result = dataSource.completeRegisterWithCredential(authCredential)

        assertTrue(result.isFailure)
    }
}