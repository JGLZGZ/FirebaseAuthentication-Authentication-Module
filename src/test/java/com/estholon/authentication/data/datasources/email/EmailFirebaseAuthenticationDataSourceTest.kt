package com.estholon.authentication.data.datasources.email

import android.content.Context
import com.estholon.authentication.data.mappers.UserMapper
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
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

class EmailFirebaseAuthenticationDataSourceTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userMapper: UserMapper
    private lateinit var context: Context
    private lateinit var dataSource: EmailFirebaseAuthenticationDataSource

    @Before
    fun setup() {
        firebaseAuth = mockk()
        userMapper = UserMapper()
        context = mockk()
        dataSource = EmailFirebaseAuthenticationDataSource(firebaseAuth, userMapper, context)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `signUpEmail returns user when success`() = runTest {
        // Given
        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()
        val firebaseUser = mockk<FirebaseUser>()

        every { firebaseAuth.createUserWithEmailAndPassword(any(), any()) } returns task
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
        val result = dataSource.signUpEmail("test@test.com", "password")

        // Then
        assertTrue(result.isSuccess)
        val userDto = result.getOrNull()
        assertEquals("uid", userDto?.uid)
        verify { firebaseAuth.createUserWithEmailAndPassword("test@test.com", "password") }
    }

    @Test
    fun `signUpEmail returns failure when error`() = runTest {
        // Given
        val task = mockk<Task<AuthResult>>()
        val exception = Exception("Auth Error")

        every { firebaseAuth.createUserWithEmailAndPassword(any(), any()) } returns task

        val failureSlot = slot<OnFailureListener>()
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(capture(failureSlot)) } answers {
            failureSlot.captured.onFailure(exception)
            task
        }

        // When
        val result = dataSource.signUpEmail("test@test.com", "password")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Auth Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `signInEmail returns user when success`() = runTest {
        // Given
        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()
        val firebaseUser = mockk<FirebaseUser>()

        every { firebaseAuth.signInWithEmailAndPassword(any(), any()) } returns task
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
        val result = dataSource.signInEmail("test@test.com", "password")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("uid", result.getOrNull()?.uid)
    }

    @Test
    fun `linkEmail returns user when success`() = runTest {
        // Given
        val firebaseUser = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns firebaseUser

        mockkStatic(EmailAuthProvider::class)
        val credential = mockk<AuthCredential>()
        every { EmailAuthProvider.getCredential(any(), any()) } returns credential

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()

        every { firebaseUser.linkWithCredential(any()) } returns task
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
        val result = dataSource.linkEmail("test@test.com", "password")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("uid", result.getOrNull()?.uid)
        verify { firebaseUser.linkWithCredential(credential) }
    }

    @Test
    fun `linkEmail returns failure when no user logged`() = runTest {
        // Given
        every { firebaseAuth.currentUser } returns null

        // When
        val result = dataSource.linkEmail("test@test.com", "password")

        // Then
        assertTrue(result.isFailure)
        assertEquals("No hay usuario autenticado", result.exceptionOrNull()?.message)
    }

    @Test
    fun `resetPassword returns success`() = runTest {
        // Given
        val task = mockk<Task<Void>>()
        every { firebaseAuth.sendPasswordResetEmail(any()) } returns task

        val successSlot = slot<OnSuccessListener<Void>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(null)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        // When
        val result = dataSource.resetPassword("test@test.com")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `signUpEmail returns failure when user is null`() = runTest {
        // Given
        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()

        every { firebaseAuth.createUserWithEmailAndPassword(any(), any()) } returns task
        every { authResult.user } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        // When
        val result = dataSource.signUpEmail("test@test.com", "password")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Error al iniciar sesión", result.exceptionOrNull()?.message)
    }

    @Test
    fun `signInEmail returns failure when error`() = runTest {
        // Given
        val task = mockk<Task<AuthResult>>()
        val exception = Exception("Sign in failed")

        every { firebaseAuth.signInWithEmailAndPassword(any(), any()) } returns task

        every { task.addOnSuccessListener(any()) } returns task
        val failureSlot = slot<OnFailureListener>()
        every { task.addOnFailureListener(capture(failureSlot)) } answers {
            failureSlot.captured.onFailure(exception)
            task
        }

        // When
        val result = dataSource.signInEmail("test@test.com", "password")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Sign in failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `signInEmail returns failure when user is null`() = runTest {
        // Given
        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()

        every { firebaseAuth.signInWithEmailAndPassword(any(), any()) } returns task
        every { authResult.user } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        // When
        val result = dataSource.signInEmail("test@test.com", "password")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Error al iniciar sesión", result.exceptionOrNull()?.message)
    }

    @Test
    fun `linkEmail returns failure when linked user is null`() = runTest {
        // Given
        val firebaseUser = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns firebaseUser

        mockkStatic(EmailAuthProvider::class)
        val credential = mockk<AuthCredential>()
        every { EmailAuthProvider.getCredential(any(), any()) } returns credential

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()

        every { firebaseUser.linkWithCredential(any()) } returns task
        every { authResult.user } returns null

        val successSlot = slot<OnSuccessListener<AuthResult>>()
        every { task.addOnSuccessListener(capture(successSlot)) } answers {
            successSlot.captured.onSuccess(authResult)
            task
        }
        every { task.addOnFailureListener(any()) } returns task

        // When
        val result = dataSource.linkEmail("test@test.com", "password")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Error al vincular cuenta de email", result.exceptionOrNull()?.message)
    }

    @Test
    fun `linkEmail returns failure on exception`() = runTest {
        // Given
        val firebaseUser = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns firebaseUser

        mockkStatic(EmailAuthProvider::class)
        val credential = mockk<AuthCredential>()
        every { EmailAuthProvider.getCredential(any(), any()) } returns credential

        val task = mockk<Task<AuthResult>>()
        val exception = Exception("Link failed")

        every { firebaseUser.linkWithCredential(any()) } returns task

        every { task.addOnSuccessListener(any()) } returns task
        val failureSlot = slot<OnFailureListener>()
        every { task.addOnFailureListener(capture(failureSlot)) } answers {
            failureSlot.captured.onFailure(exception)
            task
        }

        // When
        val result = dataSource.linkEmail("test@test.com", "password")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Link failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `resetPassword returns failure on exception`() = runTest {
        // Given
        val task = mockk<Task<Void>>()
        val exception = Exception("Reset failed")

        every { firebaseAuth.sendPasswordResetEmail(any()) } returns task

        every { task.addOnSuccessListener(any()) } returns task
        val failureSlot = slot<OnFailureListener>()
        every { task.addOnFailureListener(capture(failureSlot)) } answers {
            failureSlot.captured.onFailure(exception)
            task
        }

        // When
        val result = dataSource.resetPassword("test@test.com")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Reset failed", result.exceptionOrNull()?.message)
    }
}