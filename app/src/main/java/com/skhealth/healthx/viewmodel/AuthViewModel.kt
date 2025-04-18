package com.skhealth.healthx.viewmodel

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    val currentUser = repository.getCurrentUser()

    fun loginWithGoogle(idToken: String) = liveData {
        emit(Resource.Loading())
        try {
            val result = repository.firebaseSignInWithGoogle(idToken)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error"))
        }
    }

    fun signUpWithEmail(email: String, password: String) = liveData {
        emit(Resource.Loading())
        try {
            val result = repository.signUpWithEmail(email, password)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error"))
        }
    }
}
