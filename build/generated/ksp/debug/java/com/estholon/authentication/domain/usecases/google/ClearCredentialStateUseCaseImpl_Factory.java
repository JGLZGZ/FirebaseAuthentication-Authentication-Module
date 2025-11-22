package com.estholon.authentication.domain.usecases.google;

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
public final class ClearCredentialStateUseCaseImpl_Factory implements Factory<ClearCredentialStateUseCaseImpl> {
  private final Provider<AuthenticationRepository> authenticationRepositoryProvider;

  private ClearCredentialStateUseCaseImpl_Factory(
      Provider<AuthenticationRepository> authenticationRepositoryProvider) {
    this.authenticationRepositoryProvider = authenticationRepositoryProvider;
  }

  @Override
  public ClearCredentialStateUseCaseImpl get() {
    return newInstance(authenticationRepositoryProvider.get());
  }

  public static ClearCredentialStateUseCaseImpl_Factory create(
      Provider<AuthenticationRepository> authenticationRepositoryProvider) {
    return new ClearCredentialStateUseCaseImpl_Factory(authenticationRepositoryProvider);
  }

  public static ClearCredentialStateUseCaseImpl newInstance(
      AuthenticationRepository authenticationRepository) {
    return new ClearCredentialStateUseCaseImpl(authenticationRepository);
  }
}
