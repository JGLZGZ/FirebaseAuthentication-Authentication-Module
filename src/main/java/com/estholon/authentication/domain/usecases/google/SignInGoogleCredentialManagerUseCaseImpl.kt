package com.estholon.authentication.domain.usecases.google

import android.app.Activity
import com.estholon.analytics.domain.models.AnalyticsModel
import com.estholon.analytics.domain.usecases.SendEventUseCase
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInGoogleCredentialManagerUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val sendEventUseCase: SendEventUseCase
) : SignInGoogleCredentialManagerUseCase {

    override suspend operator fun invoke(activity: Activity) : Result<Unit> {
        return try {
            withContext(Dispatchers.IO){
                val result = authenticationRepository.signInGoogleCredentialManager(activity)

                result.fold(
                    onSuccess = {
                        val analyticsModel = AnalyticsModel(
                            title = "Sign In",
                            analyticsString = listOf(Pair("Google", "Failed Sign In / Up"))
                        )
                        sendEventUseCase(analyticsModel)
                        Result.success(Unit)
                    },
                    onFailure = { exception ->
                        val analyticsModel = AnalyticsModel(
                            title = "Sign In",
                            analyticsString = listOf(Pair("Google", "Failed Sign In / Up"))
                        )
                        sendEventUseCase(analyticsModel)
                        Result.failure(Exception(exception.message))
                    }
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}