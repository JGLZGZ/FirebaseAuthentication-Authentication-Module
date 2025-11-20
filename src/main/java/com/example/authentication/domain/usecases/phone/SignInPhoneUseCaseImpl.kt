package com.example.authentication.domain.usecases.phone

import com.estholon.analytics.domain.usecases.SendEventUseCase
import com.example.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class SignInPhoneUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val sendEventUseCase: SendEventUseCase
)  : SignInPhoneUseCase {
}