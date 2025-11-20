package com.example.authentication.domain.usecases.email

import com.example.authentication.domain.models.UserModel

interface LinkEmailUseCase {
    suspend operator fun invoke(email: String, password: String): Result<UserModel?>
}