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
import com.example.financialtrack.ui.transaction.TransactionActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.example.financialtrack.ui.profile.ProfileActivity
import com.example.financialtrack.ui.reports.ReportsActivity
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        authViewModel.currentUser.observe(this){ firebaseUser ->
            if (firebaseUser != null) {
                binding.tvUserEmail.text = firebaseUser.email
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

        binding.btnReports.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))

        }

        //Open Transaction Screen
        binding.btnTransactions.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))

            // Open Profile screen
        binding.btnProfile.setOnClickListener {
            val currentUser = authViewModel.currentUser.value
            if (currentUser != null){
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("USER_ID", currentUser.uid)
                startActivity(intent)
            }

        }
    }

    private fun signOut() {
        // Sign out from Firebase
        authViewModel.signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // The phantom error might appear here too, it's okay
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            val intent = Intent(this, LoginActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
