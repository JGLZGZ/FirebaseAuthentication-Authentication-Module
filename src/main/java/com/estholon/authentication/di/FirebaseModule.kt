package com.estholon.authentication.di

import com.estholon.authentication.data.datasources.anonymously.AnonymouslyAuthenticationDataSource
import com.estholon.authentication.data.datasources.anonymously.AnonymouslyFirebaseAuthenticationDataSource
import com.estholon.authentication.data.datasources.common.CommonAuthenticationDataSource
import com.estholon.authentication.data.datasources.common.CommonFirebaseAuthenticationDataSource
import com.estholon.authentication.data.datasources.email.EmailAuthenticationDataSource
import com.estholon.authentication.data.datasources.email.EmailFirebaseAuthenticationDataSource
import com.estholon.authentication.data.datasources.facebook.FacebookAuthenticationDataSource
import com.estholon.authentication.data.datasources.facebook.FacebookFirebaseAuthenticationDataSource
import com.estholon.authentication.data.datasources.github.GitHubAuthenticationDataSource
import com.estholon.authentication.data.datasources.github.GitHubFirebaseAuthenticationDataSource
import com.estholon.authentication.data.datasources.google.GoogleAuthenticationDataSource
import com.estholon.authentication.data.datasources.google.GoogleFirebaseAuthenticationDataSource
import com.estholon.authentication.data.datasources.microsoft.MicrosoftAuthenticationDataSource
import com.estholon.authentication.data.datasources.microsoft.MicrosoftFirebaseAuthenticationDataSource
import com.estholon.authentication.data.datasources.multifactor.MultifactorAuthenticationDataSource
import com.estholon.authentication.data.datasources.multifactor.MultifactorFirebaseAuthenticationDataSource
import com.estholon.authentication.data.datasources.phone.PhoneAuthenticationDataSource
import com.estholon.authentication.data.datasources.phone.PhoneFirebaseAuthenticationDataSource
import com.estholon.authentication.data.datasources.twitter.TwitterAuthenticationDataSource
import com.estholon.authentication.data.datasources.twitter.TwitterFirebaseAuthenticationDataSource
import com.estholon.authentication.data.datasources.yahoo.YahooAuthenticationDataSource
import com.estholon.authentication.data.datasources.yahoo.YahooFirebaseAuthenticationDataSource
import com.estholon.authentication.data.mappers.UserMapper
import com.estholon.authentication.data.repositories.AuthenticationRepositoryImpl
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import com.estholon.authentication.domain.usecases.anonymously.SignInAnonymouslyUseCase
import com.estholon.authentication.domain.usecases.anonymously.SignInAnonymouslyUseCaseImpl
import com.estholon.authentication.domain.usecases.common.IsEmailVerifiedUseCase
import com.estholon.authentication.domain.usecases.common.IsEmailVerifiedUseCaseImpl
import com.estholon.authentication.domain.usecases.common.IsUserLoggedUseCase
import com.estholon.authentication.domain.usecases.common.IsUserLoggedUseCaseImpl
import com.estholon.authentication.domain.usecases.common.SignOutUseCase
import com.estholon.authentication.domain.usecases.common.SignOutUseCaseImpl
import com.estholon.authentication.domain.usecases.email.IsEmailValidUseCase
import com.estholon.authentication.domain.usecases.email.IsEmailValidUseCaseImpl
import com.estholon.authentication.domain.usecases.email.IsPasswordValidUseCase
import com.estholon.authentication.domain.usecases.email.IsPasswordValidUseCaseImpl
import com.estholon.authentication.domain.usecases.email.LinkEmailUseCase
import com.estholon.authentication.domain.usecases.email.LinkEmailUseCaseImpl
import com.estholon.authentication.domain.usecases.email.ResetPasswordUseCase
import com.estholon.authentication.domain.usecases.email.ResetPasswordUseCaseImpl
import com.estholon.authentication.domain.usecases.email.SignInEmailUseCase
import com.estholon.authentication.domain.usecases.email.SignInEmailUseCaseImpl
import com.estholon.authentication.domain.usecases.email.SignUpEmailUseCase
import com.estholon.authentication.domain.usecases.email.SignUpEmailUseCaseImpl
import com.estholon.authentication.domain.usecases.facebook.LinkFacebookUseCase
import com.estholon.authentication.domain.usecases.facebook.LinkFacebookUseCaseImpl
import com.estholon.authentication.domain.usecases.facebook.SignInFacebookUseCase
import com.estholon.authentication.domain.usecases.facebook.SignInFacebookUseCaseImpl
import com.estholon.authentication.domain.usecases.github.LinkGitHubUseCase
import com.estholon.authentication.domain.usecases.github.LinkGitHubUseCaseImpl
import com.estholon.authentication.domain.usecases.github.SignInGitHubUseCase
import com.estholon.authentication.domain.usecases.github.SignInGitHubUseCaseImpl
import com.estholon.authentication.domain.usecases.google.ClearCredentialStateUseCase
import com.estholon.authentication.domain.usecases.google.ClearCredentialStateUseCaseImpl
import com.estholon.authentication.domain.usecases.google.LinkGoogleUseCase
import com.estholon.authentication.domain.usecases.google.LinkGoogleUseCaseImpl
import com.estholon.authentication.domain.usecases.google.SignInGoogleCredentialManagerUseCase
import com.estholon.authentication.domain.usecases.google.SignInGoogleCredentialManagerUseCaseImpl
import com.estholon.authentication.domain.usecases.google.SignInGoogleUseCase
import com.estholon.authentication.domain.usecases.google.SignInGoogleUseCaseImpl
import com.estholon.authentication.domain.usecases.microsoft.LinkMicrosoftUseCase
import com.estholon.authentication.domain.usecases.microsoft.LinkMicrosoftUseCaseImpl
import com.estholon.authentication.domain.usecases.microsoft.SignInMicrosoftUseCase
import com.estholon.authentication.domain.usecases.microsoft.SignInMicrosoftUseCaseImpl
import com.estholon.authentication.domain.usecases.multifactor.SendVerificationEmailUseCase
import com.estholon.authentication.domain.usecases.multifactor.SendVerificationEmailUseCaseImpl
import com.estholon.authentication.domain.usecases.multifactor.StartEnrollPhoneUseCase
import com.estholon.authentication.domain.usecases.multifactor.StartEnrollPhoneUseCaseImpl
import com.estholon.authentication.domain.usecases.twitter.LinkTwitterUseCase
import com.estholon.authentication.domain.usecases.twitter.LinkTwitterUseCaseImpl
import com.estholon.authentication.domain.usecases.twitter.SignInTwitterUseCase
import com.estholon.authentication.domain.usecases.twitter.SignInTwitterUseCaseImpl
import com.estholon.authentication.domain.usecases.yahoo.LinkYahooUseCase
import com.estholon.authentication.domain.usecases.yahoo.LinkYahooUseCaseImpl
import com.estholon.authentication.domain.usecases.yahoo.SignInYahooUseCase
import com.estholon.authentication.domain.usecases.yahoo.SignInYahooUseCaseImpl
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

    @Binds
    @Singleton
    abstract fun provideIsUserLoggedUseCase(
        isUserLoggedUseCaseImpl: IsUserLoggedUseCaseImpl
    ): IsUserLoggedUseCase

    companion object {

        @Singleton
        @Provides
        fun provideFirebaseAuth() = FirebaseAuth.getInstance()

        @Provides
        @Singleton
        fun provideUserMapper(): UserMapper = UserMapper()

    }


}