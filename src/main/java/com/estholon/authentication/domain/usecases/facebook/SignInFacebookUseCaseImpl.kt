package com.estholon.authentication.domain.usecases.facebook

import com.estholon.analytics.domain.models.AnalyticsModel
import com.estholon.analytics.domain.usecases.SendEventUseCase
import com.estholon.authentication.domain.repositories.AuthenticationRepository
import com.facebook.AccessToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInFacebookUseCaseImpl @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val sendEventUseCase: SendEventUseCase
)  : SignInFacebookUseCase {

    override suspend operator fun invoke( accessToken: AccessToken) : Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val result = authenticationRepository.signInFacebook(accessToken)
                result.fold(
                    onSuccess = {
                        val analyticsModel = AnalyticsModel(
                            title = "Sign In",
                            analyticsString = listOf(Pair("Facebook", "Failed Sign In / Up"))
                        )
                        sendEventUseCase(analyticsModel)
                        Result.success(Unit)
                    },
                    onFailure = { exception ->
                        val analyticsModel = AnalyticsModel(
                            title = "Sign In",
                            analyticsString = listOf(Pair("Facebook", "Failed Sign In / Up"))
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