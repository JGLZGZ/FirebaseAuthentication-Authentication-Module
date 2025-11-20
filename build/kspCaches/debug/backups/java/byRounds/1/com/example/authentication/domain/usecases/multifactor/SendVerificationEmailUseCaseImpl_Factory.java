package com.example.authentication.domain.usecases.multifactor;

import com.example.authentication.domain.repositories.AuthenticationRepository;
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
public final class SendVerificationEmailUseCaseImpl_Factory implements Factory<SendVerificationEmailUseCaseImpl> {
  private final Provider<AuthenticationRepository> authenticationRepositoryProvider;

  private SendVerificationEmailUseCaseImpl_Factory(
      Provider<AuthenticationRepository> authenticationRepositoryProvider) {
    this.authenticationRepositoryProvider = authenticationRepositoryProvider;
  }

  @Override
  public SendVerificationEmailUseCaseImpl get() {
    return newInstance(authenticationRepositoryProvider.get());
  }

  public static SendVerificationEmailUseCaseImpl_Factory create(
      Provider<AuthenticationRepository> authenticationRepositoryProvider) {
    return new SendVerificationEmailUseCaseImpl_Factory(authenticationRepositoryProvider);
  }

  public static SendVerificationEmailUseCaseImpl newInstance(
      AuthenticationRepository authenticationRepository) {
    return new SendVerificationEmailUseCaseImpl(authenticationRepository);
  }
}
