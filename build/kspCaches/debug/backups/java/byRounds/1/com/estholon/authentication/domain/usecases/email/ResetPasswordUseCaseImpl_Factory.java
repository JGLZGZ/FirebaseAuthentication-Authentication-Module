package com.estholon.authentication.domain.usecases.email;

import com.estholon.authentication.domain.repositories.AuthenticationRepository;
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
public final class ResetPasswordUseCaseImpl_Factory implements Factory<ResetPasswordUseCaseImpl> {
  private final Provider<AuthenticationRepository> authenticationRepositoryProvider;

  private ResetPasswordUseCaseImpl_Factory(
      Provider<AuthenticationRepository> authenticationRepositoryProvider) {
    this.authenticationRepositoryProvider = authenticationRepositoryProvider;
  }

  @Override
  public ResetPasswordUseCaseImpl get() {
    return newInstance(authenticationRepositoryProvider.get());
  }

  public static ResetPasswordUseCaseImpl_Factory create(
      Provider<AuthenticationRepository> authenticationRepositoryProvider) {
    return new ResetPasswordUseCaseImpl_Factory(authenticationRepositoryProvider);
  }

  public static ResetPasswordUseCaseImpl newInstance(
      AuthenticationRepository authenticationRepository) {
    return new ResetPasswordUseCaseImpl(authenticationRepository);
  }
}
