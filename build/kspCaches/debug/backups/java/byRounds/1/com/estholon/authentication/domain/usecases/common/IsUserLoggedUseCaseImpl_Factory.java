package com.estholon.authentication.domain.usecases.common;

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
public final class IsUserLoggedUseCaseImpl_Factory implements Factory<IsUserLoggedUseCaseImpl> {
  private final Provider<AuthenticationRepository> authenticationRepositoryProvider;

  private IsUserLoggedUseCaseImpl_Factory(
      Provider<AuthenticationRepository> authenticationRepositoryProvider) {
    this.authenticationRepositoryProvider = authenticationRepositoryProvider;
  }

  @Override
  public IsUserLoggedUseCaseImpl get() {
    return newInstance(authenticationRepositoryProvider.get());
  }

  public static IsUserLoggedUseCaseImpl_Factory create(
      Provider<AuthenticationRepository> authenticationRepositoryProvider) {
    return new IsUserLoggedUseCaseImpl_Factory(authenticationRepositoryProvider);
  }

  public static IsUserLoggedUseCaseImpl newInstance(
      AuthenticationRepository authenticationRepository) {
    return new IsUserLoggedUseCaseImpl(authenticationRepository);
  }
}
