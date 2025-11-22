package com.estholon.authentication.domain.usecases.twitter

import android.app.Activity
import com.estholon.analytics.domain.models.AnalyticsModel
import com.estholon.analytics.domain.usecases.SendEventUseCase
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInTwitterUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val sendEventUseCase: SendEventUseCase
) : SignInTwitterUseCase {

    override suspend operator fun invoke( activity : Activity) : Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val result = authenticationRepository.signInTwitter(
                    activity = activity
                )
                result.fold(
                    onSuccess = {
                        val analyticsModel = AnalyticsModel(
                            title = "Sign In",
                            analyticsString = listOf(Pair("Twitter", "Failed Sign In / Up"))
                        )
                        sendEventUseCase(analyticsModel)
                        Result.success(Unit)
                    },
                    onFailure = { exception ->
                        val analyticsModel = AnalyticsModel(
                            title = "Sign In",
                            analyticsString = listOf(Pair("Twitter", "Failed Sign In / Up"))
                        )
                        sendEventUseCase(analyticsModel)
                        Result.failure(Exception(exception.message))
                    }
                )
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}