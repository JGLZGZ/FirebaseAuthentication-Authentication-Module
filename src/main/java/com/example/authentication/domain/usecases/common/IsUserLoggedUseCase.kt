package com.example.authentication.domain.usecases.common

import kotlinx.coroutines.flow.Flow

interface IsUserLoggedUseCase {
    suspend operator fun invoke(): Flow<Result<Boolean>>
}