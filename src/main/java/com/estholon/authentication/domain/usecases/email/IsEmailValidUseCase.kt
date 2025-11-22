package com.estholon.authentication.domain.usecases.email

interface IsEmailValidUseCase {

    operator fun invoke( email : String ) : Result<Unit>

}