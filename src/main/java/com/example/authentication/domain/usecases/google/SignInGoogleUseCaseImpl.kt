package com.example.authentication.domain.usecases.google

import android.app.Activity
import com.estholon.analytics.domain.models.AnalyticsModel
import com.estholon.analytics.domain.usecases.SendEventUseCase
import com.example.authentication.domain.repositories.AuthenticationRepository
import com.example.authentication.domain.usecases.multifactor.SendVerificationEmailUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInGoogleUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val sendVerificationEmailUseCase: SendVerificationEmailUseCase,
    private val sendEventUseCase: SendEventUseCase
) : SignInGoogleUseCase {

    override suspend operator fun invoke(activity: Activity) : Result<Unit> {
        return try {
            withContext(Dispatchers.IO){
                val result = authenticationRepository.signInGoogle(activity)

                result.fold(
                    onSuccess = {
                        // Send statistics to analytics
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