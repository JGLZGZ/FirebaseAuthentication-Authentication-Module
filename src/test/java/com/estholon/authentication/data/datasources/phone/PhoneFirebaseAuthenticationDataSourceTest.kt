package com.estholon.authentication.data.datasources.phone

import android.app.Activity
import com.estholon.authentication.data.dtos.UserDto
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
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

class PhoneFirebaseAuthenticationDataSourceTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataSource: PhoneFirebaseAuthenticationDataSource

    @Before
    fun setup() {
        firebaseAuth = mockk()
        dataSource = PhoneFirebaseAuthenticationDataSource(firebaseAuth)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `signInPhone calls verifyPhoneNumber`() = runTest {
        // Given
        val phoneNumber = "+1234567890"
        val activity = mockk<Activity>()
        val callback = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()

        mockkStatic(PhoneAuthOptions::class)
        mockkStatic(PhoneAuthProvider::class)

        val builder = mockk<PhoneAuthOptions.Builder>()
        every { PhoneAuthOptions.newBuilder(firebaseAuth) } returns builder
        every { builder.setPhoneNumber(phoneNumber) } returns builder
        every { builder.setTimeout(any(), any()) } returns builder
        every { builder.setActivity(activity) } returns builder
        every { builder.setCallbacks(callback) } returns builder

        val options = mockk<PhoneAuthOptions>()
        every { builder.build() } returns options

        every { PhoneAuthProvider.verifyPhoneNumber(options) } returns Unit

        // When
        dataSource.signInPhone(phoneNumber, activity, callback)

        // Then
        verify { PhoneAuthProvider.verifyPhoneNumber(options) }
    }

    @Test
    fun `verifyCode returns user on success`() = runTest {
        // Given
        val verificationCode = "123456"
        val phoneCode = "id"

        mockkStatic(PhoneAuthProvider::class)
        val credential = mockk<PhoneAuthCredential>()
        every { PhoneAuthProvider.getCredential(verificationCode, phoneCode) } returns credential

        val task = mockk<Task<AuthResult>>()
        val authResult = mockk<AuthResult>()
        val firebaseUser = mockk<FirebaseUser>()

        every { firebaseAuth.signInWithCredential(credential) } returns task
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
        val result = dataSource.verifyCode(verificationCode, phoneCode)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("uid", result.getOrNull()?.uid)
    }
}