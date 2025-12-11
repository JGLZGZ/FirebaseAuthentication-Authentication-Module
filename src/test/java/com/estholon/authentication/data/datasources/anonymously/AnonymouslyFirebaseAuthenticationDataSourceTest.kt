package com.estholon.authentication.data.datasources.anonymously

import com.estholon.authentication.data.dtos.UserDto
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AnonymouslyFirebaseAuthenticationDataSourceTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataSource: AnonymouslyFirebaseAuthenticationDataSource

    @Before
    fun setup() {
        firebaseAuth = mockk()
        dataSource = AnonymouslyFirebaseAuthenticationDataSource(firebaseAuth)
    }

    @Test
    fun `signInAnonymously returns user when success`() = runTest {
        // Given
        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()
        val firebaseUser = mockk<FirebaseUser>()

        every { firebaseAuth.signInAnonymously() } returns task
        every { authResult.user } returns firebaseUser
        every { firebaseUser.uid } returns "uid"
        every { firebaseUser.email } returns null
        every { firebaseUser.displayName } returns null
        every { firebaseUser.phoneNumber } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } returns task
        every { task.addOnFailureListener(any()) } returns task

        // When
        // execute in a separate coroutine or mock the callback trigger immediately
        // Here we can trigger the callback manually after the call is made,
        // but since we are mocking the return value of signInAnonymously, we need to make sure the slot is captured.
        // The call to signInAnonymously calls addOnSuccessListener immediately.

        // However, suspendCancellableCoroutine blocks until resumed.
        // We need to trigger the callback inside the mock answer or after capture.
        // Since addOnSuccessListener returns the task, we can chain.

        // Let's use answer to trigger callback
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }

        val result = dataSource.signInAnonymously()

        // Then
        assertTrue(result.isSuccess)
        val userDto = result.getOrNull()
        assertEquals("uid", userDto?.uid)
        verify { firebaseAuth.signInAnonymously() }
    }

    @Test
    fun `signInAnonymously returns failure when user is null`() = runTest {
        // Given
        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()

        every { firebaseAuth.signInAnonymously() } returns task
        every { authResult.user } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        // When
        val result = dataSource.signInAnonymously()

        // Then
        assertTrue(result.isFailure)
        assertEquals("Error al iniciar sesión", result.exceptionOrNull()?.message)
    }

    @Test
    fun `signInAnonymously returns failure on exception`() = runTest {
        // Given
        val task = mockk<Task<AuthResult>>()
        val exception = Exception("Firebase error")

        every { firebaseAuth.signInAnonymously() } returns task

        val failureSlot = slot<OnFailureListener>()
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(capture(failureSlot)) } answers {
            failureSlot.captured.onFailure(exception)
            task
        }

        // When
        val result = dataSource.signInAnonymously()

        // Then
        assertTrue(result.isFailure)
        assertEquals("Error al iniciar sesión", result.exceptionOrNull()?.message)
    }
}