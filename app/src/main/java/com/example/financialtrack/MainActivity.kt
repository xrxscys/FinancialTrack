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
import com.example.financialtrack.ui.budget.BudgetActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.example.financialtrack.ui.profile.ProfileActivity
import com.example.financialtrack.ui.reports.ReportsActivity
import com.example.financialtrack.utils.NotificationService
import com.example.financialtrack.ui.notification.NotificationViewModel
import com.example.financialtrack.utils.DebugNotificationHelper
import com.google.firebase.auth.FirebaseAuth
import android.content.SharedPreferences
import com.example.financialtrack.ui.accounts.AccountsActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val notificationViewModel: NotificationViewModel by viewModels()
    private lateinit var notificationService: NotificationService
    private lateinit var debugNotificationHelper: DebugNotificationHelper
    private lateinit var sharedPreferences: SharedPreferences
    private var isDebugMode = false

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

        // Initialize notification service
        notificationService = NotificationService(this)
        debugNotificationHelper = DebugNotificationHelper(this)

        // Initialize SharedPreferences for debug mode persistence
        sharedPreferences = getSharedPreferences("FinancialTrackDebug", MODE_PRIVATE)
        isDebugMode = sharedPreferences.getBoolean("debug_mode_enabled", false)

        authViewModel.currentUser.observe(this) { firebaseUser ->
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
        }

        // Open Accounts screen
        binding.btnAccounts.setOnClickListener {
            startActivity(Intent(this, AccountsActivity::class.java))
        }

        //Open Budgets Screen
        binding.btnBudget.setOnClickListener {
            startActivity(Intent(this, BudgetActivity::class.java))
        }


        // Open Profile screen
        binding.btnProfile.setOnClickListener {
            val currentUser = authViewModel.currentUser.value
            if (currentUser != null) {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("USER_ID", currentUser.uid)
                startActivity(intent)
            }

        }

        // ===== TEST NOTIFICATION BUTTONS =====

        // Test: Bill Reminder
        binding.btnTestBillReminder.setOnClickListener {
            val notification = notificationService.createBillReminderNotification(
                billName = "Electric Bill",
                dueDate = "December 15, 2025"
            )
            if (notification != null) {
                notificationViewModel.insertNotification(notification)
                showTestNotificationToast("Bill Reminder sent!")
                if (isDebugMode) {
                    debugNotificationHelper.showDebugSystemNotification(
                        title = "🧪 Bill Reminder (Debug)",
                        message = "Electric Bill due December 15, 2025"
                    )
                }
            }
        }

        // Test: Large Transaction Alert
        binding.btnTestLargeTransaction.setOnClickListener {
            val notification = notificationService.createLargeTransactionNotification(
                amount = 250.50,
                description = "Electronics Purchase"
            )
            if (notification != null) {
                notificationViewModel.insertNotification(notification)
                showTestNotificationToast("Large Transaction alert sent!")
                if (isDebugMode) {
                    debugNotificationHelper.showDebugSystemNotification(
                        title = "🧪 Large Transaction (Debug)",
                        message = "Transaction: \$250.50 - Electronics Purchase"
                    )
                }
            }
        }

        // Test: Budget Alert
        binding.btnTestBudgetAlert.setOnClickListener {
            val notification = notificationService.createBudgetAlertNotification(
                category = "Groceries",
                percentageUsed = 85
            )
            if (notification != null) {
                notificationViewModel.insertNotification(notification)
                showTestNotificationToast("Budget Alert sent!")
                if (isDebugMode) {
                    debugNotificationHelper.showDebugSystemNotification(
                        title = "🧪 Budget Alert (Debug)",
                        message = "Groceries budget 85% used"
                    )
                }
            }
        }

        // Test: Goal Update
        binding.btnTestGoalUpdate.setOnClickListener {
            val notification = notificationService.createGoalUpdateNotification(
                goalName = "Emergency Fund",
                progress = 45
            )
            if (notification != null) {
                notificationViewModel.insertNotification(notification)
                showTestNotificationToast("Goal Progress notification sent!")
                if (isDebugMode) {
                    debugNotificationHelper.showDebugSystemNotification(
                        title = "🧪 Goal Progress (Debug)",
                        message = "Emergency Fund: 45% complete"
                    )
                }
            }
        }

        // Test: Generic Notification
        binding.btnTestGeneric.setOnClickListener {
            val notification = notificationService.createGenericNotification(
                title = "Test Notification",
                message = "This is a generic test notification to verify the system works properly."
            )
            if (notification != null) {
                notificationViewModel.insertNotification(notification)
                showTestNotificationToast("Generic notification sent!")
                // In debug mode, also show system notification
                if (isDebugMode) {
                    debugNotificationHelper.showDebugSystemNotification(
                        title = "🧪 Generic Notification (Debug)",
                        message = "This is a generic test notification to verify the system works properly."
                    )
                }
            }
        }

        // ===== DEBUG MODE TOGGLE =====
        // Long-press "Test Notifications" label to enable/disable debug mode
        binding.tvTestNotificationsLabel.setOnLongClickListener {
            isDebugMode = !isDebugMode
            sharedPreferences.edit().putBoolean("debug_mode_enabled", isDebugMode).apply()

            val status = if (isDebugMode) "ENABLED ✓" else "DISABLED ✗"
            val message = "Debug Mode $status\n(System notifications show even when app is open)"

            android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()

            // Update button text to indicate debug mode
            if (isDebugMode) {
                binding.tvTestNotificationsLabel.text = "Test Notifications 🧪 [DEBUG MODE ON]"
            } else {
                binding.tvTestNotificationsLabel.text = "Test Notifications"
            }

            true  // Consume the long click
        }
    }

    private fun showTestNotificationToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    fun signOut() {
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
