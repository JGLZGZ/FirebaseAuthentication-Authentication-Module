package com.estholon.firebaseauthentication.data.repositories

import com.estholon.firebaseauthentication.data.datasources.authentication.anonymously.AnonymouslyAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.common.CommonAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.email.EmailAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.facebook.FacebookAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.github.GitHubAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.google.GoogleAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.microsoft.MicrosoftAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.multifactor.MultifactorAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.phone.PhoneAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.twitter.TwitterAuthenticationDataSource
import com.estholon.firebaseauthentication.data.datasources.authentication.yahoo.YahooAuthenticationDataSource
import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.estholon.firebaseauthentication.data.mapper.UserMapper
import com.estholon.firebaseauthentication.domain.models.UserModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
    fun `signOut delegates to commonAuthenticationDataSource`() = runTest {
        // Given
        coEvery { commonDataSource.signOut() } returns Unit

        // When
        repository.signOut()

        // Then
        coVerify(exactly = 1) { commonDataSource.signOut() }
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
}