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
public final class LinkGoogleUseCaseImpl_Factory implements Factory<LinkGoogleUseCaseImpl> {
  private final Provider<AuthenticationRepository> authenticationRepositoryProvider;

  private LinkGoogleUseCaseImpl_Factory(
      Provider<AuthenticationRepository> authenticationRepositoryProvider) {
    this.authenticationRepositoryProvider = authenticationRepositoryProvider;
  }

  @Override
  public LinkGoogleUseCaseImpl get() {
    return newInstance(authenticationRepositoryProvider.get());
  }

  public static LinkGoogleUseCaseImpl_Factory create(
      Provider<AuthenticationRepository> authenticationRepositoryProvider) {
    return new LinkGoogleUseCaseImpl_Factory(authenticationRepositoryProvider);
  }

  public static LinkGoogleUseCaseImpl newInstance(
      AuthenticationRepository authenticationRepository) {
    return new LinkGoogleUseCaseImpl(authenticationRepository);
  }
}
