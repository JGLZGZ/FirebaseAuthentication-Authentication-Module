package com.example.authentication.domain.usecases.multifactor

import com.estholon.analytics.domain.usecases.SendEventUseCase
import com.example.authentication.domain.repositories.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StartEnrollPhoneUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val sendEventUseCase: SendEventUseCase
) : StartEnrollPhoneUseCase {

    override suspend operator fun invoke(phoneNumber: String) : Flow<Result<String>> = flow {
        val session = authenticationRepository.getMultifactorSession()
        val result = authenticationRepository.enrollMfaSendSms(session, phoneNumber)
        emit(result)
    }

}