package com.estholon.authentication.di;

import com.estholon.authentication.data.mappers.UserMapper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class FirebaseModule_Companion_ProvideUserMapperFactory implements Factory<UserMapper> {
  @Override
  public UserMapper get() {
    return provideUserMapper();
  }

  public static FirebaseModule_Companion_ProvideUserMapperFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static UserMapper provideUserMapper() {
    return Preconditions.checkNotNullFromProvides(FirebaseModule.Companion.provideUserMapper());
  }

  private static final class InstanceHolder {
    static final FirebaseModule_Companion_ProvideUserMapperFactory INSTANCE = new FirebaseModule_Companion_ProvideUserMapperFactory();
  }
}
