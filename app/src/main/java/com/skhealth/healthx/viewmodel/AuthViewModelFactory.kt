package com.skhealth.healthx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skhealth.healthx.firebase.FirebaseSource
import com.skhealth.healthx.repository.AuthRepository

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // Pass the repository directly to AuthViewModel
            return AuthViewModel(repository) as T  // Pass AuthRepository to AuthViewModel
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
