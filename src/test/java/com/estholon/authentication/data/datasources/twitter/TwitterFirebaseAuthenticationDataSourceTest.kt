package com.estholon.authentication.data.datasources.twitter

import android.app.Activity
import android.util.Log
import com.estholon.authentication.data.dtos.UserDto
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.UserInfo
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TwitterFirebaseAuthenticationDataSourceTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataSource: TwitterFirebaseAuthenticationDataSource

    @Before
    fun setup() {
        // Mock Android Log
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        // Mock OAuthProvider.newBuilder() static method
        mockkStatic(OAuthProvider::class)
        val mockBuilder = mockk<OAuthProvider.Builder>()
        val mockProvider = mockk<OAuthProvider>()
        every { OAuthProvider.newBuilder(any<String>()) } returns mockBuilder
        every { mockBuilder.build() } returns mockProvider

        firebaseAuth = mockk()
        dataSource = TwitterFirebaseAuthenticationDataSource(firebaseAuth)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `signInTwitter returns user on pending result success`() = runTest {
        // Given
        val activity = mockk<Activity>()
        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()
        val firebaseUser = mockk<FirebaseUser>()

        every { firebaseAuth.pendingAuthResult } returns task
        every { authResult.user } returns firebaseUser
        every { firebaseUser.uid } returns "uid"
        every { firebaseUser.email } returns null
        every { firebaseUser.displayName } returns null
        every { firebaseUser.phoneNumber } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        // When
        val result = dataSource.signInTwitter(activity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("uid", result.getOrNull()?.uid)
    }

    @Test
    fun `linkTwitter returns success when not linked`() = runTest {
        // Given
        val activity = mockk<Activity>()
        val firebaseUser = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns firebaseUser
        every { firebaseUser.providerData } returns emptyList()

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()

        every { firebaseUser.startActivityForLinkWithProvider(activity, any()) } returns task
        every { authResult.user } returns firebaseUser
        every { firebaseUser.uid } returns "uid"
        every { firebaseUser.email } returns null
        every { firebaseUser.displayName } returns null
        every { firebaseUser.phoneNumber } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        // When
        val result = dataSource.linkTwitter(activity)

        // Then
        assertTrue(result.isSuccess)
    }
}