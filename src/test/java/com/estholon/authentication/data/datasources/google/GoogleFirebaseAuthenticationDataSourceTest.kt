package com.estholon.authentication.data.datasources.google

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import com.estholon.authentication.data.datasources.email.EmailAuthenticationDataSource
import com.estholon.authentication.data.dtos.UserDto
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GoogleFirebaseAuthenticationDataSourceTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var emailAuthenticationDataSource: EmailAuthenticationDataSource
    private lateinit var context: Context
    private lateinit var credentialManager: CredentialManager
    private lateinit var dataSource: GoogleFirebaseAuthenticationDataSource

    @Before
    fun setup() {
        firebaseAuth = mockk()
        emailAuthenticationDataSource = mockk()
        context = mockk(relaxed = true)
        credentialManager = mockk()
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

    @Test
    fun `signInGoogleCredentialManager returns user on success`() = runTest {
        // Given
        val activity = mockk<Activity>()
        val credentialResponse = mockk<GetCredentialResponse>()
        val customCredential = mockk<CustomCredential>()

        coEvery { credentialManager.getCredential(any(), any()) } returns credentialResponse
        every { credentialResponse.credential } returns customCredential
        every { customCredential.type } returns GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL

        // Mock GoogleIdTokenCredential.createFrom
        mockkStatic(GoogleIdTokenCredential::class)
        val googleIdTokenCredential = mockk<GoogleIdTokenCredential>()
        every { GoogleIdTokenCredential.createFrom(any()) } returns googleIdTokenCredential
        every { googleIdTokenCredential.idToken } returns "idToken"
        every { googleIdTokenCredential.id } returns "id"
        every { googleIdTokenCredential.displayName } returns "name"
        every { googleIdTokenCredential.givenName } returns "email"

        // Mock GoogleAuthProvider
        mockkStatic(GoogleAuthProvider::class)
        val authCredential = mockk<AuthCredential>()
        every { GoogleAuthProvider.getCredential("idToken", null) } returns authCredential

        // Mock Firebase Sign In
        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()
        val firebaseUser = mockk<FirebaseUser>()

        every { firebaseAuth.signInWithCredential(authCredential) } returns task
        every { authResult.user } returns firebaseUser
        every { firebaseUser.uid } returns "uid"
        every { firebaseUser.email } returns "test@test.com"
        every { firebaseUser.displayName } returns null
        every { firebaseUser.phoneNumber } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        // When
        val result = dataSource.signInGoogleCredentialManager(activity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("uid", result.getOrNull()?.uid)
    }

    @Test
    fun `handleCredentialResponse handles PasswordCredential by delegating to emailDS`() = runTest {
        // Given
        val credentialResponse = mockk<GetCredentialResponse>()
        val passwordCredential = mockk<PasswordCredential>()
        every { credentialResponse.credential } returns passwordCredential
        every { passwordCredential.id } returns "email"
        every { passwordCredential.password } returns "pwd"

        val userDto = UserDto("uid", "email", null, null)
        coEvery { emailAuthenticationDataSource.signInEmail("email", "pwd") } returns Result.success(userDto)

        // When
        val result = dataSource.handleCredentialResponse(credentialResponse)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(userDto, result.getOrNull())
    }

    @Test
    fun `clearCredentialState calls credentialManager`() = runTest {
        // Given
        coEvery { credentialManager.clearCredentialState(any()) } returns Unit

        // When
        dataSource.clearCredentialState()

        // Then
        coVerify { credentialManager.clearCredentialState(any()) }
    }
}