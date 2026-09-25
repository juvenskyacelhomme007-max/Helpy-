package com.example.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.model.UserProfile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(private val context: Context) {
    private val auth: FirebaseAuth = Firebase.auth
    private val databaseId: String = try {
        context.getString(R.string.firestore_database_id)
    } catch (e: Exception) {
        ""
    }
    private val firestore: FirebaseFirestore = if (databaseId.isNotEmpty()) {
        FirebaseFirestore.getInstance(databaseId)
    } else {
        @Suppress("DEPRECATION")
        FirebaseFirestore.getInstance()
    }

    private val credentialManager: CredentialManager = CredentialManager.create(context)

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    private fun getWebClientId(): String? {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        return if (resId != 0) {
            context.getString(resId)
        } else {
            null
        }
    }

    suspend fun attemptAutoSignIn(): Result<FirebaseUser> {
        val user = auth.currentUser
        if (user != null) {
            syncUserProfile(user)
            return Result.success(user)
        }

        val clientId = getWebClientId()
            ?: return Result.failure(IllegalStateException("Web Client ID introuvable dans les ressources"))

        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val signedInUser = authResult.user ?: throw IllegalStateException("Utilisateur connecté introuvable")
                syncUserProfile(signedInUser)
                Result.success(signedInUser)
            } else {
                Result.failure(IllegalStateException("Identifiants non valides"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(activity: Activity): Result<FirebaseUser> {
        val clientId = getWebClientId()
            ?: return Result.failure(IllegalStateException("Configuration Google Sign-In : default_web_client_id indisponible."))

        return try {
            val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()

            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user ?: throw IllegalStateException("Impossible de connecter l'utilisateur")
                syncUserProfile(user)
                Result.success(user)
            } else {
                Result.failure(IllegalStateException("Type d'identifiant inconnu"))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w("AuthRepository", "Connexion Google annulée par l'utilisateur: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erreur lors de la connexion Google", e)
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erreur lors de la déconnexion", e)
            Result.failure(e)
        }
    }

    private suspend fun syncUserProfile(user: FirebaseUser) {
        try {
            val userDocRef = firestore.collection("users").document(user.uid)
            val snapshot = userDocRef.get().await()
            if (!snapshot.exists()) {
                val profile = UserProfile(
                    id = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: "Membre Helpy",
                    photoUrl = user.photoUrl?.toString() ?: "",
                    phoneNumber = user.phoneNumber ?: "",
                    country = "Mondial",
                    currency = "USD",
                    role = "Utilisateur",
                    verified = false,
                    createdAt = Timestamp.now()
                )
                userDocRef.set(profile).await()
            }
        } catch (e: Exception) {
            Log.w("AuthRepository", "Impossible de synchroniser le profil utilisateur: ${e.message}")
        }
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Vous devez être connecté"))
            firestore.collection("users").document(uid).set(profile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
