package com.estholon.authentication.domain.usecases.multifactor

interface SendVerificationEmailUseCase {

    suspend operator fun invoke(): Result<Unit>

}