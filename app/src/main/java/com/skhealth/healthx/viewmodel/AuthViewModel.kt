package com.skhealth.healthx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.skhealth.healthx.model.Resource
import com.skhealth.healthx.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    // LiveData to observe authentication results
    private val _authResult = MutableLiveData<Resource<FirebaseUser>>()
    val authResult: LiveData<Resource<FirebaseUser>> = _authResult

    // Login with Google using coroutine
    fun loginWithGoogle(idToken: String) {
        _authResult.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val result = repository.firebaseSignInWithGoogle(idToken)
                _authResult.postValue(result)
            } catch (e: Exception) {
                _authResult.postValue(Resource.Error(e.localizedMessage ?: "Google sign-in failed"))
            }
        }
    }

    // Sign up with email and password
    fun signUpWithEmail(email: String, password: String) {
        _authResult.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val result = repository.signUpWithEmail(email, password)
                _authResult.postValue(result)
            } catch (e: Exception) {
                _authResult.postValue(Resource.Error(e.localizedMessage ?: "Signup failed"))
            }
        }
    }

    // Login with email and password
    fun loginWithEmail(email: String, password: String) {
        _authResult.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val result = repository.loginWithEmail(email, password)
                _authResult.postValue(result)
            } catch (e: Exception) {
                _authResult.postValue(Resource.Error(e.localizedMessage ?: "Login failed"))
            }
        }
    }

    // Optional: expose current user if needed
    val currentUser: FirebaseUser?
        get() = repository.getCurrentUser()
}
