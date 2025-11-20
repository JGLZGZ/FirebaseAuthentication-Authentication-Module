package com.example.authentication.di

import com.example.authentication.data.datasources.anonymously.AnonymouslyAuthenticationDataSource
import com.example.authentication.data.datasources.anonymously.AnonymouslyFirebaseAuthenticationDataSource
import com.example.authentication.data.datasources.common.CommonAuthenticationDataSource
import com.example.authentication.data.datasources.common.CommonFirebaseAuthenticationDataSource
import com.example.authentication.data.datasources.email.EmailAuthenticationDataSource
import com.example.authentication.data.datasources.email.EmailFirebaseAuthenticationDataSource
import com.example.authentication.data.datasources.facebook.FacebookAuthenticationDataSource
import com.example.authentication.data.datasources.facebook.FacebookFirebaseAuthenticationDataSource
import com.example.authentication.data.datasources.github.GitHubAuthenticationDataSource
import com.example.authentication.data.datasources.github.GitHubFirebaseAuthenticationDataSource
import com.example.authentication.data.datasources.google.GoogleAuthenticationDataSource
import com.example.authentication.data.datasources.google.GoogleFirebaseAuthenticationDataSource
import com.example.authentication.data.datasources.microsoft.MicrosoftAuthenticationDataSource
import com.example.authentication.data.datasources.microsoft.MicrosoftFirebaseAuthenticationDataSource
import com.example.authentication.data.datasources.multifactor.MultifactorAuthenticationDataSource
import com.example.authentication.data.datasources.multifactor.MultifactorFirebaseAuthenticationDataSource
import com.example.authentication.data.datasources.phone.PhoneAuthenticationDataSource
import com.example.authentication.data.datasources.phone.PhoneFirebaseAuthenticationDataSource
import com.example.authentication.data.datasources.twitter.TwitterAuthenticationDataSource
import com.example.authentication.data.datasources.twitter.TwitterFirebaseAuthenticationDataSource
import com.example.authentication.data.datasources.yahoo.YahooAuthenticationDataSource
import com.example.authentication.data.datasources.yahoo.YahooFirebaseAuthenticationDataSource
import com.example.authentication.data.mappers.UserMapper
import com.example.authentication.data.repositories.AuthenticationRepositoryImpl
import com.example.authentication.domain.repositories.AuthenticationRepository
import com.example.authentication.domain.usecases.anonymously.SignInAnonymouslyUseCase
import com.example.authentication.domain.usecases.anonymously.SignInAnonymouslyUseCaseImpl
import com.example.authentication.domain.usecases.common.IsEmailVerifiedUseCase
import com.example.authentication.domain.usecases.common.IsEmailVerifiedUseCaseImpl
import com.example.authentication.domain.usecases.common.SignOutUseCase
import com.example.authentication.domain.usecases.common.SignOutUseCaseImpl
import com.example.authentication.domain.usecases.email.IsEmailValidUseCase
import com.example.authentication.domain.usecases.email.IsEmailValidUseCaseImpl
import com.example.authentication.domain.usecases.email.IsPasswordValidUseCase
import com.example.authentication.domain.usecases.email.IsPasswordValidUseCaseImpl
import com.example.authentication.domain.usecases.email.LinkEmailUseCase
import com.example.authentication.domain.usecases.email.LinkEmailUseCaseImpl
import com.example.authentication.domain.usecases.email.ResetPasswordUseCase
import com.example.authentication.domain.usecases.email.ResetPasswordUseCaseImpl
import com.example.authentication.domain.usecases.email.SignInEmailUseCase
import com.example.authentication.domain.usecases.email.SignInEmailUseCaseImpl
import com.example.authentication.domain.usecases.email.SignUpEmailUseCase
import com.example.authentication.domain.usecases.email.SignUpEmailUseCaseImpl
import com.example.authentication.domain.usecases.facebook.LinkFacebookUseCase
import com.example.authentication.domain.usecases.facebook.LinkFacebookUseCaseImpl
import com.example.authentication.domain.usecases.facebook.SignInFacebookUseCase
import com.example.authentication.domain.usecases.facebook.SignInFacebookUseCaseImpl
import com.example.authentication.domain.usecases.github.LinkGitHubUseCase
import com.example.authentication.domain.usecases.github.LinkGitHubUseCaseImpl
import com.example.authentication.domain.usecases.github.SignInGitHubUseCase
import com.example.authentication.domain.usecases.github.SignInGitHubUseCaseImpl
import com.example.authentication.domain.usecases.google.ClearCredentialStateUseCase
import com.example.authentication.domain.usecases.google.ClearCredentialStateUseCaseImpl
import com.example.authentication.domain.usecases.google.LinkGoogleUseCase
import com.example.authentication.domain.usecases.google.LinkGoogleUseCaseImpl
import com.example.authentication.domain.usecases.google.SignInGoogleCredentialManagerUseCase
import com.example.authentication.domain.usecases.google.SignInGoogleCredentialManagerUseCaseImpl
import com.example.authentication.domain.usecases.google.SignInGoogleUseCase
import com.example.authentication.domain.usecases.google.SignInGoogleUseCaseImpl
import com.example.authentication.domain.usecases.microsoft.LinkMicrosoftUseCase
import com.example.authentication.domain.usecases.microsoft.LinkMicrosoftUseCaseImpl
import com.example.authentication.domain.usecases.microsoft.SignInMicrosoftUseCase
import com.example.authentication.domain.usecases.microsoft.SignInMicrosoftUseCaseImpl
import com.example.authentication.domain.usecases.multifactor.SendVerificationEmailUseCase
import com.example.authentication.domain.usecases.multifactor.SendVerificationEmailUseCaseImpl
import com.example.authentication.domain.usecases.multifactor.StartEnrollPhoneUseCase
import com.example.authentication.domain.usecases.multifactor.StartEnrollPhoneUseCaseImpl
import com.example.authentication.domain.usecases.twitter.LinkTwitterUseCase
import com.example.authentication.domain.usecases.twitter.LinkTwitterUseCaseImpl
import com.example.authentication.domain.usecases.twitter.SignInTwitterUseCase
import com.example.authentication.domain.usecases.twitter.SignInTwitterUseCaseImpl
import com.example.authentication.domain.usecases.yahoo.LinkYahooUseCase
import com.example.authentication.domain.usecases.yahoo.LinkYahooUseCaseImpl
import com.example.authentication.domain.usecases.yahoo.SignInYahooUseCase
import com.example.authentication.domain.usecases.yahoo.SignInYahooUseCaseImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Provides
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseModule {

    @Binds
    @Singleton
    abstract fun bindCommonAuthenticationDataSource(
        commonFirebaseAuthenticationDataSource: CommonFirebaseAuthenticationDataSource
    ): CommonAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindAnonymouslyAuthenticationDataSource(
        anonymouslyFirebaseAuthenticationDataSource: AnonymouslyFirebaseAuthenticationDataSource
    ): AnonymouslyAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindEmailAuthenticationDataSource(
        emailFirebaseAuthenticationDataSource: EmailFirebaseAuthenticationDataSource
    ): EmailAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindGoogleAuthenticationDataSource(
        googleFirebaseAuthenticationDataSource: GoogleFirebaseAuthenticationDataSource
    ): GoogleAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindFacebookAuthenticationDataSource(
        facebookFirebaseAuthenticationDataSource: FacebookFirebaseAuthenticationDataSource
    ): FacebookAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindGitHubAuthenticationDataSource(
        gitHubFirebaseAuthenticationDataSource: GitHubFirebaseAuthenticationDataSource
    ): GitHubAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindMicrosoftAuthenticationDataSource(
        microsoftFirebaseAuthenticationDataSource: MicrosoftFirebaseAuthenticationDataSource
    ): MicrosoftAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindTwitterAuthenticationDataSource(
        twitterFirebaseAuthenticationDataSource: TwitterFirebaseAuthenticationDataSource
    ): TwitterAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindYahooAuthenticationDataSource(
        yahooFirebaseAuthenticationDataSource: YahooFirebaseAuthenticationDataSource
    ): YahooAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindPhoneAuthenticationDataSource(
        phoneFirebaseAuthenticationDataSource: PhoneFirebaseAuthenticationDataSource
    ): PhoneAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindMultifactorAuthenticationDataSource(
        multifactorFirebaseAuthenticationDataSource: MultifactorFirebaseAuthenticationDataSource
    ): MultifactorAuthenticationDataSource

    @Binds
    @Singleton
    abstract fun bindAuthenticationRepository(
        authenticationRepositoryImpl: AuthenticationRepositoryImpl
    ): AuthenticationRepository


    // USE CASES

    @Binds
    @Singleton
    abstract fun provideStartEnrollPhoneUseCase(
        startEnrollPhoneUseCaseImpl: StartEnrollPhoneUseCaseImpl
    ): StartEnrollPhoneUseCase

    @Binds
    @Singleton
    abstract fun provideSignInAnonymouslyUseCase(
        signInAnonymouslyUseCaseImpl: SignInAnonymouslyUseCaseImpl
    ): SignInAnonymouslyUseCase

    @Binds
    @Singleton
    abstract fun provideSignOutUseCase(
        signOutUseCaseImpl: SignOutUseCaseImpl
    ): SignOutUseCase

    @Binds
    @Singleton
    abstract fun provideIsEmailValidUseCase(
        isEmailValidUseCaseImpl: IsEmailValidUseCaseImpl
    ): IsEmailValidUseCase

    @Binds
    @Singleton
    abstract fun provideIsPasswordValidUseCase(
        isPasswordValidUseCaseImpl: IsPasswordValidUseCaseImpl
    ): IsPasswordValidUseCase

    @Binds
    @Singleton
    abstract fun provideLinkEmailUseCase(
        linkEmailUseCaseImpl: LinkEmailUseCaseImpl
    ): LinkEmailUseCase

    @Binds
    @Singleton
    abstract fun provideResetPasswordUseCase(
        resetPasswordUseCaseImpl: ResetPasswordUseCaseImpl
    ): ResetPasswordUseCase

    @Binds
    @Singleton
    abstract fun provideSignInEmailUseCase(
        signInEmailUseCaseImpl: SignInEmailUseCaseImpl
    ): SignInEmailUseCase

    @Binds
    @Singleton
    abstract fun provideSignUpEmailUseCase(
        signUpEmailUseCaseImpl: SignUpEmailUseCaseImpl
    ): SignUpEmailUseCase

    @Binds
    @Singleton
    abstract fun provideLinkFacebookUseCase(
        linkFacebookUseCaseImpl: LinkFacebookUseCaseImpl
    ): LinkFacebookUseCase

    @Binds
    @Singleton
    abstract fun provideSignInFacebookUseCase(
        signInFacebookUseCaseImpl: SignInFacebookUseCaseImpl
    ): SignInFacebookUseCase

    @Binds
    @Singleton
    abstract fun provideLinkGitHubUseCase(
        linkGitHubUseCaseImpl: LinkGitHubUseCaseImpl
    ): LinkGitHubUseCase

    @Binds
    @Singleton
    abstract fun provideSignInGitHubUseCase(
        signInGitHubUseCaseImpl: SignInGitHubUseCaseImpl
    ): SignInGitHubUseCase

    @Binds
    @Singleton
    abstract fun provideClearCredentialStateUseCase(
        clearCredentialStateUseCaseImpl: ClearCredentialStateUseCaseImpl
    ): ClearCredentialStateUseCase

    @Binds
    @Singleton
    abstract fun provideLinkGoogleUseCase(
        linkGoogleUseCaseImpl: LinkGoogleUseCaseImpl
    ): LinkGoogleUseCase

    @Binds
    @Singleton
    abstract fun provideSignInGoogleCredentialManagerUseCase(
        signInGoogleCredentialManagerUseCaseImpl: SignInGoogleCredentialManagerUseCaseImpl
    ): SignInGoogleCredentialManagerUseCase

    @Binds
    @Singleton
    abstract fun provideSignInGoogleUseCase(
        signInGoogleUseCaseImpl: SignInGoogleUseCaseImpl
    ): SignInGoogleUseCase

    @Binds
    @Singleton
    abstract fun provideLinkMicrosoftUseCase(
        linkMicrosoftUseCaseImpl: LinkMicrosoftUseCaseImpl
    ): LinkMicrosoftUseCase

    @Binds
    @Singleton
    abstract fun provideSignInMicrosoftUseCase(
        signInMicrosoftUseCaseImpl: SignInMicrosoftUseCaseImpl
    ): SignInMicrosoftUseCase

    @Binds
    @Singleton
    abstract fun provideLinkTwitterUseCase(
        linkTwitterUseCaseImpl: LinkTwitterUseCaseImpl
    ): LinkTwitterUseCase

    @Binds
    @Singleton
    abstract fun provideSignInTwitterUseCase(
        linkSignInTwitterUseCaseImpl: SignInTwitterUseCaseImpl
    ): SignInTwitterUseCase

    @Binds
    @Singleton
    abstract fun provideLinkYahooUseCase(
        linkYahooUseCaseImpl: LinkYahooUseCaseImpl
    ): LinkYahooUseCase

    @Binds
    @Singleton
    abstract fun provideSignInYahooUseCase(
        signInYahooUseCaseImpl: SignInYahooUseCaseImpl
    ): SignInYahooUseCase

    @Binds
    @Singleton
    abstract fun provideSendVerificationEmailUseCase(
        sendVerificationEmailUseCaseImpl: SendVerificationEmailUseCaseImpl
    ): SendVerificationEmailUseCase

    @Binds
    @Singleton
    abstract fun provideIsEmailVerifiedUseCase(
        isEmailVerifiedUseCaseImpl: IsEmailVerifiedUseCaseImpl
    ): IsEmailVerifiedUseCase

    companion object {

        @Singleton
        @Provides
        fun provideFirebaseAuth() = FirebaseAuth.getInstance()

        @Provides
        @Singleton
        fun provideUserMapper(): UserMapper = UserMapper()

    }


}