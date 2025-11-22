package com.estholon.authentication.domain.usecases.common

interface IsEmailVerifiedUseCase {

    suspend operator fun invoke() : Result<Boolean>

}