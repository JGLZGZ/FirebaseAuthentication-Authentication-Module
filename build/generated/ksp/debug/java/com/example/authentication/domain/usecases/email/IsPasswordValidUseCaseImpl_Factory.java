package com.example.authentication.domain.usecases.email;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class IsPasswordValidUseCaseImpl_Factory implements Factory<IsPasswordValidUseCaseImpl> {
  @Override
  public IsPasswordValidUseCaseImpl get() {
    return newInstance();
  }

  public static IsPasswordValidUseCaseImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static IsPasswordValidUseCaseImpl newInstance() {
    return new IsPasswordValidUseCaseImpl();
  }

  private static final class InstanceHolder {
    static final IsPasswordValidUseCaseImpl_Factory INSTANCE = new IsPasswordValidUseCaseImpl_Factory();
  }
}
