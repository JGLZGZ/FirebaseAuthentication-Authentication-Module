package com.example.authentication.data.repositories;

import com.example.authentication.data.datasources.anonymously.AnonymouslyAuthenticationDataSource;
import com.example.authentication.data.datasources.common.CommonAuthenticationDataSource;
import com.example.authentication.data.datasources.email.EmailAuthenticationDataSource;
import com.example.authentication.data.datasources.facebook.FacebookAuthenticationDataSource;
import com.example.authentication.data.datasources.github.GitHubAuthenticationDataSource;
import com.example.authentication.data.datasources.google.GoogleAuthenticationDataSource;
import com.example.authentication.data.datasources.microsoft.MicrosoftAuthenticationDataSource;
import com.example.authentication.data.datasources.multifactor.MultifactorAuthenticationDataSource;
import com.example.authentication.data.datasources.phone.PhoneAuthenticationDataSource;
import com.example.authentication.data.datasources.twitter.TwitterAuthenticationDataSource;
import com.example.authentication.data.datasources.yahoo.YahooAuthenticationDataSource;
import com.example.authentication.data.mappers.UserMapper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AuthenticationRepositoryImpl_Factory implements Factory<AuthenticationRepositoryImpl> {
  private final Provider<CommonAuthenticationDataSource> commonAuthenticationDataSourceProvider;

  private final Provider<AnonymouslyAuthenticationDataSource> anonymouslyAuthenticationDataSourceProvider;

  private final Provider<EmailAuthenticationDataSource> emailAuthenticationDataSourceProvider;

  private final Provider<GoogleAuthenticationDataSource> googleAuthenticationDataSourceProvider;

  private final Provider<FacebookAuthenticationDataSource> facebookAuthenticationDataSourceProvider;

  private final Provider<GitHubAuthenticationDataSource> gitHubAuthenticationDataSourceProvider;

  private final Provider<MicrosoftAuthenticationDataSource> microsoftAuthenticationDataSourceProvider;

  private final Provider<PhoneAuthenticationDataSource> phoneAuthenticationDataSourceProvider;

  private final Provider<TwitterAuthenticationDataSource> twitterAuthenticationDataSourceProvider;

  private final Provider<YahooAuthenticationDataSource> yahooAuthenticationDataSourceProvider;

  private final Provider<MultifactorAuthenticationDataSource> multifactorAuthenticationDataSourceProvider;

  private final Provider<UserMapper> userMapperProvider;

  private AuthenticationRepositoryImpl_Factory(
      Provider<CommonAuthenticationDataSource> commonAuthenticationDataSourceProvider,
      Provider<AnonymouslyAuthenticationDataSource> anonymouslyAuthenticationDataSourceProvider,
      Provider<EmailAuthenticationDataSource> emailAuthenticationDataSourceProvider,
      Provider<GoogleAuthenticationDataSource> googleAuthenticationDataSourceProvider,
      Provider<FacebookAuthenticationDataSource> facebookAuthenticationDataSourceProvider,
      Provider<GitHubAuthenticationDataSource> gitHubAuthenticationDataSourceProvider,
      Provider<MicrosoftAuthenticationDataSource> microsoftAuthenticationDataSourceProvider,
      Provider<PhoneAuthenticationDataSource> phoneAuthenticationDataSourceProvider,
      Provider<TwitterAuthenticationDataSource> twitterAuthenticationDataSourceProvider,
      Provider<YahooAuthenticationDataSource> yahooAuthenticationDataSourceProvider,
      Provider<MultifactorAuthenticationDataSource> multifactorAuthenticationDataSourceProvider,
      Provider<UserMapper> userMapperProvider) {
    this.commonAuthenticationDataSourceProvider = commonAuthenticationDataSourceProvider;
    this.anonymouslyAuthenticationDataSourceProvider = anonymouslyAuthenticationDataSourceProvider;
    this.emailAuthenticationDataSourceProvider = emailAuthenticationDataSourceProvider;
    this.googleAuthenticationDataSourceProvider = googleAuthenticationDataSourceProvider;
    this.facebookAuthenticationDataSourceProvider = facebookAuthenticationDataSourceProvider;
    this.gitHubAuthenticationDataSourceProvider = gitHubAuthenticationDataSourceProvider;
    this.microsoftAuthenticationDataSourceProvider = microsoftAuthenticationDataSourceProvider;
    this.phoneAuthenticationDataSourceProvider = phoneAuthenticationDataSourceProvider;
    this.twitterAuthenticationDataSourceProvider = twitterAuthenticationDataSourceProvider;
    this.yahooAuthenticationDataSourceProvider = yahooAuthenticationDataSourceProvider;
    this.multifactorAuthenticationDataSourceProvider = multifactorAuthenticationDataSourceProvider;
    this.userMapperProvider = userMapperProvider;
  }

  @Override
  public AuthenticationRepositoryImpl get() {
    return newInstance(commonAuthenticationDataSourceProvider.get(), anonymouslyAuthenticationDataSourceProvider.get(), emailAuthenticationDataSourceProvider.get(), googleAuthenticationDataSourceProvider.get(), facebookAuthenticationDataSourceProvider.get(), gitHubAuthenticationDataSourceProvider.get(), microsoftAuthenticationDataSourceProvider.get(), phoneAuthenticationDataSourceProvider.get(), twitterAuthenticationDataSourceProvider.get(), yahooAuthenticationDataSourceProvider.get(), multifactorAuthenticationDataSourceProvider.get(), userMapperProvider.get());
  }

  public static AuthenticationRepositoryImpl_Factory create(
      Provider<CommonAuthenticationDataSource> commonAuthenticationDataSourceProvider,
      Provider<AnonymouslyAuthenticationDataSource> anonymouslyAuthenticationDataSourceProvider,
      Provider<EmailAuthenticationDataSource> emailAuthenticationDataSourceProvider,
      Provider<GoogleAuthenticationDataSource> googleAuthenticationDataSourceProvider,
      Provider<FacebookAuthenticationDataSource> facebookAuthenticationDataSourceProvider,
      Provider<GitHubAuthenticationDataSource> gitHubAuthenticationDataSourceProvider,
      Provider<MicrosoftAuthenticationDataSource> microsoftAuthenticationDataSourceProvider,
      Provider<PhoneAuthenticationDataSource> phoneAuthenticationDataSourceProvider,
      Provider<TwitterAuthenticationDataSource> twitterAuthenticationDataSourceProvider,
      Provider<YahooAuthenticationDataSource> yahooAuthenticationDataSourceProvider,
      Provider<MultifactorAuthenticationDataSource> multifactorAuthenticationDataSourceProvider,
      Provider<UserMapper> userMapperProvider) {
    return new AuthenticationRepositoryImpl_Factory(commonAuthenticationDataSourceProvider, anonymouslyAuthenticationDataSourceProvider, emailAuthenticationDataSourceProvider, googleAuthenticationDataSourceProvider, facebookAuthenticationDataSourceProvider, gitHubAuthenticationDataSourceProvider, microsoftAuthenticationDataSourceProvider, phoneAuthenticationDataSourceProvider, twitterAuthenticationDataSourceProvider, yahooAuthenticationDataSourceProvider, multifactorAuthenticationDataSourceProvider, userMapperProvider);
  }

  public static AuthenticationRepositoryImpl newInstance(
      CommonAuthenticationDataSource commonAuthenticationDataSource,
      AnonymouslyAuthenticationDataSource anonymouslyAuthenticationDataSource,
      EmailAuthenticationDataSource emailAuthenticationDataSource,
      GoogleAuthenticationDataSource googleAuthenticationDataSource,
      FacebookAuthenticationDataSource facebookAuthenticationDataSource,
      GitHubAuthenticationDataSource gitHubAuthenticationDataSource,
      MicrosoftAuthenticationDataSource microsoftAuthenticationDataSource,
      PhoneAuthenticationDataSource phoneAuthenticationDataSource,
      TwitterAuthenticationDataSource twitterAuthenticationDataSource,
      YahooAuthenticationDataSource yahooAuthenticationDataSource,
      MultifactorAuthenticationDataSource multifactorAuthenticationDataSource,
      UserMapper userMapper) {
    return new AuthenticationRepositoryImpl(commonAuthenticationDataSource, anonymouslyAuthenticationDataSource, emailAuthenticationDataSource, googleAuthenticationDataSource, facebookAuthenticationDataSource, gitHubAuthenticationDataSource, microsoftAuthenticationDataSource, phoneAuthenticationDataSource, twitterAuthenticationDataSource, yahooAuthenticationDataSource, multifactorAuthenticationDataSource, userMapper);
  }
}
