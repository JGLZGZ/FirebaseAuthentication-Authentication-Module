package com.estholon.authentication.data.datasources.multifactor

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.MultiFactorSession
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneMultiFactorAssertion
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
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
        factory = PhoneAuthOptionsFactoryImpl()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ==================== CONSTRUCTOR TESTS ====================

    @Test
    fun factory_canBeInstantiated() {
        val newFactory = PhoneAuthOptionsFactoryImpl()
        assertNotNull(newFactory)
    }

    // ==================== createPhoneAuthOptions TESTS ====================

    @Test
    fun createPhoneAuthOptions_withValidParameters_returnsPhoneAuthOptions() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()

        val result = factory.createPhoneAuthOptions(
            firebaseAuth = firebaseAuth,
            phoneNumber = TEST_PHONE_NUMBER,
            timeout = TEST_TIMEOUT,
            timeUnit = TimeUnit.SECONDS,
            session = session,
            callbacks = callbacks
        )

        assertNotNull(result)
        assertTrue(result is PhoneAuthOptions)
    }

    @Test
    fun createPhoneAuthOptions_withDifferentTimeouts_returnsValidOptions() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()

        val timeouts = listOf(30L, 60L, 120L)
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
    fun createPhoneAuthOptions_withDifferentTimeUnits_returnsValidOptions() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()

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
    fun createPhoneAuthOptions_withDifferentPhoneFormats_returnsValidOptions() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()

        val phoneNumbers = listOf(
            "+1234567890",
            "+44 7911 123456",
            "+34 612 345 678"
        )
        phoneNumbers.forEach { phone ->
            val result = factory.createPhoneAuthOptions(
                firebaseAuth = firebaseAuth,
                phoneNumber = phone,
                timeout = TEST_TIMEOUT,
                timeUnit = TimeUnit.SECONDS,
                session = session,
                callbacks = callbacks
            )
            assertNotNull(result)
        }
    }

    @Test
    fun createPhoneAuthOptions_withZeroTimeout_returnsValidOptions() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()

        val result = factory.createPhoneAuthOptions(
            firebaseAuth = firebaseAuth,
            phoneNumber = TEST_PHONE_NUMBER,
            timeout = 0L,
            timeUnit = TimeUnit.SECONDS,
            session = session,
            callbacks = callbacks
        )

        assertNotNull(result)
    }

    @Test
    fun createPhoneAuthOptions_multipleCallsReturnIndependentOptions() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()

        val result1 = factory.createPhoneAuthOptions(
            firebaseAuth = firebaseAuth,
            phoneNumber = "+1111111111",
            timeout = 30L,
            timeUnit = TimeUnit.SECONDS,
            session = session,
            callbacks = callbacks
        )

        val result2 = factory.createPhoneAuthOptions(
            firebaseAuth = firebaseAuth,
            phoneNumber = "+2222222222",
            timeout = 60L,
            timeUnit = TimeUnit.MINUTES,
            session = session,
            callbacks = callbacks
        )

        assertNotNull(result1)
        assertNotNull(result2)
    }

    // ==================== verifyPhoneNumber TESTS ====================

    @Test
    fun verifyPhoneNumber_callsPhoneAuthProvider() {
        val mockOptions = mockk<PhoneAuthOptions>()
        val optionsSlot = slot<PhoneAuthOptions>()

        mockkStatic(PhoneAuthProvider::class)
        every { PhoneAuthProvider.verifyPhoneNumber(capture(optionsSlot)) } returns Unit

        factory.verifyPhoneNumber(mockOptions)

        verify(exactly = 1) { PhoneAuthProvider.verifyPhoneNumber(mockOptions) }
        assertEquals(mockOptions, optionsSlot.captured)
    }

    @Test
    fun verifyPhoneNumber_isCalledExactlyOnce() {
        val mockOptions = mockk<PhoneAuthOptions>()

        mockkStatic(PhoneAuthProvider::class)
        every { PhoneAuthProvider.verifyPhoneNumber(any()) } returns Unit

        factory.verifyPhoneNumber(mockOptions)

        verify(exactly = 1) { PhoneAuthProvider.verifyPhoneNumber(any()) }
    }

    // ==================== getCredential TESTS ====================

    @Test
    fun getCredential_returnsPhoneAuthCredential() {
        val result = factory.getCredential(TEST_VERIFICATION_ID, TEST_VERIFICATION_CODE)

        assertNotNull(result)
        assertTrue(result is PhoneAuthCredential)
    }

    @Test
    fun getCredential_withDifferentVerificationCodes_returnsCredentials() {
        val codes = listOf("123456", "000000", "999999", "111111")

        codes.forEach { code ->
            val result = factory.getCredential(TEST_VERIFICATION_ID, code)
            assertNotNull(result)
            assertTrue(result is PhoneAuthCredential)
        }
    }

    @Test
    fun getCredential_withDifferentVerificationIds_returnsCredentials() {
        val ids = listOf("id-1", "id-2", "verification-abc", "long-id-12345")

        ids.forEach { id ->
            val result = factory.getCredential(id, TEST_VERIFICATION_CODE)
            assertNotNull(result)
            assertTrue(result is PhoneAuthCredential)
        }
    }

    @Test
    fun getCredential_withEmptyVerificationId_returnsCredential() {
        val result = factory.getCredential("", TEST_VERIFICATION_CODE)

        assertNotNull(result)
        assertTrue(result is PhoneAuthCredential)
    }

    @Test
    fun getCredential_withEmptyVerificationCode_returnsCredential() {
        val result = factory.getCredential(TEST_VERIFICATION_ID, "")

        assertNotNull(result)
        assertTrue(result is PhoneAuthCredential)
    }

    @Test
    fun getCredential_multipleCallsReturnIndependentCredentials() {
        val credential1 = factory.getCredential("id-1", "111111")
        val credential2 = factory.getCredential("id-2", "222222")

        assertNotNull(credential1)
        assertNotNull(credential2)
    }

    // ==================== getMultiFactorAssertion TESTS ====================

    @Test
    fun getMultiFactorAssertion_returnsPhoneMultiFactorAssertion() {
        val credential = factory.getCredential(TEST_VERIFICATION_ID, TEST_VERIFICATION_CODE)

        val result = factory.getMultiFactorAssertion(credential)

        assertNotNull(result)
        assertTrue(result is PhoneMultiFactorAssertion)
    }

    @Test
    fun getMultiFactorAssertion_withDifferentCredentials_returnsAssertions() {
        val credentials = listOf(
            factory.getCredential("id-1", "111111"),
            factory.getCredential("id-2", "222222"),
            factory.getCredential("id-3", "333333")
        )

        credentials.forEach { credential ->
            val result = factory.getMultiFactorAssertion(credential)
            assertNotNull(result)
            assertTrue(result is PhoneMultiFactorAssertion)
        }
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    fun fullFlow_fromCredentialToAssertion_works() {
        val credential = factory.getCredential(TEST_VERIFICATION_ID, TEST_VERIFICATION_CODE)
        val assertion = factory.getMultiFactorAssertion(credential)

        assertNotNull(credential)
        assertNotNull(assertion)
        assertTrue(credential is PhoneAuthCredential)
        assertTrue(assertion is PhoneMultiFactorAssertion)
    }

    @Test
    fun fullFlow_createOptionsAndGetCredential_works() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()

        val options = factory.createPhoneAuthOptions(
            firebaseAuth = firebaseAuth,
            phoneNumber = TEST_PHONE_NUMBER,
            timeout = TEST_TIMEOUT,
            timeUnit = TimeUnit.SECONDS,
            session = session,
            callbacks = callbacks
        )

        val credential = factory.getCredential(TEST_VERIFICATION_ID, TEST_VERIFICATION_CODE)
        val assertion = factory.getMultiFactorAssertion(credential)

        assertNotNull(options)
        assertNotNull(credential)
        assertNotNull(assertion)
    }

    @Test
    fun fullFlow_multipleOperationsInSequence_works() {
        val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
        val session = mockk<MultiFactorSession>()
        val callbacks = mockk<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()

        // Create options for multiple phone numbers
        val options1 = factory.createPhoneAuthOptions(
            firebaseAuth = firebaseAuth,
            phoneNumber = "+1111111111",
            timeout = 30L,
            timeUnit = TimeUnit.SECONDS,
            session = session,
            callbacks = callbacks
        )

        val options2 = factory.createPhoneAuthOptions(
            firebaseAuth = firebaseAuth,
            phoneNumber = "+2222222222",
            timeout = 60L,
            timeUnit = TimeUnit.SECONDS,
            session = session,
            callbacks = callbacks
        )

        // Get credentials and assertions
        val credential1 = factory.getCredential("id-1", "123456")
        val credential2 = factory.getCredential("id-2", "654321")

        val assertion1 = factory.getMultiFactorAssertion(credential1)
        val assertion2 = factory.getMultiFactorAssertion(credential2)

        assertNotNull(options1)
        assertNotNull(options2)
        assertNotNull(credential1)
        assertNotNull(credential2)
        assertNotNull(assertion1)
        assertNotNull(assertion2)
    }
}
