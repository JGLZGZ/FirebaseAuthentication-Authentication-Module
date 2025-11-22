package com.estholon.authentication.domain.usecases.anonymously

import com.estholon.analytics.domain.usecases.SendEventUseCase
import com.estholon.analytics.domain.models.AnalyticsModel
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import javax.inject.Inject

class SignInAnonymouslyUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val sendEventUseCase: SendEventUseCase
) : SignInAnonymouslyUseCase {

    override suspend operator fun invoke() : Result<Unit> {
        return try {
                val result = authenticationRepository.signInAnonymously()

                result.fold(
                    onSuccess = {
                        val analyticsModel = AnalyticsModel(
                            title = "Sign In",
                            analyticsString = listOf(Pair("Anonymously", "Failed Sign In / Up"))
                        )
                        sendEventUseCase(analyticsModel)
                        Result.success(Unit)
                    },
                    onFailure = { exception ->
                        val analyticsModel = AnalyticsModel(
                            title = "Sign In",
                            analyticsString = listOf(Pair("Anonymously", "Failed Sign In / Up"))
                        )
                        sendEventUseCase(analyticsModel)
                        Result.failure(Exception(exception.message))
                    }
                )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}