package com.skhealth.healthx.repository

import com.skhealth.healthx.firebase.FirebaseSource

class AuthRepository(private val firebaseSource: FirebaseSource) {
    fun getCurrentUser() = firebaseSource.getCurrentUser()

    suspend fun firebaseSignInWithGoogle(idToken: String) =
        firebaseSource.firebaseSignInWithGoogle(idToken)

    suspend fun signUpWithEmail(email: String, password: String) =
        firebaseSource.signUpWithEmail(email, password)

    suspend fun loginWithEmail(email: String, password: String) =
        firebaseSource.loginWithEmail(email, password)
}
