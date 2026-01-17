package com.estholon.authentication.data.datasources.google

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import com.estholon.authentication.R
import com.estholon.authentication.data.datasources.email.EmailAuthenticationDataSource
import com.estholon.authentication.data.dtos.UserDto
import com.estholon.authentication.data.mappers.toUserDto
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.security.SecureRandom
import javax.inject.Inject
import kotlin.coroutines.resume

class GoogleFirebaseAuthenticationDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val emailAuthenticationDataSource: EmailAuthenticationDataSource,
    @ApplicationContext private val context: Context,
    private val credentialManager: CredentialManager,
    private val credentialOptionsFactory: GoogleCredentialOptionsFactory
) : GoogleAuthenticationDataSource {

    companion object {
        private const val TAG = "FirebaseAuthDS"
    }

    // SIGN IN GOOGLE

    override suspend fun signInGoogleCredentialManager(activity: Activity): Result<UserDto?> {
        return try {
            val nonce = generateNonce()

            val googleIdOption = credentialOptionsFactory.createGoogleIdOption(
                serverClientId = context.getString(R.string.default_web_client_id),
                nonce = nonce,
                filterByAuthorizedAccounts = true,
                autoSelectEnabled = true
            )

            val request = credentialOptionsFactory.createCredentialRequest(googleIdOption)

            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = activity
                )
                handleCredentialResponse(result)
            } catch (e: GetCredentialException) {
                signUpWithCredentialManager(activity, nonce)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en signInWithCredentialManager", e)
            Result.failure(e)
        }
    }

    override suspend fun signInGoogle(activity: Activity): Result<UserDto?> {
        return try {
            val nonce = generateNonce()

            val signInWithGoogleOption = credentialOptionsFactory.createSignInWithGoogleOption(
                serverClientId = context.getString(R.string.default_web_client_id),
                nonce = nonce
            )

            val request = credentialOptionsFactory.createCredentialRequestWithSignIn(signInWithGoogleOption)

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )
            handleCredentialResponse(result)
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Error al iniciar sesión con Google: ${e.message}"))
        }
    }

    override suspend fun handleCredentialResponse(result: GetCredentialResponse) : Result<UserDto?> {
        val credential = result.credential

        return when (credential) {
            is PublicKeyCredential -> {
                Result.failure(Exception("Passkeys no implementadas aún"))
            }

            is PasswordCredential -> {
                emailAuthenticationDataSource.signInEmail(credential.id, credential.password)
            }

            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = credentialOptionsFactory.parseGoogleIdTokenCredential(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        val userInfo = """
                            ID: ${googleIdTokenCredential.id}
                            Name: ${googleIdTokenCredential.displayName}
                            Email: ${googleIdTokenCredential.givenName}
                        """.trimIndent()
                        Log.d(TAG, "Información del usuario: $userInfo")

                        signInWithGoogleIdToken(idToken)

                    } catch (e: GoogleIdTokenParsingException) {
                        Result.failure(Exception("Token de Google inválido"))
                    }
                } else {
                    Result.failure(Exception("Tipo de credencial no soportada"))
                }
            }

            else -> {
                Result.failure(Exception("Tipo de credencial no reconocida"))
            }
        }
    }

    override suspend fun clearCredentialState() {
        try {
            val request = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(request)
            Log.d(TAG, "Estado de credenciales limpiado exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al limpiar estado de credenciales", e)
        }
    }

    override suspend fun linkGoogle(activity: Activity): Result<UserDto?> {
        return try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("No hay usuario autenticado"))
            }

            val nonce = generateNonce()

            // Try with allowed accounts
            val googleIdOption = credentialOptionsFactory.createGoogleIdOption(
                serverClientId = context.getString(R.string.default_web_client_id),
                nonce = nonce,
                filterByAuthorizedAccounts = true,
                autoSelectEnabled = false
            )

            val request = credentialOptionsFactory.createCredentialRequest(googleIdOption)

            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = activity
                )
                handleCredentialResponseForLinking(result, currentUser)
            } catch (e: GetCredentialException) {
                // Try with all accounts
                linkWithCredentialManagerAllAccounts(activity, currentUser, nonce)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en linkGoogle", e)
            Result.failure(Exception("Error al vincular con Google: ${e.message}"))
        }
    }

    internal fun generateNonce(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private suspend fun signUpWithCredentialManager(activity: Activity, nonce: String): Result<UserDto?> {
        return try {
            val googleIdOption = credentialOptionsFactory.createGoogleIdOption(
                serverClientId = context.getString(R.string.default_web_client_id),
                nonce = nonce,
                filterByAuthorizedAccounts = false,
                autoSelectEnabled = false
            )

            val request = credentialOptionsFactory.createCredentialRequest(googleIdOption)

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )
            handleCredentialResponse(result)
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Error al registrarse: ${e.message}"))
        }
    }

    internal suspend fun signInWithGoogleIdToken(idToken: String): Result<UserDto?> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return completeRegisterWithCredential(credential)
    }

    internal suspend fun completeRegisterWithCredential(credential: AuthCredential): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    val result = if (it.user != null) {
                        Result.success(it.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al iniciar sesión"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = Result.failure<UserDto?>(Exception(it.message.toString()))
                    cancellableContinuation.resume(result)
                }
        }
    }

    internal suspend fun handleCredentialResponseForLinking(
        result: GetCredentialResponse,
        currentUser: FirebaseUser
    ): Result<UserDto?> {
        val credential = result.credential

        return when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = credentialOptionsFactory.parseGoogleIdTokenCredential(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        val userInfo = """
                            ID: ${googleIdTokenCredential.id}
                            Name: ${googleIdTokenCredential.displayName}
                            Email: ${googleIdTokenCredential.givenName}
                        """.trimIndent()
                        Log.d(TAG, "Vinculando usuario de Google: $userInfo")

                        linkWithGoogleIdToken(currentUser, idToken)

                    } catch (e: GoogleIdTokenParsingException) {
                        Result.failure(Exception("Token de Google inválido"))
                    }
                } else {
                    Result.failure(Exception("Tipo de credencial no soportada para vinculación"))
                }
            }
            else -> {
                Result.failure(Exception("Tipo de credencial no reconocida para vinculación"))
            }
        }
    }

    internal suspend fun linkWithGoogleIdToken(currentUser: FirebaseUser, idToken: String): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            // Create credential with token
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            // Link credential to current user
            currentUser.linkWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    val result = if (authResult.user != null) {
                        Log.d(TAG, "Cuenta de Google vinculada exitosamente")
                        Result.success(authResult.user!!.toUserDto())
                    } else {
                        Result.failure(Exception("Error al vincular cuenta de Google"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al vincular con Google", exception)
                    val result = Result.failure<UserDto?>(Exception(exception.message.toString()))
                    cancellableContinuation.resume(result)
                }
        }
    }

    private suspend fun linkWithCredentialManagerAllAccounts(
        activity: Activity,
        currentUser: FirebaseUser,
        nonce: String
    ): Result<UserDto?> {
        return try {
            val googleIdOption = credentialOptionsFactory.createGoogleIdOption(
                serverClientId = context.getString(R.string.default_web_client_id),
                nonce = nonce,
                filterByAuthorizedAccounts = false,
                autoSelectEnabled = false
            )

            val request = credentialOptionsFactory.createCredentialRequest(googleIdOption)

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )
            handleCredentialResponseForLinking(result, currentUser)
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Error al vincular con Google: ${e.message}"))
        }
    }

}