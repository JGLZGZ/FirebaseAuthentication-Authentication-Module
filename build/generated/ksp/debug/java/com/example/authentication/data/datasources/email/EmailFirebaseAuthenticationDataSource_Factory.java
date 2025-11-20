package com.example.authentication.data.datasources.email;

import android.content.Context;
import com.example.authentication.data.mappers.UserMapper;
import com.google.firebase.auth.FirebaseAuth;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class EmailFirebaseAuthenticationDataSource_Factory implements Factory<EmailFirebaseAuthenticationDataSource> {
  private final Provider<FirebaseAuth> firebaseAuthProvider;

  private final Provider<UserMapper> userMapperProvider;

  private final Provider<Context> contextProvider;

  private EmailFirebaseAuthenticationDataSource_Factory(Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<UserMapper> userMapperProvider, Provider<Context> contextProvider) {
    this.firebaseAuthProvider = firebaseAuthProvider;
    this.userMapperProvider = userMapperProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public EmailFirebaseAuthenticationDataSource get() {
    return newInstance(firebaseAuthProvider.get(), userMapperProvider.get(), contextProvider.get());
  }

  public static EmailFirebaseAuthenticationDataSource_Factory create(
      Provider<FirebaseAuth> firebaseAuthProvider, Provider<UserMapper> userMapperProvider,
      Provider<Context> contextProvider) {
    return new EmailFirebaseAuthenticationDataSource_Factory(firebaseAuthProvider, userMapperProvider, contextProvider);
  }

  public static EmailFirebaseAuthenticationDataSource newInstance(FirebaseAuth firebaseAuth,
      UserMapper userMapper, Context context) {
    return new EmailFirebaseAuthenticationDataSource(firebaseAuth, userMapper, context);
  }
}
