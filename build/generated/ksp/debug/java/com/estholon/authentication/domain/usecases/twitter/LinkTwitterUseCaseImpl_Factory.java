package com.estholon.authentication.domain.usecases.twitter;

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
public final class LinkTwitterUseCaseImpl_Factory implements Factory<LinkTwitterUseCaseImpl> {
  private final Provider<AuthenticationRepository> authenticationRepositoryProvider;

  private LinkTwitterUseCaseImpl_Factory(
      Provider<AuthenticationRepository> authenticationRepositoryProvider) {
    this.authenticationRepositoryProvider = authenticationRepositoryProvider;
  }

  @Override
  public LinkTwitterUseCaseImpl get() {
    return newInstance(authenticationRepositoryProvider.get());
  }

  public static LinkTwitterUseCaseImpl_Factory create(
      Provider<AuthenticationRepository> authenticationRepositoryProvider) {
    return new LinkTwitterUseCaseImpl_Factory(authenticationRepositoryProvider);
  }

  public static LinkTwitterUseCaseImpl newInstance(
      AuthenticationRepository authenticationRepository) {
    return new LinkTwitterUseCaseImpl(authenticationRepository);
  }
}
