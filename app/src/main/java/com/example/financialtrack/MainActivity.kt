package com.example.financialtrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.financialtrack.databinding.ActivityMainBinding
import com.example.financialtrack.ui.auth.AuthViewModel
import com.example.financialtrack.ui.auth.LoginActivity
import com.example.financialtrack.ui.debt.DebtActivity
import com.example.financialtrack.ui.notifications.NotificationActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe the current user's data
        authViewModel.currentUser.observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                // User is logged in, display their email
                binding.tvUserEmail.text = firebaseUser.email
            } else {
                // User is signed out, navigate back to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        // Set up the sign out button
        binding.btnSignOut.setOnClickListener {
            signOut()
        }

        // Open Notifications screen
        binding.btnNotification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        // Open Debt & Loan screen
        binding.btnDebts.setOnClickListener {
            startActivity(Intent(this, DebtActivity::class.java))
        }

    }

    private fun signOut() {
        // Sign out from Firebase
        authViewModel.signOut()

        // Sign out from Google
        // We need to build the GoogleSignInClient again here to sign out properly
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // The phantom error might appear here too, it's okay
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
    }
}
