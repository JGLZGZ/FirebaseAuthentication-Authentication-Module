package com.estholon.authentication.data.datasources.google

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
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
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.unmockkAll
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
        // Mock Android Log to avoid "not mocked" errors
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

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

    // Note: signInGoogleCredentialManager test removed because GoogleIdTokenCredential.createFrom()
    // uses internal Bundle methods that cannot be properly mocked without Robolectric.
    // This functionality should be tested via instrumented tests.

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