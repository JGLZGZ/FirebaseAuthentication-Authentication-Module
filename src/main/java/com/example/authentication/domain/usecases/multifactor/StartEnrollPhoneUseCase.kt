package com.example.authentication.domain.usecases.multifactor

import kotlinx.coroutines.flow.Flow

interface StartEnrollPhoneUseCase {
    suspend operator fun invoke(phoneNumber: String): Flow<Result<String>>
}