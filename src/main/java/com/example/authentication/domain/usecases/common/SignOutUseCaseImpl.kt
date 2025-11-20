package com.example.authentication.domain.usecases.common

import com.example.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class SignOutUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : SignOutUseCase {

    override suspend operator fun invoke(){
        authenticationRepository.signOut()
    }

}