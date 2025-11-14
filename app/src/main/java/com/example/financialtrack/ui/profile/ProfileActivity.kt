package com.example.financialtrack.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financialtrack.R
import com.example.financialtrack.databinding.ActivityProfileBinding
import com.example.financialtrack.ui.profile.FinancialGoals
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.financialtrack.ui.auth.AuthViewModel
import com.example.financialtrack.ui.auth.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            profileViewModel.updateProfilePicture(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileViewModel.user.observe(this) { user ->
            user?.let {
                binding.tvDisplayName.text = it.displayName ?: "Set Display Name"
                binding.tvEmail.text = it.email


                Glide.with(this)
                    .load(it.photoUrl)
                    .placeholder(R.drawable.ic_profile)
                    .circleCrop()
                    .into(binding.ivProfilePicture)
            }
        }

        checkLoginProvider()

        authViewModel.passwordChangeResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }


        binding.ivProfilePicture.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnFinancialgoal.setOnClickListener {
            val intent = Intent(this, FinancialGoals::class.java)
            startActivity(intent)
        }

        binding.btnEditName.setOnClickListener {
            showEditNameDialog()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnLogout.setOnClickListener {
            signOut()
        }
    }

    private fun showEditNameDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Display Name")

        val input = EditText(this)
        input.setText(profileViewModel.user.value?.displayName)
        builder.setView(input)

        builder.setPositiveButton("Save") { dialog, _->
            val newName = input.text.toString().trim()
            if(newName.isNotEmpty()) {
                profileViewModel.updateDisplayName(newName)
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun checkLoginProvider() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val isEmailPasswordUser = it.providerData.any { provider -> provider.providerId == "password" }

            if (isEmailPasswordUser){
                binding.btnChangePassword.visibility = View.VISIBLE
            } else {
                binding.btnChangePassword.visibility = View.GONE
            }
        }
    }

    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val etCurrentPassword = dialogView.findViewById<EditText>(R.id.et_current_password)
        val etNewPassword = dialogView.findViewById<EditText>(R.id.et_new_password)
        val etConfirmPassword = dialogView.findViewById<EditText>(R.id.et_confirm_password)


        builder.setView(dialogView)
            .setTitle("Change Password")
            .setPositiveButton("Save") { dialog, _ ->
                val currentPassword = etCurrentPassword.text.toString().trim()
                val newPassword = etNewPassword.text.toString().trim()
                val confirmPassword = etConfirmPassword.text.toString().trim()

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                } else if (newPassword.length < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                } else {
                    authViewModel.changeUserPassword(currentPassword, newPassword)
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun signOut(){
        authViewModel.signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
