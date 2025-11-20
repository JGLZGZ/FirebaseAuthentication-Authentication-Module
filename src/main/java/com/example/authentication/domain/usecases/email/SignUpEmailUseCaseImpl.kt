package com.example.authentication.domain.usecases.email

import com.estholon.analytics.domain.models.AnalyticsModel
import com.estholon.analytics.domain.usecases.SendEventUseCase
import com.example.authentication.domain.repositories.AuthenticationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignUpEmailUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val sendEventUseCase: SendEventUseCase
) : SignUpEmailUseCase {

    override suspend operator fun invoke(email: String, password: String) : Result<Unit> {


        return try {
            withContext(Dispatchers.IO) {

                val result = authenticationRepository.signUpEmail(email, password)
                result.fold(
                    onSuccess = {
                        // Send verification email
                        authenticationRepository.sendEmailVerification()
                        // Send statistics to analytics
                        val analyticsModel = AnalyticsModel(
                            title = "Sign Up",
                            analyticsString = listOf(Pair("Email", "Successful Sign Up"))
                        )
                        sendEventUseCase(analyticsModel)
                        Result.success(Unit)
                    },
                    onFailure = { exception ->
                        val analyticsModel = AnalyticsModel(
                            title = "Sign Up",
                            analyticsString = listOf(Pair("Email", "Failed Sign Up"))
                        )
                        sendEventUseCase(analyticsModel)
                        Result.failure(exception)
                    }
                )

            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}