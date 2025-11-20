package com.example.authentication.domain.usecases.google

import com.example.authentication.domain.repositories.AuthenticationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClearCredentialStateUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ClearCredentialStateUseCase {

    override suspend operator fun invoke() {
        withContext(Dispatchers.IO) {
            authenticationRepository.clearCredentialState()
        }
    }

}