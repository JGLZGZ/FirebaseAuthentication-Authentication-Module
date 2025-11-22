package com.estholon.authentication.domain.usecases.email

interface IsPasswordValidUseCase {
    operator fun invoke(password: String) : Result<Unit>
}