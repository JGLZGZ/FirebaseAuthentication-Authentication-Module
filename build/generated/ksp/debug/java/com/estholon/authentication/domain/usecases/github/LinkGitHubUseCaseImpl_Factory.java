package com.estholon.authentication.domain.usecases.github;

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
public final class LinkGitHubUseCaseImpl_Factory implements Factory<LinkGitHubUseCaseImpl> {
  private final Provider<AuthenticationRepository> authenticationRepositoryProvider;

  private LinkGitHubUseCaseImpl_Factory(
      Provider<AuthenticationRepository> authenticationRepositoryProvider) {
    this.authenticationRepositoryProvider = authenticationRepositoryProvider;
  }

  @Override
  public LinkGitHubUseCaseImpl get() {
    return newInstance(authenticationRepositoryProvider.get());
  }

  public static LinkGitHubUseCaseImpl_Factory create(
      Provider<AuthenticationRepository> authenticationRepositoryProvider) {
    return new LinkGitHubUseCaseImpl_Factory(authenticationRepositoryProvider);
  }

  public static LinkGitHubUseCaseImpl newInstance(
      AuthenticationRepository authenticationRepository) {
    return new LinkGitHubUseCaseImpl(authenticationRepository);
  }
}
