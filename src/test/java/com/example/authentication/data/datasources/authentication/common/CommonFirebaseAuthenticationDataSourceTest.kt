package com.estholon.firebaseauthentication.data.datasources.authentication.common

import com.estholon.firebaseauthentication.data.datasources.authentication.google.GoogleAuthenticationDataSource
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class CommonFirebaseAuthenticationDataSourceTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleAuthenticationDataSource: GoogleAuthenticationDataSource

    private lateinit var dataSource: CommonFirebaseAuthenticationDataSource

    @Before
    fun setup() {
        firebaseAuth = mockk()
        googleAuthenticationDataSource = mockk()
        dataSource =
            CommonFirebaseAuthenticationDataSource(firebaseAuth, googleAuthenticationDataSource)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `isUserLogged returns true when user is logged in`() {

        //Given
        val user = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns user

        //When
        val result = dataSource.isUserLogged()

        //Then
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `isUserLogged returns false when user is not logged in`() {

        //Given
        every { firebaseAuth.currentUser } returns null

        //When
        val result = dataSource.isUserLogged()

        //Then
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrNull())
    }

    @Test
    fun `isUserLogged returns failure when Firebase exception`() {

        //Given
        every { firebaseAuth.currentUser } throws RuntimeException("Firebase error")

        //When
        val result = dataSource.isUserLogged()

        //Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception.message?.contains("Firebase error") == true)
    }

    @Test
    fun `isEmailVerified returns false when no user is logged in`() = runTest {

        //Given
        every { firebaseAuth.currentUser } returns null

        //When
        val result = dataSource.isEmailVerified()

        //Then
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrNull())
    }

    @Test
    fun `isEmailVerified returns true when user email is verified`() = runTest {

        //Given
        val user = mockk<FirebaseUser>()
        val tokenResult = mockk<GetTokenResult>()
        val reload = mockk<Void>()
        every { firebaseAuth.currentUser } returns user
        every { user.getIdToken(true) } returns Tasks.forResult(tokenResult)
        every { user.reload() } returns Tasks.forResult(reload)
        every { user.isEmailVerified } returns true

        //When
        val result = dataSource.isEmailVerified()

        //Then
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
        verify(exactly = 1) { user.getIdToken(true) }
        verify(exactly = 1) { user.reload() }
    }

    @Test
    fun `isEmailVerified returns false when user email is not verified`() = runTest {
        //Given
        val user = mockk<FirebaseUser>()
        val tokenResult = mockk<GetTokenResult>()
        val reload = mockk<Void>()
        every { firebaseAuth.currentUser } returns user
        every { user.getIdToken(true) } returns Tasks.forResult(tokenResult)
        every { user.reload() } returns Tasks.forResult(reload)
        every { user.isEmailVerified } returns false

        //When
        val result = dataSource.isEmailVerified()

        //Then
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrNull())
        verify(exactly = 1) { user.getIdToken(true) }
        verify(exactly = 1) { user.reload() }
    }

    @Test
    fun `isEmailVerified returns failure when getIdToken throws exception`() = runTest {
        //Given
        val user = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns user
        every { user.getIdToken(true) } throws RuntimeException("Token error")

        //When
        val result = dataSource.isEmailVerified()

        //Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception.message?.contains("Token error") == true)
    }

    @Test
    fun `isEmailVerified returns failure when reload throws exception`() = runTest {
        //Given
        val user = mockk<FirebaseUser>()
        val tokenResult = mockk<GetTokenResult>()
        every { firebaseAuth.currentUser } returns user
        every { user.getIdToken(true) } returns Tasks.forResult(tokenResult)
        every { user.reload() } throws RuntimeException("Reload error")

        //When
        val result = dataSource.isEmailVerified()

        //Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception.message?.contains("Reload error") == true)
    }

    @Test
    fun `isEmailVerified returns failure when unexpected exception occurs`() = runTest {
        //Given
        every { firebaseAuth.currentUser } throws RuntimeException("Unexpected error")

        //When
        val result = dataSource.isEmailVerified()

        //Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception.message?.contains("Unexpected error") == true)
    }

    @Test
    fun `sendEmailVerification returns success when user is authenticated`() = runTest {
        //Given
        val user = mockk<FirebaseUser>()
        val voidResult = mockk<Void>()
        every { firebaseAuth.currentUser } returns user
        every { user.sendEmailVerification() } returns Tasks.forResult(voidResult)

        //When
        val result = dataSource.sendEmailVerification()

        //Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `sendEmailVerification returns failure when no user is authenticated`() = runTest {
        //Given
        every { firebaseAuth.currentUser } returns null

        //When
        val result = dataSource.sendEmailVerification()

        //Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception.message?.contains("No hay usuario autenticado") == true)
    }

    @Test
    fun `sendEmailVerification returns failure when sendEmailVerification throws exception`() = runTest {
        //Given
        val user = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns user
        every { user.sendEmailVerification() } throws RuntimeException("Email error")

        //When
        val result = dataSource.sendEmailVerification()

        //Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception.message?.contains("Email error") == true)
    }

    @Test
    fun `signOut calls signOut, logOut and clearCredentialState`() = runTest {
        // Given
        every { firebaseAuth.signOut() } returns Unit
        mockkObject(LoginManager.Companion)
        val loginManager = mockk<LoginManager>()
        every { LoginManager.getInstance() } returns loginManager
        every { loginManager.logOut() } returns Unit
        coEvery { googleAuthenticationDataSource.clearCredentialState() } returns Unit

        // When
        dataSource.signOut()

        // Then
        verify(exactly = 1) { firebaseAuth.signOut() }
        verify(exactly = 1) { loginManager.logOut() }
        coVerify(exactly = 1) { googleAuthenticationDataSource.clearCredentialState() }
    }


    @Test
    fun `signOut handles exception in signOut gracefully`() = runTest {
        //Given
        every { firebaseAuth.signOut() } throws RuntimeException("SignOut error")
        mockkObject(LoginManager.Companion)
        val loginManager = mockk<LoginManager>()
        every { LoginManager.getInstance() } returns loginManager
        every { loginManager.logOut() } returns Unit
        coEvery { googleAuthenticationDataSource.clearCredentialState() } returns Unit

        //When
        try {
            dataSource.signOut()
        } catch (e: Exception) {
            assertTrue(e.message?.contains("SignOut error") == true)
        }
    }
}