package com.estholon.authentication.data.datasources.multifactor

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.MultiFactorSession
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneMultiFactorAssertion
import com.google.firebase.auth.PhoneMultiFactorGenerator
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class PhoneAuthOptionsFactoryImplTest {

    private lateinit var factory: PhoneAuthOptionsFactoryImpl

    companion object {
        private const val TEST_PHONE_NUMBER = "+1234567890"
        private const val TEST_VERIFICATION_ID = "test-verification-id"
        private const val TEST_VERIFICATION_CODE = "123456"
        private const val TEST_TIMEOUT = 60L
    }

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.isLoggable(any(), any()) } returns false

        factory = PhoneAuthOptionsFactoryImpl()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ==================== CONSTRUCTOR TESTS ====================

    @Test
    fun `factory can be instantiated`() {
        val newFactory = PhoneAuthOptionsFactoryImpl()
        assertNotNull(newFactory)
    }

    // ==================== createPhoneAuthOptions TESTS ====================

    @Test
    fun `createPhoneAuthOptions returns valid options with all parameters`() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
        val mockBuilder = mockk<PhoneAuthOptions.Builder>(relaxed = true)
        val mockOptions = mockk<PhoneAuthOptions>()

        mockkStatic(PhoneAuthOptions::class)
        every { PhoneAuthOptions.newBuilder(firebaseAuth) } returns mockBuilder
        every { mockBuilder.setPhoneNumber(any()) } returns mockBuilder
        every { mockBuilder.setTimeout(any(), any()) } returns mockBuilder
        every { mockBuilder.setMultiFactorSession(any()) } returns mockBuilder
        every { mockBuilder.setCallbacks(any()) } returns mockBuilder
        every { mockBuilder.build() } returns mockOptions

        val result = factory.createPhoneAuthOptions(
            firebaseAuth = firebaseAuth,
            phoneNumber = TEST_PHONE_NUMBER,
            timeout = TEST_TIMEOUT,
            timeUnit = TimeUnit.SECONDS,
            session = session,
            callbacks = callbacks
        )

        assertNotNull(result)
        assertEquals(mockOptions, result)
    }

    @Test
    fun `createPhoneAuthOptions sets phone number correctly`() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
        val mockBuilder = mockk<PhoneAuthOptions.Builder>(relaxed = true)
        val mockOptions = mockk<PhoneAuthOptions>()
        val phoneNumberSlot = slot<String>()

        mockkStatic(PhoneAuthOptions::class)
        every { PhoneAuthOptions.newBuilder(firebaseAuth) } returns mockBuilder
        every { mockBuilder.setPhoneNumber(capture(phoneNumberSlot)) } returns mockBuilder
        every { mockBuilder.setTimeout(any(), any()) } returns mockBuilder
        every { mockBuilder.setMultiFactorSession(any()) } returns mockBuilder
        every { mockBuilder.setCallbacks(any()) } returns mockBuilder
        every { mockBuilder.build() } returns mockOptions

        factory.createPhoneAuthOptions(
            firebaseAuth = firebaseAuth,
            phoneNumber = TEST_PHONE_NUMBER,
            timeout = TEST_TIMEOUT,
            timeUnit = TimeUnit.SECONDS,
            session = session,
            callbacks = callbacks
        )

        assertEquals(TEST_PHONE_NUMBER, phoneNumberSlot.captured)
    }

    @Test
    fun `createPhoneAuthOptions sets timeout correctly`() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
        val mockBuilder = mockk<PhoneAuthOptions.Builder>(relaxed = true)
        val mockOptions = mockk<PhoneAuthOptions>()
        val timeoutSlot = slot<Long>()
        val timeUnitSlot = slot<TimeUnit>()

        mockkStatic(PhoneAuthOptions::class)
        every { PhoneAuthOptions.newBuilder(firebaseAuth) } returns mockBuilder
        every { mockBuilder.setPhoneNumber(any()) } returns mockBuilder
        every { mockBuilder.setTimeout(capture(timeoutSlot), capture(timeUnitSlot)) } returns mockBuilder
        every { mockBuilder.setMultiFactorSession(any()) } returns mockBuilder
        every { mockBuilder.setCallbacks(any()) } returns mockBuilder
        every { mockBuilder.build() } returns mockOptions

        factory.createPhoneAuthOptions(
            firebaseAuth = firebaseAuth,
            phoneNumber = TEST_PHONE_NUMBER,
            timeout = TEST_TIMEOUT,
            timeUnit = TimeUnit.SECONDS,
            session = session,
            callbacks = callbacks
        )

        assertEquals(TEST_TIMEOUT, timeoutSlot.captured)
        assertEquals(TimeUnit.SECONDS, timeUnitSlot.captured)
    }

    @Test
    fun `createPhoneAuthOptions sets multifactor session correctly`() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
        val mockBuilder = mockk<PhoneAuthOptions.Builder>(relaxed = true)
        val mockOptions = mockk<PhoneAuthOptions>()
        val sessionSlot = slot<MultiFactorSession>()

        mockkStatic(PhoneAuthOptions::class)
        every { PhoneAuthOptions.newBuilder(firebaseAuth) } returns mockBuilder
        every { mockBuilder.setPhoneNumber(any()) } returns mockBuilder
        every { mockBuilder.setTimeout(any(), any()) } returns mockBuilder
        every { mockBuilder.setMultiFactorSession(capture(sessionSlot)) } returns mockBuilder
        every { mockBuilder.setCallbacks(any()) } returns mockBuilder
        every { mockBuilder.build() } returns mockOptions

        factory.createPhoneAuthOptions(
            firebaseAuth = firebaseAuth,
            phoneNumber = TEST_PHONE_NUMBER,
            timeout = TEST_TIMEOUT,
            timeUnit = TimeUnit.SECONDS,
            session = session,
            callbacks = callbacks
        )

        assertEquals(session, sessionSlot.captured)
    }

    @Test
    fun `createPhoneAuthOptions sets callbacks correctly`() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
        val mockBuilder = mockk<PhoneAuthOptions.Builder>(relaxed = true)
        val mockOptions = mockk<PhoneAuthOptions>()
        val callbacksSlot = slot<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()

        mockkStatic(PhoneAuthOptions::class)
        every { PhoneAuthOptions.newBuilder(firebaseAuth) } returns mockBuilder
        every { mockBuilder.setPhoneNumber(any()) } returns mockBuilder
        every { mockBuilder.setTimeout(any(), any()) } returns mockBuilder
        every { mockBuilder.setMultiFactorSession(any()) } returns mockBuilder
        every { mockBuilder.setCallbacks(capture(callbacksSlot)) } returns mockBuilder
        every { mockBuilder.build() } returns mockOptions

        factory.createPhoneAuthOptions(
            firebaseAuth = firebaseAuth,
            phoneNumber = TEST_PHONE_NUMBER,
            timeout = TEST_TIMEOUT,
            timeUnit = TimeUnit.SECONDS,
            session = session,
            callbacks = callbacks
        )

        assertEquals(callbacks, callbacksSlot.captured)
    }

    @Test
    fun `createPhoneAuthOptions with different timeout values`() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
        val mockBuilder = mockk<PhoneAuthOptions.Builder>(relaxed = true)
        val mockOptions = mockk<PhoneAuthOptions>()

        mockkStatic(PhoneAuthOptions::class)
        every { PhoneAuthOptions.newBuilder(firebaseAuth) } returns mockBuilder
        every { mockBuilder.setPhoneNumber(any()) } returns mockBuilder
        every { mockBuilder.setTimeout(any(), any()) } returns mockBuilder
        every { mockBuilder.setMultiFactorSession(any()) } returns mockBuilder
        every { mockBuilder.setCallbacks(any()) } returns mockBuilder
        every { mockBuilder.build() } returns mockOptions

        val timeouts = listOf(30L, 60L, 120L, 0L)
        timeouts.forEach { timeout ->
            val result = factory.createPhoneAuthOptions(
                firebaseAuth = firebaseAuth,
                phoneNumber = TEST_PHONE_NUMBER,
                timeout = timeout,
                timeUnit = TimeUnit.SECONDS,
                session = session,
                callbacks = callbacks
            )
            assertNotNull(result)
        }
    }

    @Test
    fun `createPhoneAuthOptions with different time units`() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
        val mockBuilder = mockk<PhoneAuthOptions.Builder>(relaxed = true)
        val mockOptions = mockk<PhoneAuthOptions>()

        mockkStatic(PhoneAuthOptions::class)
        every { PhoneAuthOptions.newBuilder(firebaseAuth) } returns mockBuilder
        every { mockBuilder.setPhoneNumber(any()) } returns mockBuilder
        every { mockBuilder.setTimeout(any(), any()) } returns mockBuilder
        every { mockBuilder.setMultiFactorSession(any()) } returns mockBuilder
        every { mockBuilder.setCallbacks(any()) } returns mockBuilder
        every { mockBuilder.build() } returns mockOptions

        val timeUnits = listOf(TimeUnit.SECONDS, TimeUnit.MILLISECONDS, TimeUnit.MINUTES)
        timeUnits.forEach { timeUnit ->
            val result = factory.createPhoneAuthOptions(
                firebaseAuth = firebaseAuth,
                phoneNumber = TEST_PHONE_NUMBER,
                timeout = TEST_TIMEOUT,
                timeUnit = timeUnit,
                session = session,
                callbacks = callbacks
            )
            assertNotNull(result)
        }
    }

    @Test
    fun `createPhoneAuthOptions with different phone number formats`() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
        val mockBuilder = mockk<PhoneAuthOptions.Builder>(relaxed = true)
        val mockOptions = mockk<PhoneAuthOptions>()

        mockkStatic(PhoneAuthOptions::class)
        every { PhoneAuthOptions.newBuilder(firebaseAuth) } returns mockBuilder
        every { mockBuilder.setPhoneNumber(any()) } returns mockBuilder
        every { mockBuilder.setTimeout(any(), any()) } returns mockBuilder
        every { mockBuilder.setMultiFactorSession(any()) } returns mockBuilder
        every { mockBuilder.setCallbacks(any()) } returns mockBuilder
        every { mockBuilder.build() } returns mockOptions

        val phoneNumbers = listOf(
            "+1234567890",
            "+44 7911 123456",
            "+34 612 345 678",
            "+49 151 12345678"
        )
        phoneNumbers.forEach { phoneNumber ->
            val result = factory.createPhoneAuthOptions(
                firebaseAuth = firebaseAuth,
                phoneNumber = phoneNumber,
                timeout = TEST_TIMEOUT,
                timeUnit = TimeUnit.SECONDS,
                session = session,
                callbacks = callbacks
            )
            assertNotNull(result)
        }
    }

    // ==================== verifyPhoneNumber TESTS ====================

    @Test
    fun `verifyPhoneNumber calls PhoneAuthProvider with correct options`() {
        val mockOptions = mockk<PhoneAuthOptions>()
        val optionsSlot = slot<PhoneAuthOptions>()

        mockkStatic(PhoneAuthProvider::class)
        every { PhoneAuthProvider.verifyPhoneNumber(capture(optionsSlot)) } returns Unit

        factory.verifyPhoneNumber(mockOptions)

        verify(exactly = 1) { PhoneAuthProvider.verifyPhoneNumber(mockOptions) }
        assertEquals(mockOptions, optionsSlot.captured)
    }

    @Test
    fun `verifyPhoneNumber is called exactly once`() {
        val mockOptions = mockk<PhoneAuthOptions>()

        mockkStatic(PhoneAuthProvider::class)
        every { PhoneAuthProvider.verifyPhoneNumber(any()) } returns Unit

        factory.verifyPhoneNumber(mockOptions)

        verify(exactly = 1) { PhoneAuthProvider.verifyPhoneNumber(any()) }
    }

    // ==================== getCredential TESTS ====================

    @Test
    fun `getCredential returns credential with valid parameters`() {
        val mockCredential = mockk<PhoneAuthCredential>()

        mockkStatic(PhoneAuthProvider::class)
        every { PhoneAuthProvider.getCredential(TEST_VERIFICATION_ID, TEST_VERIFICATION_CODE) } returns mockCredential

        val result = factory.getCredential(TEST_VERIFICATION_ID, TEST_VERIFICATION_CODE)

        assertNotNull(result)
        assertEquals(mockCredential, result)
    }

    @Test
    fun `getCredential passes verification id correctly`() {
        val mockCredential = mockk<PhoneAuthCredential>()
        val verificationIdSlot = slot<String>()

        mockkStatic(PhoneAuthProvider::class)
        every { PhoneAuthProvider.getCredential(capture(verificationIdSlot), any()) } returns mockCredential

        factory.getCredential(TEST_VERIFICATION_ID, TEST_VERIFICATION_CODE)

        assertEquals(TEST_VERIFICATION_ID, verificationIdSlot.captured)
    }

    @Test
    fun `getCredential passes verification code correctly`() {
        val mockCredential = mockk<PhoneAuthCredential>()
        val verificationCodeSlot = slot<String>()

        mockkStatic(PhoneAuthProvider::class)
        every { PhoneAuthProvider.getCredential(any(), capture(verificationCodeSlot)) } returns mockCredential

        factory.getCredential(TEST_VERIFICATION_ID, TEST_VERIFICATION_CODE)

        assertEquals(TEST_VERIFICATION_CODE, verificationCodeSlot.captured)
    }

    @Test
    fun `getCredential with different verification codes`() {
        val mockCredential = mockk<PhoneAuthCredential>()

        mockkStatic(PhoneAuthProvider::class)
        every { PhoneAuthProvider.getCredential(any(), any()) } returns mockCredential

        val codes = listOf("123456", "000000", "999999", "111111")
        codes.forEach { code ->
            val result = factory.getCredential(TEST_VERIFICATION_ID, code)
            assertNotNull(result)
        }
    }

    @Test
    fun `getCredential with different verification ids`() {
        val mockCredential = mockk<PhoneAuthCredential>()

        mockkStatic(PhoneAuthProvider::class)
        every { PhoneAuthProvider.getCredential(any(), any()) } returns mockCredential

        val ids = listOf("id-1", "id-2", "verification-abc-123", "long-verification-id-12345")
        ids.forEach { id ->
            val result = factory.getCredential(id, TEST_VERIFICATION_CODE)
            assertNotNull(result)
        }
    }

    // ==================== getMultiFactorAssertion TESTS ====================

    @Test
    fun `getMultiFactorAssertion returns assertion with valid credential`() {
        val mockCredential = mockk<PhoneAuthCredential>()
        val mockAssertion = mockk<PhoneMultiFactorAssertion>()

        mockkStatic(PhoneMultiFactorGenerator::class)
        every { PhoneMultiFactorGenerator.getAssertion(mockCredential) } returns mockAssertion

        val result = factory.getMultiFactorAssertion(mockCredential)

        assertNotNull(result)
        assertEquals(mockAssertion, result)
    }

    @Test
    fun `getMultiFactorAssertion passes credential correctly`() {
        val mockCredential = mockk<PhoneAuthCredential>()
        val mockAssertion = mockk<PhoneMultiFactorAssertion>()
        val credentialSlot = slot<PhoneAuthCredential>()

        mockkStatic(PhoneMultiFactorGenerator::class)
        every { PhoneMultiFactorGenerator.getAssertion(capture(credentialSlot)) } returns mockAssertion

        factory.getMultiFactorAssertion(mockCredential)

        assertEquals(mockCredential, credentialSlot.captured)
    }

    @Test
    fun `getMultiFactorAssertion is called with provided credential`() {
        val mockCredential = mockk<PhoneAuthCredential>()
        val mockAssertion = mockk<PhoneMultiFactorAssertion>()

        mockkStatic(PhoneMultiFactorGenerator::class)
        every { PhoneMultiFactorGenerator.getAssertion(any()) } returns mockAssertion

        factory.getMultiFactorAssertion(mockCredential)

        verify(exactly = 1) { PhoneMultiFactorGenerator.getAssertion(mockCredential) }
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    fun `full flow from credential to assertion works`() {
        val mockCredential = mockk<PhoneAuthCredential>()
        val mockAssertion = mockk<PhoneMultiFactorAssertion>()

        mockkStatic(PhoneAuthProvider::class)
        mockkStatic(PhoneMultiFactorGenerator::class)
        every { PhoneAuthProvider.getCredential(any(), any()) } returns mockCredential
        every { PhoneMultiFactorGenerator.getAssertion(mockCredential) } returns mockAssertion

        val credential = factory.getCredential(TEST_VERIFICATION_ID, TEST_VERIFICATION_CODE)
        val assertion = factory.getMultiFactorAssertion(credential)

        assertNotNull(credential)
        assertNotNull(assertion)
        assertEquals(mockAssertion, assertion)
    }
}
