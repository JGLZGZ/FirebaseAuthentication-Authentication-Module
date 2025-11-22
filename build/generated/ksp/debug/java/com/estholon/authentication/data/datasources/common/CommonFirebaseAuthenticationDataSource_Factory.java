package com.estholon.authentication.data.datasources.common;

import com.estholon.authentication.data.datasources.google.GoogleAuthenticationDataSource;
import com.google.firebase.auth.FirebaseAuth;
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
public final class CommonFirebaseAuthenticationDataSource_Factory implements Factory<CommonFirebaseAuthenticationDataSource> {
  private final Provider<FirebaseAuth> firebaseAuthProvider;

  private final Provider<GoogleAuthenticationDataSource> googleAuthenticationDataSourceProvider;

  private CommonFirebaseAuthenticationDataSource_Factory(
      Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<GoogleAuthenticationDataSource> googleAuthenticationDataSourceProvider) {
    this.firebaseAuthProvider = firebaseAuthProvider;
    this.googleAuthenticationDataSourceProvider = googleAuthenticationDataSourceProvider;
  }

  @Override
  public CommonFirebaseAuthenticationDataSource get() {
    return newInstance(firebaseAuthProvider.get(), googleAuthenticationDataSourceProvider.get());
  }

  public static CommonFirebaseAuthenticationDataSource_Factory create(
      Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<GoogleAuthenticationDataSource> googleAuthenticationDataSourceProvider) {
    return new CommonFirebaseAuthenticationDataSource_Factory(firebaseAuthProvider, googleAuthenticationDataSourceProvider);
  }

  public static CommonFirebaseAuthenticationDataSource newInstance(FirebaseAuth firebaseAuth,
      GoogleAuthenticationDataSource googleAuthenticationDataSource) {
    return new CommonFirebaseAuthenticationDataSource(firebaseAuth, googleAuthenticationDataSource);
  }
}
