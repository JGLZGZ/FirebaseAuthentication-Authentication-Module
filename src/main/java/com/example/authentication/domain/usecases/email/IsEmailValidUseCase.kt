package com.example.authentication.domain.usecases.email

interface IsEmailValidUseCase {

    operator fun invoke( email : String ) : Result<Unit>

}