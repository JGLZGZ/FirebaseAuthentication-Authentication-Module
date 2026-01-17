package com.estholon.authentication.data.datasources.multifactor

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.MultiFactor
import com.google.firebase.auth.MultiFactorSession
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneMultiFactorAssertion
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MultifactorFirebaseAuthenticationDataSourceTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var phoneAuthOptionsFactory: PhoneAuthOptionsFactory
    private lateinit var dataSource: MultifactorFirebaseAuthenticationDataSource

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.isLoggable(any(), any()) } returns false

        firebaseAuth = mockk()
        phoneAuthOptionsFactory = mockk(relaxed = true)
        dataSource = MultifactorFirebaseAuthenticationDataSource(firebaseAuth, phoneAuthOptionsFactory)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // getMultifactorSession tests

    @Test
    fun `getMultifactorSession returns session when user exists`() = runTest {
        val user = mockk<FirebaseUser>()
        val multiFactor = mockk<MultiFactor>()
        val task = mockk<Task<MultiFactorSession>>()
        val session = mockk<MultiFactorSession>()

        every { firebaseAuth.currentUser } returns user
        every { user.multiFactor } returns multiFactor
        every { multiFactor.session } returns task

        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        coEvery { task.await() } returns session

        val result = dataSource.getMultifactorSession()

        assertEquals(session, result)
    }

    @Test
    fun `getMultifactorSession throws exception when no current user`() = runTest {
        every { firebaseAuth.currentUser } returns null

        try {
            dataSource.getMultifactorSession()
            assertTrue("Expected exception to be thrown", false)
        } catch (e: Exception) {
            assertEquals("No hay usuario autenticado", e.message)
        }
    }

    // verifySmsForEnroll tests

    @Test
    fun `verifySmsForEnroll returns success when enrollment succeeds`() = runTest {
        val verificationId = "test-verification-id"
        val verificationCode = "123456"
        val credential = mockk<PhoneAuthCredential>()
        val assertion = mockk<PhoneMultiFactorAssertion>()
        val user = mockk<FirebaseUser>()
        val multiFactor = mockk<MultiFactor>()
        val enrollTask = mockk<Task<Void>>()

        every { phoneAuthOptionsFactory.getCredential(verificationId, verificationCode) } returns credential
        every { phoneAuthOptionsFactory.getMultiFactorAssertion(credential) } returns assertion
        every { firebaseAuth.currentUser } returns user
        every { user.multiFactor } returns multiFactor
        every { multiFactor.enroll(assertion, "Personal number") } returns enrollTask

        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        coEvery { enrollTask.await() } returns mockk()

        val result = dataSource.verifySmsForEnroll(verificationId, verificationCode)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `verifySmsForEnroll returns success when no current user`() = runTest {
        val verificationId = "test-verification-id"
        val verificationCode = "123456"
        val credential = mockk<PhoneAuthCredential>()
        val assertion = mockk<PhoneMultiFactorAssertion>()

        every { phoneAuthOptionsFactory.getCredential(verificationId, verificationCode) } returns credential
        every { phoneAuthOptionsFactory.getMultiFactorAssertion(credential) } returns assertion
        every { firebaseAuth.currentUser } returns null

        val result = dataSource.verifySmsForEnroll(verificationId, verificationCode)

        // Returns success because enroll is not called due to null user with ?. operator
        assertTrue(result.isSuccess)
    }

    @Test
    fun `verifySmsForEnroll returns failure when enrollment fails`() = runTest {
        val verificationId = "test-verification-id"
        val verificationCode = "123456"
        val credential = mockk<PhoneAuthCredential>()
        val assertion = mockk<PhoneMultiFactorAssertion>()
        val user = mockk<FirebaseUser>()
        val multiFactor = mockk<MultiFactor>()
        val enrollTask = mockk<Task<Void>>()
        val exception = Exception("Enrollment failed")

        every { phoneAuthOptionsFactory.getCredential(verificationId, verificationCode) } returns credential
        every { phoneAuthOptionsFactory.getMultiFactorAssertion(credential) } returns assertion
        every { firebaseAuth.currentUser } returns user
        every { user.multiFactor } returns multiFactor
        every { multiFactor.enroll(assertion, "Personal number") } returns enrollTask

        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        coEvery { enrollTask.await() } throws exception

        val result = dataSource.verifySmsForEnroll(verificationId, verificationCode)

        assertTrue(result.isFailure)
        assertEquals("Enrollment failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `verifySmsForEnroll calls factory methods with correct parameters`() = runTest {
        val verificationId = "test-verification-id"
        val verificationCode = "123456"
        val credential = mockk<PhoneAuthCredential>()
        val assertion = mockk<PhoneMultiFactorAssertion>()
        val user = mockk<FirebaseUser>()
        val multiFactor = mockk<MultiFactor>()
        val enrollTask = mockk<Task<Void>>()

        every { phoneAuthOptionsFactory.getCredential(verificationId, verificationCode) } returns credential
        every { phoneAuthOptionsFactory.getMultiFactorAssertion(credential) } returns assertion
        every { firebaseAuth.currentUser } returns user
        every { user.multiFactor } returns multiFactor
        every { multiFactor.enroll(assertion, "Personal number") } returns enrollTask

        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        coEvery { enrollTask.await() } returns mockk()

        dataSource.verifySmsForEnroll(verificationId, verificationCode)

        verify { phoneAuthOptionsFactory.getCredential(verificationId, verificationCode) }
        verify { phoneAuthOptionsFactory.getMultiFactorAssertion(credential) }
        verify { multiFactor.enroll(assertion, "Personal number") }
    }

    @Test
    fun `verifySmsForEnroll returns failure when getCredential throws`() = runTest {
        val verificationId = "test-verification-id"
        val verificationCode = "123456"
        val exception = Exception("Invalid credential")

        every { phoneAuthOptionsFactory.getCredential(verificationId, verificationCode) } throws exception

        val result = dataSource.verifySmsForEnroll(verificationId, verificationCode)

        assertTrue(result.isFailure)
        assertEquals("Invalid credential", result.exceptionOrNull()?.message)
    }

    @Test
    fun `verifySmsForEnroll returns failure when getMultiFactorAssertion throws`() = runTest {
        val verificationId = "test-verification-id"
        val verificationCode = "123456"
        val credential = mockk<PhoneAuthCredential>()
        val exception = Exception("Invalid assertion")

        every { phoneAuthOptionsFactory.getCredential(verificationId, verificationCode) } returns credential
        every { phoneAuthOptionsFactory.getMultiFactorAssertion(credential) } throws exception

        val result = dataSource.verifySmsForEnroll(verificationId, verificationCode)

        assertTrue(result.isFailure)
        assertEquals("Invalid assertion", result.exceptionOrNull()?.message)
    }

    // Note: Tests for callback-based methods (enrollMfaSendSms, sendSmsForEnroll) are not included
    // due to MockK's callback capture mechanism conflicting with PhoneAuthProvider's
    // static class initialization. Full coverage for these methods should be achieved
    // through instrumented tests on device/emulator.
}
