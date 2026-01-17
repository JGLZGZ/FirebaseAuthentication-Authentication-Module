package com.estholon.authentication.data.datasources.facebook

import android.util.Log
import com.estholon.authentication.data.dtos.UserDto
import com.facebook.AccessToken
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FacebookFirebaseAuthenticationDataSourceTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataSource: FacebookFirebaseAuthenticationDataSource

    @Before
    fun setup() {
        // Mock Android Log to avoid "not mocked" errors
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        // Mock FacebookAuthProvider.getCredential() static method
        mockkStatic(FacebookAuthProvider::class)
        val mockCredential = mockk<AuthCredential>()
        every { FacebookAuthProvider.getCredential(any()) } returns mockCredential

        firebaseAuth = mockk()
        dataSource = FacebookFirebaseAuthenticationDataSource(firebaseAuth)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ==================== SIGN IN FACEBOOK ====================

    @Test
    fun `signInFacebook returns user on success`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>()
        every { accessToken.token } returns "facebook_token"

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()
        val firebaseUser = mockk<FirebaseUser>()

        every { firebaseAuth.signInWithCredential(any()) } returns task
        every { authResult.user } returns firebaseUser
        every { firebaseUser.uid } returns "facebookUid"
        every { firebaseUser.email } returns "facebook@example.com"
        every { firebaseUser.displayName } returns "Facebook User"
        every { firebaseUser.phoneNumber } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        // When
        val result = dataSource.signInFacebook(accessToken)

        // Then
        assertTrue(result.isSuccess)
        val userDto = result.getOrNull()
        assertNotNull(userDto)
        assertEquals("facebookUid", userDto?.uid)
        assertEquals("facebook@example.com", userDto?.email)
        assertEquals("Facebook User", userDto?.displayName)
    }

    @Test
    fun `signInFacebook returns failure when user is null`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>()
        every { accessToken.token } returns "facebook_token"

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()

        every { firebaseAuth.signInWithCredential(any()) } returns task
        every { authResult.user } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        // When
        val result = dataSource.signInFacebook(accessToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Error al iniciar sesión", result.exceptionOrNull()?.message)
    }

    @Test
    fun `signInFacebook returns failure on exception`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>()
        every { accessToken.token } returns "facebook_token"

        val task = mockk<Task<AuthResult>>()
        val exception = Exception("Firebase sign in failed")

        every { firebaseAuth.signInWithCredential(any()) } returns task

        every { task.addOnSuccessListener(any()) } returns task
        val failureSlot = slot<OnFailureListener>()
        every { task.addOnFailureListener(capture(failureSlot)) } answers {
            failureSlot.captured.onFailure(exception)
            task
        }

        // When
        val result = dataSource.signInFacebook(accessToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Firebase sign in failed", result.exceptionOrNull()?.message)
    }

    // ==================== LINK FACEBOOK ====================

    @Test
    fun `linkFacebook returns user on success`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>()
        every { accessToken.token } returns "facebook_token"

        val currentUser = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns currentUser

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()
        val linkedUser = mockk<FirebaseUser>()

        every { currentUser.linkWithCredential(any()) } returns task
        every { authResult.user } returns linkedUser
        every { linkedUser.uid } returns "linkedUid"
        every { linkedUser.email } returns "linked@example.com"
        every { linkedUser.displayName } returns "Linked User"
        every { linkedUser.phoneNumber } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        // When
        val result = dataSource.linkFacebook(accessToken)

        // Then
        assertTrue(result.isSuccess)
        val userDto = result.getOrNull()
        assertNotNull(userDto)
        assertEquals("linkedUid", userDto?.uid)
        assertEquals("linked@example.com", userDto?.email)
        assertEquals("Linked User", userDto?.displayName)
    }

    @Test
    fun `linkFacebook returns failure when linked user is null`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>()
        every { accessToken.token } returns "facebook_token"

        val currentUser = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns currentUser

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()

        every { currentUser.linkWithCredential(any()) } returns task
        every { authResult.user } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        // When
        val result = dataSource.linkFacebook(accessToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Error al vincular cuenta de Facebook", result.exceptionOrNull()?.message)
    }

    @Test
    fun `linkFacebook returns failure on exception`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>()
        every { accessToken.token } returns "facebook_token"

        val currentUser = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns currentUser

        val task = mockk<Task<AuthResult>>()
        val exception = Exception("Link failed")

        every { currentUser.linkWithCredential(any()) } returns task

        every { task.addOnSuccessListener(any()) } returns task
        val failureSlot = slot<OnFailureListener>()
        every { task.addOnFailureListener(capture(failureSlot)) } answers {
            failureSlot.captured.onFailure(exception)
            task
        }

        // When
        val result = dataSource.linkFacebook(accessToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Link failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `linkFacebook returns failure when no current user`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>()
        every { accessToken.token } returns "facebook_token"
        every { firebaseAuth.currentUser } returns null

        // When
        val result = dataSource.linkFacebook(accessToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals("No hay usuario autenticado", result.exceptionOrNull()?.message)
    }
}
