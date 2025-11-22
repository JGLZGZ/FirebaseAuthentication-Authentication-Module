package com.estholon.authentication.domain.usecases.email

import com.estholon.authentication.domain.models.UserModel

interface LinkEmailUseCase {
    suspend operator fun invoke(email: String, password: String): Result<UserModel?>
}