package com.example.authentication.domain.usecases.multifactor

interface SendVerificationEmailUseCase {

    suspend operator fun invoke(): Result<Unit>

}