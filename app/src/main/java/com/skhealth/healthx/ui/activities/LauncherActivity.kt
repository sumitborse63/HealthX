package com.skhealth.healthx.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.skhealth.healthx.MainActivity
import com.skhealth.healthx.R

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            // User is signed in, go to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // User not signed in, go to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish() // Close LauncherActivity
    }
}
