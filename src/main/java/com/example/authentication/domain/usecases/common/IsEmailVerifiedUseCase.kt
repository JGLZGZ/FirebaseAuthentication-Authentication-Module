package com.example.authentication.domain.usecases.common

interface IsEmailVerifiedUseCase {

    suspend operator fun invoke() : Result<Boolean>

}