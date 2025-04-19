package com.skhealth.healthx.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.skhealth.healthx.MainActivity
import com.skhealth.healthx.R
import com.skhealth.healthx.firebase.FirebaseSource
import com.skhealth.healthx.model.Resource
import com.skhealth.healthx.repository.AuthRepository
import com.skhealth.healthx.viewmodel.AuthViewModel
import com.skhealth.healthx.viewmodel.AuthViewModelFactory

class SignupActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private val googleSignInResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.result
                account?.let {
                    // Attempt to sign in or create a new user
                    handleGoogleSignIn(it.idToken)
                }
            }
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase Auth
        firebaseAuth = Firebase.auth

        // Initialize ViewModel with Factory
        val firebaseSource = FirebaseSource()
        val repository = AuthRepository(firebaseSource)
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val signUpButton = findViewById<Button>(R.id.btnSignUp)
        val googleSignUpButton = findViewById<Button>(R.id.btnGoogleSignUp)
        val alreadyUserTextView = findViewById<TextView>(R.id.tvAlreadyUser)

        // Observe LiveData from ViewModel
        viewModel.authResult.observe(this, Observer { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading UI (e.g., progress bar)
                }
                is Resource.Success -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is Resource.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        // Sign-up with Email and Password
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result = task.result
                            val existingMethods = result?.signInMethods

                            if (existingMethods != null && existingMethods.isNotEmpty()) {
                                Toast.makeText(this, "User already exists. Please login.", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            } else {
                                // Trigger signup logic
                                viewModel.signUpWithEmail(email, password)
                            }
                        } else {
                            Toast.makeText(this, "Error checking user existence", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigate to Login if already a user
        alreadyUserTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Google Sign-Up Button
        googleSignUpButton.setOnClickListener {
            googleSignInResult.launch(googleSignInClient.signInIntent)
        }
    }

    // Handle Google sign-in and check if the user exists
    private fun handleGoogleSignIn(idToken: String?) {
        if (idToken != null) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            // Attempt to sign in with Google credential
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // User signed in successfully
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        // If sign-in fails, show message
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

}
