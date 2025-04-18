package com.skhealth.healthx.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import com.skhealth.healthx.model.Resource

class FirebaseSource {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Return current user if available
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Sign in with Google and return Resource with FirebaseUser or error
    suspend fun firebaseSignInWithGoogle(idToken: String): Resource<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: throw Exception("Google sign-in failed: User not found")
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error("Google sign-in failed: ${e.localizedMessage}")
        }
    }

    // Sign up with email and password
    suspend fun signUpWithEmail(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Signup failed: User not found")
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error("Signup failed: ${e.localizedMessage}")
        }
    }

    // Login with email and password
    suspend fun loginWithEmail(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Login failed: User not found")
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error("Login failed: ${e.localizedMessage}")
        }
    }
}
