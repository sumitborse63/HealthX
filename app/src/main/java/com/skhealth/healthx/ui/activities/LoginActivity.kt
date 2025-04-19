package com.skhealth.healthx.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.skhealth.healthx.MainActivity
import com.skhealth.healthx.R
import com.skhealth.healthx.viewmodel.AuthViewModel
import com.skhealth.healthx.viewmodel.AuthViewModelFactory
import com.skhealth.healthx.firebase.FirebaseSource
import com.skhealth.healthx.repository.AuthRepository
import com.skhealth.healthx.model.Resource

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private val RC_SIGN_IN = 9001
    private lateinit var progressBar: ProgressBar

    // Register the Google Sign-In result callback
    private val googleSignInResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.result
                account?.let { firebaseAuthWithGoogle(it.idToken) }
            } else {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Create an instance of FirebaseSource
        val firebaseSource = FirebaseSource()

        // Create an instance of AuthRepository with FirebaseSource
        val repository = AuthRepository(firebaseSource)

        // Create an instance of AuthViewModelFactory
        val factory = AuthViewModelFactory(repository)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        firebaseAuth = FirebaseAuth.getInstance() // Initialize Firebase Auth

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Views for Email login and Google sign-in
        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val btnEmailLogin = findViewById<Button>(R.id.btnSignin)
        val btnGoogleSignIn = findViewById<Button>(R.id.btngoogleSignin)
        progressBar = findViewById(R.id.progressBar) // Loading indicator
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val tvGoToSignup = findViewById<TextView>(R.id.tvGoToSignUp)

        // Set click listener for Go to Signup
        tvGoToSignup.setOnClickListener {
            // Navigate to Signup Activity
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        tvForgotPassword.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                resetPassword(email)
            } else {
                Toast.makeText(
                    this,
                    "Please enter your email to reset the password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
            // Google Sign-In button
        btnGoogleSignIn.setOnClickListener {
            googleSignInResult.launch(googleSignInClient.signInIntent) // Launch Google Sign-In
        }

        // Email login button
        btnEmailLogin.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                progressBar.visibility = ProgressBar.VISIBLE
                viewModel.loginWithEmail(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        observeViewModel()
    }

    // Handle Firebase sign-in with Google
    private fun firebaseAuthWithGoogle(idToken: String?) {
        if (idToken != null) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = ProgressBar.INVISIBLE
                    if (task.isSuccessful) {
                        // Successfully signed in with Firebase
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        // If sign-in fails, display a message to the user.
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun resetPassword(email: String) {
        progressBar.visibility = ProgressBar.VISIBLE
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                progressBar.visibility = ProgressBar.INVISIBLE
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send reset email. Please check the email address.", Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun observeViewModel() {
        viewModel.authResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Show loading spinner when loading
                    progressBar.visibility = ProgressBar.VISIBLE
                }
                is Resource.Success -> {
                    // Hide loading spinner and navigate to the main activity
                    progressBar.visibility = ProgressBar.INVISIBLE
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is Resource.Error -> {
                    // Hide loading spinner and show error message
                    progressBar.visibility = ProgressBar.INVISIBLE
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
