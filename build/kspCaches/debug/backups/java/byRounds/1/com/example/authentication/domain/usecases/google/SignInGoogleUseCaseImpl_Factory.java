package com.example.authentication.domain.usecases.google;

import com.estholon.analytics.domain.usecases.SendEventUseCase;
import com.example.authentication.domain.repositories.AuthenticationRepository;
import com.example.authentication.domain.usecases.multifactor.SendVerificationEmailUseCase;
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
public final class SignInGoogleUseCaseImpl_Factory implements Factory<SignInGoogleUseCaseImpl> {
  private final Provider<AuthenticationRepository> authenticationRepositoryProvider;

  private final Provider<SendVerificationEmailUseCase> sendVerificationEmailUseCaseProvider;

  private final Provider<SendEventUseCase> sendEventUseCaseProvider;

  private SignInGoogleUseCaseImpl_Factory(
      Provider<AuthenticationRepository> authenticationRepositoryProvider,
      Provider<SendVerificationEmailUseCase> sendVerificationEmailUseCaseProvider,
      Provider<SendEventUseCase> sendEventUseCaseProvider) {
    this.authenticationRepositoryProvider = authenticationRepositoryProvider;
    this.sendVerificationEmailUseCaseProvider = sendVerificationEmailUseCaseProvider;
    this.sendEventUseCaseProvider = sendEventUseCaseProvider;
  }

  @Override
  public SignInGoogleUseCaseImpl get() {
    return newInstance(authenticationRepositoryProvider.get(), sendVerificationEmailUseCaseProvider.get(), sendEventUseCaseProvider.get());
  }

  public static SignInGoogleUseCaseImpl_Factory create(
      Provider<AuthenticationRepository> authenticationRepositoryProvider,
      Provider<SendVerificationEmailUseCase> sendVerificationEmailUseCaseProvider,
      Provider<SendEventUseCase> sendEventUseCaseProvider) {
    return new SignInGoogleUseCaseImpl_Factory(authenticationRepositoryProvider, sendVerificationEmailUseCaseProvider, sendEventUseCaseProvider);
  }

  public static SignInGoogleUseCaseImpl newInstance(
      AuthenticationRepository authenticationRepository,
      SendVerificationEmailUseCase sendVerificationEmailUseCase,
      SendEventUseCase sendEventUseCase) {
    return new SignInGoogleUseCaseImpl(authenticationRepository, sendVerificationEmailUseCase, sendEventUseCase);
  }
}
