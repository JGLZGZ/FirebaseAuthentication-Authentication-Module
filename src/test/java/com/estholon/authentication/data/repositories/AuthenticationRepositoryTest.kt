package com.estholon.firebaseauthentication.data.repositories

import android.app.Activity
import androidx.credentials.GetCredentialResponse
import com.estholon.authentication.data.datasources.anonymously.AnonymouslyAuthenticationDataSource
import com.estholon.authentication.data.datasources.common.CommonAuthenticationDataSource
import com.estholon.authentication.data.datasources.email.EmailAuthenticationDataSource
import com.estholon.authentication.data.datasources.facebook.FacebookAuthenticationDataSource
import com.estholon.authentication.data.datasources.github.GitHubAuthenticationDataSource
import com.estholon.authentication.data.datasources.google.GoogleAuthenticationDataSource
import com.estholon.authentication.data.datasources.microsoft.MicrosoftAuthenticationDataSource
import com.estholon.authentication.data.datasources.multifactor.MultifactorAuthenticationDataSource
import com.estholon.authentication.data.datasources.phone.PhoneAuthenticationDataSource
import com.estholon.authentication.data.datasources.twitter.TwitterAuthenticationDataSource
import com.estholon.authentication.data.datasources.yahoo.YahooAuthenticationDataSource
import com.estholon.authentication.data.dtos.UserDto
import com.estholon.authentication.data.mappers.UserMapper
import com.estholon.authentication.data.repositories.AuthenticationRepositoryImpl
import com.estholon.authentication.domain.models.UserModel
import com.facebook.AccessToken
import com.google.firebase.auth.MultiFactorSession
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthenticationRepositoryTest {

    private lateinit var commonDataSource: CommonAuthenticationDataSource
    private lateinit var anonymouslyDataSource: AnonymouslyAuthenticationDataSource
    private lateinit var emailDataSource: EmailAuthenticationDataSource
    private lateinit var googleDataSource: GoogleAuthenticationDataSource
    private lateinit var facebookDataSource: FacebookAuthenticationDataSource
    private lateinit var gitHubDataSource: GitHubAuthenticationDataSource
    private lateinit var microsoftDataSource: MicrosoftAuthenticationDataSource
    private lateinit var phoneDataSource: PhoneAuthenticationDataSource
    private lateinit var twitterDataSource: TwitterAuthenticationDataSource
    private lateinit var yahooDataSource: YahooAuthenticationDataSource
    private lateinit var multifactorDataSource: MultifactorAuthenticationDataSource
    private lateinit var userMapper: UserMapper

    private lateinit var repository: AuthenticationRepositoryImpl

    @Before
    fun setup() {
        commonDataSource = mockk()
        anonymouslyDataSource = mockk()
        emailDataSource = mockk()
        googleDataSource = mockk()
        facebookDataSource = mockk()
        gitHubDataSource = mockk()
        microsoftDataSource = mockk()
        phoneDataSource = mockk()
        twitterDataSource = mockk()
        yahooDataSource = mockk()
        multifactorDataSource = mockk()
        userMapper = UserMapper()

        repository = AuthenticationRepositoryImpl(
            commonDataSource,
            anonymouslyDataSource,
            emailDataSource,
            googleDataSource,
            facebookDataSource,
            gitHubDataSource,
            microsoftDataSource,
            phoneDataSource,
            twitterDataSource,
            yahooDataSource,
            multifactorDataSource,
            userMapper
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ==================== GENERAL FUNCTIONS ====================

    @Test
    fun `isUserLogged delegates to commonAuthenticationDataSource and returns true`() {
        // Given
        every { commonDataSource.isUserLogged() } returns Result.success(true)

        // When
        val result = repository.isUserLogged()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
        verify(exactly = 1) { commonDataSource.isUserLogged() }
    }

    @Test
    fun `isUserLogged delegates to commonAuthenticationDataSource and returns false`() {
        // Given
        every { commonDataSource.isUserLogged() } returns Result.success(false)

        // When
        val result = repository.isUserLogged()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrNull())
        verify(exactly = 1) { commonDataSource.isUserLogged() }
    }

    @Test
    fun `isUserLogged returns failure when datasource fails`() {
        // Given
        val exception = Exception("Auth error")
        every { commonDataSource.isUserLogged() } returns Result.failure(exception)

        // When
        val result = repository.isUserLogged()

        // Then
        assertTrue(result.isFailure)
        assertEquals("Auth error", result.exceptionOrNull()?.message)
        verify(exactly = 1) { commonDataSource.isUserLogged() }
    }

    @Test
    fun `sendEmailVerification delegates to commonAuthenticationDataSource and returns success`() = runTest {
        // Given
        coEvery { commonDataSource.sendEmailVerification() } returns Result.success(Unit)

        // When
        val result = repository.sendEmailVerification()

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { commonDataSource.sendEmailVerification() }
    }

    @Test
    fun `sendEmailVerification returns failure when datasource fails`() = runTest {
        // Given
        val exception = Exception("Email verification failed")
        coEvery { commonDataSource.sendEmailVerification() } returns Result.failure(exception)

        // When
        val result = repository.sendEmailVerification()

        // Then
        assertTrue(result.isFailure)
        assertEquals("Email verification failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { commonDataSource.sendEmailVerification() }
    }

    @Test
    fun `isEmailVerified delegates to commonAuthenticationDataSource and returns true`() = runTest {
        // Given
        coEvery { commonDataSource.isEmailVerified() } returns Result.success(true)

        // When
        val result = repository.isEmailVerified()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
        coVerify(exactly = 1) { commonDataSource.isEmailVerified() }
    }

    @Test
    fun `isEmailVerified delegates to commonAuthenticationDataSource and returns false`() = runTest {
        // Given
        coEvery { commonDataSource.isEmailVerified() } returns Result.success(false)

        // When
        val result = repository.isEmailVerified()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrNull())
        coVerify(exactly = 1) { commonDataSource.isEmailVerified() }
    }

    @Test
    fun `isEmailVerified returns failure when datasource fails`() = runTest {
        // Given
        val exception = Exception("Check email verification failed")
        coEvery { commonDataSource.isEmailVerified() } returns Result.failure(exception)

        // When
        val result = repository.isEmailVerified()

        // Then
        assertTrue(result.isFailure)
        assertEquals("Check email verification failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { commonDataSource.isEmailVerified() }
    }

    // ==================== EMAIL ====================

    @Test
    fun `signUpEmail maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val dto = UserDto(uid = "uid123", email = "a@b.com", displayName = "Name", phoneNumber = "+1")
        coEvery { emailDataSource.signUpEmail("a@b.com", "pwd") } returns Result.success(dto)

        // When
        val result = repository.signUpEmail("a@b.com", "pwd")

        // Then
        assertTrue(result.isSuccess)
        val userModel: UserModel? = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        assertEquals(dto.email, userModel?.email)
        assertEquals(dto.displayName, userModel?.displayName)
        assertEquals(dto.phoneNumber, userModel?.phoneNumber)
        coVerify(exactly = 1) { emailDataSource.signUpEmail("a@b.com", "pwd") }
    }

    @Test
    fun `signUpEmail returns null when datasource returns success with null user`() = runTest {
        // Given
        coEvery { emailDataSource.signUpEmail("x@x.com", "pwd") } returns Result.success(null)

        // When
        val result = repository.signUpEmail("x@x.com", "pwd")

        // Then
        assertTrue(result.isSuccess)
        val userModel: UserModel? = result.getOrNull()
        assertEquals(null, userModel)
        coVerify(exactly = 1) { emailDataSource.signUpEmail("x@x.com", "pwd") }
    }

    @Test
    fun `signUpEmail returns failure when datasource fails`() = runTest {
        // Given
        val exception = Exception("Sign up failed")
        coEvery { emailDataSource.signUpEmail("a@b.com", "pwd") } returns Result.failure(exception)

        // When
        val result = repository.signUpEmail("a@b.com", "pwd")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Sign up failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { emailDataSource.signUpEmail("a@b.com", "pwd") }
    }

    @Test
    fun `signInEmail maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val dto = UserDto(uid = "uid123", email = "a@b.com", displayName = "Name", phoneNumber = "+1")
        coEvery { emailDataSource.signInEmail("a@b.com", "pwd") } returns Result.success(dto)

        // When
        val result = repository.signInEmail("a@b.com", "pwd")

        // Then
        assertTrue(result.isSuccess)
        val userModel: UserModel? = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        assertEquals(dto.email, userModel?.email)
        assertEquals(dto.displayName, userModel?.displayName)
        assertEquals(dto.phoneNumber, userModel?.phoneNumber)
        coVerify(exactly = 1) { emailDataSource.signInEmail("a@b.com", "pwd") }
    }

    @Test
    fun `signInEmail returns null when datasource returns success with null user`() = runTest {
        // Given
        coEvery { emailDataSource.signInEmail("x@x.com", "pwd") } returns Result.success(null)

        // When
        val result = repository.signInEmail("x@x.com", "pwd")

        // Then
        assertTrue(result.isSuccess)
        val userModel: UserModel? = result.getOrNull()
        assertEquals(null, userModel)
        coVerify(exactly = 1) { emailDataSource.signInEmail("x@x.com", "pwd") }
    }

    @Test
    fun `signInEmail returns failure when datasource fails`() = runTest {
        // Given
        val exception = Exception("Sign in failed")
        coEvery { emailDataSource.signInEmail("a@b.com", "pwd") } returns Result.failure(exception)

        // When
        val result = repository.signInEmail("a@b.com", "pwd")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Sign in failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { emailDataSource.signInEmail("a@b.com", "pwd") }
    }

    @Test
    fun `linkEmail maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val dto = UserDto(uid = "uid123", email = "a@b.com", displayName = "Name", phoneNumber = "+1")
        coEvery { emailDataSource.linkEmail("a@b.com", "pwd") } returns Result.success(dto)

        // When
        val result = repository.linkEmail("a@b.com", "pwd")

        // Then
        assertTrue(result.isSuccess)
        val userModel: UserModel? = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        assertEquals(dto.email, userModel?.email)
        coVerify(exactly = 1) { emailDataSource.linkEmail("a@b.com", "pwd") }
    }

    @Test
    fun `linkEmail returns null when datasource returns success with null user`() = runTest {
        // Given
        coEvery { emailDataSource.linkEmail("x@x.com", "pwd") } returns Result.success(null)

        // When
        val result = repository.linkEmail("x@x.com", "pwd")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { emailDataSource.linkEmail("x@x.com", "pwd") }
    }

    @Test
    fun `linkEmail returns failure when datasource fails`() = runTest {
        // Given
        val exception = Exception("Link email failed")
        coEvery { emailDataSource.linkEmail("a@b.com", "pwd") } returns Result.failure(exception)

        // When
        val result = repository.linkEmail("a@b.com", "pwd")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Link email failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { emailDataSource.linkEmail("a@b.com", "pwd") }
    }

    @Test
    fun `resetPassword delegates to emailDataSource`() = runTest {
        // Given
        coEvery { emailDataSource.resetPassword("test@t.com") } returns Result.success(Unit)

        // When
        val result = repository.resetPassword("test@t.com")

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { emailDataSource.resetPassword("test@t.com") }
    }

    @Test
    fun `resetPassword returns failure when datasource fails`() = runTest {
        // Given
        val exception = Exception("Reset password failed")
        coEvery { emailDataSource.resetPassword("test@t.com") } returns Result.failure(exception)

        // When
        val result = repository.resetPassword("test@t.com")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Reset password failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { emailDataSource.resetPassword("test@t.com") }
    }

    // ==================== ANONYMOUSLY ====================

    @Test
    fun `signInAnonymously maps dto to domain when datasource returns user`() = runTest {
        // Given
        val dto = UserDto(uid = "anonUid", email = null, displayName = null, phoneNumber = null)
        coEvery { anonymouslyDataSource.signInAnonymously() } returns Result.success(dto)

        // When
        val result = repository.signInAnonymously()

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        coVerify(exactly = 1) { anonymouslyDataSource.signInAnonymously() }
    }

    @Test
    fun `signInAnonymously returns null when datasource returns success with null user`() = runTest {
        // Given
        coEvery { anonymouslyDataSource.signInAnonymously() } returns Result.success(null)

        // When
        val result = repository.signInAnonymously()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { anonymouslyDataSource.signInAnonymously() }
    }

    @Test
    fun `signInAnonymously returns failure when datasource fails`() = runTest {
        // Given
        val exception = Exception("Anonymous sign in failed")
        coEvery { anonymouslyDataSource.signInAnonymously() } returns Result.failure(exception)

        // When
        val result = repository.signInAnonymously()

        // Then
        assertTrue(result.isFailure)
        assertEquals("Anonymous sign in failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { anonymouslyDataSource.signInAnonymously() }
    }

    // ==================== PHONE ====================

    // Note: signInPhone test removed because PhoneAuthProvider.OnVerificationStateChangedCallbacks
    // has static initializers that cannot be properly mocked without Robolectric.
    // This functionality should be tested via instrumented tests.

    @Test
    fun `verifyCode maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val dto = UserDto(uid = "phoneUid", email = null, displayName = null, phoneNumber = "+1234567890")
        coEvery { phoneDataSource.verifyCode("verificationId", "123456") } returns Result.success(dto)

        // When
        val result = repository.verifyCode("verificationId", "123456")

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        assertEquals(dto.phoneNumber, userModel?.phoneNumber)
        coVerify(exactly = 1) { phoneDataSource.verifyCode("verificationId", "123456") }
    }

    @Test
    fun `verifyCode returns null when datasource returns success with null user`() = runTest {
        // Given
        coEvery { phoneDataSource.verifyCode("verificationId", "123456") } returns Result.success(null)

        // When
        val result = repository.verifyCode("verificationId", "123456")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { phoneDataSource.verifyCode("verificationId", "123456") }
    }

    @Test
    fun `verifyCode returns failure when datasource fails`() = runTest {
        // Given
        val exception = Exception("Verification failed")
        coEvery { phoneDataSource.verifyCode("verificationId", "123456") } returns Result.failure(exception)

        // When
        val result = repository.verifyCode("verificationId", "123456")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Verification failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { phoneDataSource.verifyCode("verificationId", "123456") }
    }

    // ==================== GOOGLE ====================

    @Test
    fun `signInGoogleCredentialManager maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val dto = UserDto(uid = "googleUid", email = "google@gmail.com", displayName = "Google User", phoneNumber = null)
        coEvery { googleDataSource.signInGoogleCredentialManager(activity) } returns Result.success(dto)

        // When
        val result = repository.signInGoogleCredentialManager(activity)

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        assertEquals(dto.email, userModel?.email)
        coVerify(exactly = 1) { googleDataSource.signInGoogleCredentialManager(activity) }
    }

    @Test
    fun `signInGoogleCredentialManager returns null when datasource returns success with null user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        coEvery { googleDataSource.signInGoogleCredentialManager(activity) } returns Result.success(null)

        // When
        val result = repository.signInGoogleCredentialManager(activity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { googleDataSource.signInGoogleCredentialManager(activity) }
    }

    @Test
    fun `signInGoogleCredentialManager returns failure when datasource fails`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val exception = Exception("Google sign in failed")
        coEvery { googleDataSource.signInGoogleCredentialManager(activity) } returns Result.failure(exception)

        // When
        val result = repository.signInGoogleCredentialManager(activity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Google sign in failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { googleDataSource.signInGoogleCredentialManager(activity) }
    }

    @Test
    fun `signInGoogle maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val dto = UserDto(uid = "googleUid", email = "google@gmail.com", displayName = "Google User", phoneNumber = null)
        coEvery { googleDataSource.signInGoogle(activity) } returns Result.success(dto)

        // When
        val result = repository.signInGoogle(activity)

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        assertEquals(dto.email, userModel?.email)
        coVerify(exactly = 1) { googleDataSource.signInGoogle(activity) }
    }

    @Test
    fun `signInGoogle returns null when datasource returns success with null user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        coEvery { googleDataSource.signInGoogle(activity) } returns Result.success(null)

        // When
        val result = repository.signInGoogle(activity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { googleDataSource.signInGoogle(activity) }
    }

    @Test
    fun `signInGoogle returns failure when datasource fails`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val exception = Exception("Google sign in failed")
        coEvery { googleDataSource.signInGoogle(activity) } returns Result.failure(exception)

        // When
        val result = repository.signInGoogle(activity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Google sign in failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { googleDataSource.signInGoogle(activity) }
    }

    @Test
    fun `handleCredentialResponse maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val credentialResponse = mockk<GetCredentialResponse>(relaxed = true)
        val dto = UserDto(uid = "googleUid", email = "google@gmail.com", displayName = "Google User", phoneNumber = null)
        coEvery { googleDataSource.handleCredentialResponse(credentialResponse) } returns Result.success(dto)

        // When
        val result = repository.handleCredentialResponse(credentialResponse)

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        coVerify(exactly = 1) { googleDataSource.handleCredentialResponse(credentialResponse) }
    }

    @Test
    fun `handleCredentialResponse returns null when datasource returns success with null user`() = runTest {
        // Given
        val credentialResponse = mockk<GetCredentialResponse>(relaxed = true)
        coEvery { googleDataSource.handleCredentialResponse(credentialResponse) } returns Result.success(null)

        // When
        val result = repository.handleCredentialResponse(credentialResponse)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { googleDataSource.handleCredentialResponse(credentialResponse) }
    }

    @Test
    fun `handleCredentialResponse returns failure when datasource fails`() = runTest {
        // Given
        val credentialResponse = mockk<GetCredentialResponse>(relaxed = true)
        val exception = Exception("Handle credential failed")
        coEvery { googleDataSource.handleCredentialResponse(credentialResponse) } returns Result.failure(exception)

        // When
        val result = repository.handleCredentialResponse(credentialResponse)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Handle credential failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { googleDataSource.handleCredentialResponse(credentialResponse) }
    }

    @Test
    fun `clearCredentialState delegates to googleDataSource`() = runTest {
        // Given
        coEvery { googleDataSource.clearCredentialState() } returns Unit

        // When
        repository.clearCredentialState()

        // Then
        coVerify(exactly = 1) { googleDataSource.clearCredentialState() }
    }

    @Test
    fun `linkGoogle maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val dto = UserDto(uid = "googleUid", email = "google@gmail.com", displayName = "Google User", phoneNumber = null)
        coEvery { googleDataSource.linkGoogle(activity) } returns Result.success(dto)

        // When
        val result = repository.linkGoogle(activity)

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        coVerify(exactly = 1) { googleDataSource.linkGoogle(activity) }
    }

    @Test
    fun `linkGoogle returns null when datasource returns success with null user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        coEvery { googleDataSource.linkGoogle(activity) } returns Result.success(null)

        // When
        val result = repository.linkGoogle(activity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { googleDataSource.linkGoogle(activity) }
    }

    @Test
    fun `linkGoogle returns failure when datasource fails`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val exception = Exception("Link Google failed")
        coEvery { googleDataSource.linkGoogle(activity) } returns Result.failure(exception)

        // When
        val result = repository.linkGoogle(activity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Link Google failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { googleDataSource.linkGoogle(activity) }
    }

    // ==================== FACEBOOK ====================

    @Test
    fun `signInFacebook maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>(relaxed = true)
        val dto = UserDto(uid = "fbUid", email = "fb@facebook.com", displayName = "FB User", phoneNumber = null)
        coEvery { facebookDataSource.signInFacebook(accessToken) } returns Result.success(dto)

        // When
        val result = repository.signInFacebook(accessToken)

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        assertEquals(dto.email, userModel?.email)
        coVerify(exactly = 1) { facebookDataSource.signInFacebook(accessToken) }
    }

    @Test
    fun `signInFacebook returns null when datasource returns success with null user`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>(relaxed = true)
        coEvery { facebookDataSource.signInFacebook(accessToken) } returns Result.success(null)

        // When
        val result = repository.signInFacebook(accessToken)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { facebookDataSource.signInFacebook(accessToken) }
    }

    @Test
    fun `signInFacebook returns failure when datasource fails`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>(relaxed = true)
        val exception = Exception("Facebook sign in failed")
        coEvery { facebookDataSource.signInFacebook(accessToken) } returns Result.failure(exception)

        // When
        val result = repository.signInFacebook(accessToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Facebook sign in failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { facebookDataSource.signInFacebook(accessToken) }
    }

    @Test
    fun `linkFacebook maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>(relaxed = true)
        val dto = UserDto(uid = "fbUid", email = "fb@facebook.com", displayName = "FB User", phoneNumber = null)
        coEvery { facebookDataSource.linkFacebook(accessToken) } returns Result.success(dto)

        // When
        val result = repository.linkFacebook(accessToken)

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        coVerify(exactly = 1) { facebookDataSource.linkFacebook(accessToken) }
    }

    @Test
    fun `linkFacebook returns null when datasource returns success with null user`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>(relaxed = true)
        coEvery { facebookDataSource.linkFacebook(accessToken) } returns Result.success(null)

        // When
        val result = repository.linkFacebook(accessToken)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { facebookDataSource.linkFacebook(accessToken) }
    }

    @Test
    fun `linkFacebook returns failure when datasource fails`() = runTest {
        // Given
        val accessToken = mockk<AccessToken>(relaxed = true)
        val exception = Exception("Link Facebook failed")
        coEvery { facebookDataSource.linkFacebook(accessToken) } returns Result.failure(exception)

        // When
        val result = repository.linkFacebook(accessToken)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Link Facebook failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { facebookDataSource.linkFacebook(accessToken) }
    }

    // ==================== GITHUB ====================

    @Test
    fun `signInGitHub maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val dto = UserDto(uid = "ghUid", email = "gh@github.com", displayName = "GitHub User", phoneNumber = null)
        coEvery { gitHubDataSource.signInGitHub(activity) } returns Result.success(dto)

        // When
        val result = repository.signInGitHub(activity)

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        assertEquals(dto.email, userModel?.email)
        coVerify(exactly = 1) { gitHubDataSource.signInGitHub(activity) }
    }

    @Test
    fun `signInGitHub returns null when datasource returns success with null user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        coEvery { gitHubDataSource.signInGitHub(activity) } returns Result.success(null)

        // When
        val result = repository.signInGitHub(activity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { gitHubDataSource.signInGitHub(activity) }
    }

    @Test
    fun `signInGitHub returns failure when datasource fails`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val exception = Exception("GitHub sign in failed")
        coEvery { gitHubDataSource.signInGitHub(activity) } returns Result.failure(exception)

        // When
        val result = repository.signInGitHub(activity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("GitHub sign in failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { gitHubDataSource.signInGitHub(activity) }
    }

    @Test
    fun `linkGitHub maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val dto = UserDto(uid = "ghUid", email = "gh@github.com", displayName = "GitHub User", phoneNumber = null)
        coEvery { gitHubDataSource.linkGitHub(activity) } returns Result.success(dto)

        // When
        val result = repository.linkGitHub(activity)

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        coVerify(exactly = 1) { gitHubDataSource.linkGitHub(activity) }
    }

    @Test
    fun `linkGitHub returns null when datasource returns success with null user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        coEvery { gitHubDataSource.linkGitHub(activity) } returns Result.success(null)

        // When
        val result = repository.linkGitHub(activity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { gitHubDataSource.linkGitHub(activity) }
    }

    @Test
    fun `linkGitHub returns failure when datasource fails`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val exception = Exception("Link GitHub failed")
        coEvery { gitHubDataSource.linkGitHub(activity) } returns Result.failure(exception)

        // When
        val result = repository.linkGitHub(activity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Link GitHub failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { gitHubDataSource.linkGitHub(activity) }
    }

    // ==================== MICROSOFT ====================

    @Test
    fun `signInMicrosoft maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val dto = UserDto(uid = "msUid", email = "ms@outlook.com", displayName = "MS User", phoneNumber = null)
        coEvery { microsoftDataSource.signInMicrosoft(activity) } returns Result.success(dto)

        // When
        val result = repository.signInMicrosoft(activity)

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        assertEquals(dto.email, userModel?.email)
        coVerify(exactly = 1) { microsoftDataSource.signInMicrosoft(activity) }
    }

    @Test
    fun `signInMicrosoft returns null when datasource returns success with null user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        coEvery { microsoftDataSource.signInMicrosoft(activity) } returns Result.success(null)

        // When
        val result = repository.signInMicrosoft(activity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { microsoftDataSource.signInMicrosoft(activity) }
    }

    @Test
    fun `signInMicrosoft returns failure when datasource fails`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val exception = Exception("Microsoft sign in failed")
        coEvery { microsoftDataSource.signInMicrosoft(activity) } returns Result.failure(exception)

        // When
        val result = repository.signInMicrosoft(activity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Microsoft sign in failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { microsoftDataSource.signInMicrosoft(activity) }
    }

    @Test
    fun `linkMicrosoft maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val dto = UserDto(uid = "msUid", email = "ms@outlook.com", displayName = "MS User", phoneNumber = null)
        coEvery { microsoftDataSource.linkMicrosoft(activity) } returns Result.success(dto)

        // When
        val result = repository.linkMicrosoft(activity)

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        coVerify(exactly = 1) { microsoftDataSource.linkMicrosoft(activity) }
    }

    @Test
    fun `linkMicrosoft returns null when datasource returns success with null user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        coEvery { microsoftDataSource.linkMicrosoft(activity) } returns Result.success(null)

        // When
        val result = repository.linkMicrosoft(activity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { microsoftDataSource.linkMicrosoft(activity) }
    }

    @Test
    fun `linkMicrosoft returns failure when datasource fails`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val exception = Exception("Link Microsoft failed")
        coEvery { microsoftDataSource.linkMicrosoft(activity) } returns Result.failure(exception)

        // When
        val result = repository.linkMicrosoft(activity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Link Microsoft failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { microsoftDataSource.linkMicrosoft(activity) }
    }

    // ==================== TWITTER ====================

    @Test
    fun `signInTwitter maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val dto = UserDto(uid = "twUid", email = "tw@twitter.com", displayName = "Twitter User", phoneNumber = null)
        coEvery { twitterDataSource.signInTwitter(activity) } returns Result.success(dto)

        // When
        val result = repository.signInTwitter(activity)

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        assertEquals(dto.email, userModel?.email)
        coVerify(exactly = 1) { twitterDataSource.signInTwitter(activity) }
    }

    @Test
    fun `signInTwitter returns null when datasource returns success with null user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        coEvery { twitterDataSource.signInTwitter(activity) } returns Result.success(null)

        // When
        val result = repository.signInTwitter(activity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { twitterDataSource.signInTwitter(activity) }
    }

    @Test
    fun `signInTwitter returns failure when datasource fails`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val exception = Exception("Twitter sign in failed")
        coEvery { twitterDataSource.signInTwitter(activity) } returns Result.failure(exception)

        // When
        val result = repository.signInTwitter(activity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Twitter sign in failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { twitterDataSource.signInTwitter(activity) }
    }

    @Test
    fun `linkTwitter maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val dto = UserDto(uid = "twUid", email = "tw@twitter.com", displayName = "Twitter User", phoneNumber = null)
        coEvery { twitterDataSource.linkTwitter(activity) } returns Result.success(dto)

        // When
        val result = repository.linkTwitter(activity)

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        coVerify(exactly = 1) { twitterDataSource.linkTwitter(activity) }
    }

    @Test
    fun `linkTwitter returns null when datasource returns success with null user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        coEvery { twitterDataSource.linkTwitter(activity) } returns Result.success(null)

        // When
        val result = repository.linkTwitter(activity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { twitterDataSource.linkTwitter(activity) }
    }

    @Test
    fun `linkTwitter returns failure when datasource fails`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val exception = Exception("Link Twitter failed")
        coEvery { twitterDataSource.linkTwitter(activity) } returns Result.failure(exception)

        // When
        val result = repository.linkTwitter(activity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Link Twitter failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { twitterDataSource.linkTwitter(activity) }
    }

    // ==================== YAHOO ====================

    @Test
    fun `signInYahoo maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val dto = UserDto(uid = "yahooUid", email = "user@yahoo.com", displayName = "Yahoo User", phoneNumber = null)
        coEvery { yahooDataSource.signInYahoo(activity) } returns Result.success(dto)

        // When
        val result = repository.signInYahoo(activity)

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        assertEquals(dto.email, userModel?.email)
        coVerify(exactly = 1) { yahooDataSource.signInYahoo(activity) }
    }

    @Test
    fun `signInYahoo returns null when datasource returns success with null user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        coEvery { yahooDataSource.signInYahoo(activity) } returns Result.success(null)

        // When
        val result = repository.signInYahoo(activity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { yahooDataSource.signInYahoo(activity) }
    }

    @Test
    fun `signInYahoo returns failure when datasource fails`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val exception = Exception("Yahoo sign in failed")
        coEvery { yahooDataSource.signInYahoo(activity) } returns Result.failure(exception)

        // When
        val result = repository.signInYahoo(activity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Yahoo sign in failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { yahooDataSource.signInYahoo(activity) }
    }

    @Test
    fun `linkYahoo maps UserDto to UserModel when datasource returns a user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val dto = UserDto(uid = "yahooUid", email = "user@yahoo.com", displayName = "Yahoo User", phoneNumber = null)
        coEvery { yahooDataSource.linkYahoo(activity) } returns Result.success(dto)

        // When
        val result = repository.linkYahoo(activity)

        // Then
        assertTrue(result.isSuccess)
        val userModel = result.getOrNull()
        assertNotNull(userModel)
        assertEquals(dto.uid, userModel?.uid)
        coVerify(exactly = 1) { yahooDataSource.linkYahoo(activity) }
    }

    @Test
    fun `linkYahoo returns null when datasource returns success with null user`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        coEvery { yahooDataSource.linkYahoo(activity) } returns Result.success(null)

        // When
        val result = repository.linkYahoo(activity)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
        coVerify(exactly = 1) { yahooDataSource.linkYahoo(activity) }
    }

    @Test
    fun `linkYahoo returns failure when datasource fails`() = runTest {
        // Given
        val activity = mockk<Activity>(relaxed = true)
        val exception = Exception("Link Yahoo failed")
        coEvery { yahooDataSource.linkYahoo(activity) } returns Result.failure(exception)

        // When
        val result = repository.linkYahoo(activity)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Link Yahoo failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { yahooDataSource.linkYahoo(activity) }
    }

    // ==================== MFA ====================

    @Test
    fun `getMultifactorSession delegates to multifactorDataSource and returns session`() = runTest {
        // Given
        val session = mockk<MultiFactorSession>(relaxed = true)
        coEvery { multifactorDataSource.getMultifactorSession() } returns session

        // When
        val result = repository.getMultifactorSession()

        // Then
        assertEquals(session, result)
        coVerify(exactly = 1) { multifactorDataSource.getMultifactorSession() }
    }

    @Test
    fun `getMultifactorSession throws exception when datasource throws`() = runTest {
        // Given
        val exception = Exception("No authenticated user")
        coEvery { multifactorDataSource.getMultifactorSession() } throws exception

        // When / Then
        try {
            repository.getMultifactorSession()
            fail("Expected exception to be thrown")
        } catch (e: Exception) {
            assertEquals("No authenticated user", e.message)
        }
        coVerify(exactly = 1) { multifactorDataSource.getMultifactorSession() }
    }

    @Test
    fun `enrollMfaSendSms delegates to multifactorDataSource and returns verificationId`() = runTest {
        // Given
        val session = mockk<MultiFactorSession>(relaxed = true)
        val phoneNumber = "+1234567890"
        coEvery { multifactorDataSource.enrollMfaSendSms(session, phoneNumber) } returns Result.success("verificationId123")

        // When
        val result = repository.enrollMfaSendSms(session, phoneNumber)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("verificationId123", result.getOrNull())
        coVerify(exactly = 1) { multifactorDataSource.enrollMfaSendSms(session, phoneNumber) }
    }

    @Test
    fun `enrollMfaSendSms returns failure when datasource fails`() = runTest {
        // Given
        val session = mockk<MultiFactorSession>(relaxed = true)
        val phoneNumber = "+1234567890"
        val exception = Exception("SMS send failed")
        coEvery { multifactorDataSource.enrollMfaSendSms(session, phoneNumber) } returns Result.failure(exception)

        // When
        val result = repository.enrollMfaSendSms(session, phoneNumber)

        // Then
        assertTrue(result.isFailure)
        assertEquals("SMS send failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { multifactorDataSource.enrollMfaSendSms(session, phoneNumber) }
    }

    @Test
    fun `verifySmsForEnroll emits success from flow when datasource succeeds`() = runTest {
        // Given
        val verificationId = "verificationId123"
        val verificationCode = "123456"
        coEvery { multifactorDataSource.verifySmsForEnroll(verificationId, verificationCode) } returns Result.success(Unit)

        // When
        val flow = repository.verifySmsForEnroll(verificationId, verificationCode)
        val result = flow.first()

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { multifactorDataSource.verifySmsForEnroll(verificationId, verificationCode) }
    }

    @Test
    fun `verifySmsForEnroll emits failure from flow when datasource fails`() = runTest {
        // Given
        val verificationId = "verificationId123"
        val verificationCode = "123456"
        val exception = Exception("Verification failed")
        coEvery { multifactorDataSource.verifySmsForEnroll(verificationId, verificationCode) } returns Result.failure(exception)

        // When
        val flow = repository.verifySmsForEnroll(verificationId, verificationCode)
        val result = flow.first()

        // Then
        assertTrue(result.isFailure)
        assertEquals("Verification failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { multifactorDataSource.verifySmsForEnroll(verificationId, verificationCode) }
    }

    // ==================== SIGN OUT ====================

    @Test
    fun `signOut delegates to commonAuthenticationDataSource`() = runTest {
        // Given
        coEvery { commonDataSource.signOut() } returns Unit

        // When
        repository.signOut()

        // Then
        coVerify(exactly = 1) { commonDataSource.signOut() }
    }
}
