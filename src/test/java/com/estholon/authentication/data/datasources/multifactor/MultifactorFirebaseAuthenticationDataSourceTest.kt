package com.estholon.authentication.data.datasources.multifactor

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.MultiFactor
import com.google.firebase.auth.MultiFactorSession
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthOptions
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import io.mockk.coEvery

class MultifactorFirebaseAuthenticationDataSourceTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataSource: MultifactorFirebaseAuthenticationDataSource

    @Before
    fun setup() {
        firebaseAuth = mockk()
        dataSource = MultifactorFirebaseAuthenticationDataSource(firebaseAuth)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getMultifactorSession returns session`() = runTest {
        // Given
        val user = mockk<FirebaseUser>()
        val multiFactor = mockk<MultiFactor>()
        val task = mockk<Task<MultiFactorSession>>()
        val session = mockk<MultiFactorSession>()

        every { firebaseAuth.currentUser } returns user
        every { user.multiFactor } returns multiFactor
        every { multiFactor.session } returns task

        // Mock await
        // Since await is an extension function on Task, we need to mock it if possible or mock the task behavior
        // But await is inline/extension, often easier to wrap or mock task completness.
        // Usually mockkStatic("kotlinx.coroutines.tasks.TasksKt") works for await()

        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        coEvery { task.await() } returns session

        // When
        val result = dataSource.getMultifactorSession()

        // Then
        assertEquals(session, result)
    }

    @Test
    fun `enrollMfaSendSms returns verificationId`() = runTest {
        // Given
        val session = mockk<MultiFactorSession>()
        val phoneNumber = "+1234567890"

        mockkStatic(PhoneAuthProvider::class)
        mockkStatic(PhoneAuthOptions::class)

        val builder = mockk<PhoneAuthOptions.Builder>()
        every { PhoneAuthOptions.newBuilder(firebaseAuth) } returns builder
        every { builder.setPhoneNumber(phoneNumber) } returns builder
        every { builder.setTimeout(any(), any()) } returns builder
        every { builder.setMultiFactorSession(session) } returns builder

        val callbacksSlot = slot<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
        every { builder.setCallbacks(capture(callbacksSlot)) } returns builder

        val options = mockk<PhoneAuthOptions>()
        every { builder.build() } returns options

        every { PhoneAuthProvider.verifyPhoneNumber(options) } answers {
            // Simulate code sent callback
            callbacksSlot.captured.onCodeSent("verificationId", mockk())
        }

        // When
        val result = dataSource.enrollMfaSendSms(session, phoneNumber)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("verificationId", result.getOrNull())
    }
}