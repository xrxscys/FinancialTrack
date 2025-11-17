package com.example.financialtrack.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.User
import com.example.financialtrack.data.repository.UserRepository
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import android.net.Uri
import com.example.financialtrack.data.repository.FinancialGoalRepository
import com.google.firebase.storage.FirebaseStorage
import com.example.financialtrack.data.model.FinancialGoal


class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val goalRepository: FinancialGoalRepository
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user
    lateinit var activeGoals: LiveData<List<FinancialGoal>>


    init {
        val db = AppDatabase.getDatabase(application)
        userRepository = UserRepository(db.userDao())
        goalRepository = FinancialGoalRepository(db.financialGoalDao())

         loadCurrentUser()
    }

    fun addGoal(goal: FinancialGoal) = viewModelScope.launch {
        goalRepository.insert(goal)
    }


    fun updateSavedAMount(goal: FinancialGoal, amountToChange: Double, isAdding: Boolean) = viewModelScope.launch {
        val newSavedAmount = if (isAdding){
            goal.savedAmount + amountToChange
        }else{
            goal.savedAmount - amountToChange
        }

        val updateGoal = goal.copy(
            savedAmount = newSavedAmount.coerceIn(0.0, goal.targetAmount)
        )

        goalRepository.update(updateGoal)
    }

    fun loadUser(userId: String){
        viewModelScope.launch{
            val userFromDb = userRepository.getUserById(userId)
            if (userFromDb == null || userFromDb.displayName.isNullOrBlank() || userFromDb.email.isNullOrBlank()) {
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                if (firebaseUser != null) {
                    val fallbackUser = User(
                        id = firebaseUser.uid,
                        displayName = firebaseUser.displayName ?: "Set Display Name",
                        email = firebaseUser.email ?: "",
                        photoUrl = firebaseUser.photoUrl?.toString()
                    )
                    _user.postValue(fallbackUser)
                } else {
                    _user.postValue(userFromDb)
                }
            } else {
                _user.postValue(userFromDb)
            }
        }
    }

    private fun loadCurrentUser(){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let {fbUser ->
            userRepository.getUser(fbUser.uid).observeForever { dbUser ->
                _user.postValue(dbUser)
            }
            activeGoals = goalRepository.getActiveGoals(fbUser.uid)
        }
    }

    fun getUser(userId: String): LiveData<User?> {
        return userRepository.getUser(userId)
    }

    fun updateUser(user: User) = viewModelScope.launch {
        userRepository.update(user)
    }

    fun updateDisplayName(newName: String) = viewModelScope.launch {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val localUser = _user.value

        if (firebaseUser != null && localUser != null) {
            val profileUpdates = userProfileChangeRequest {
                displayName = newName
            }

            firebaseUser.updateProfile(profileUpdates).await()

            val updatedLocalUser = localUser.copy(displayName = newName)
            userRepository.update(updatedLocalUser)

            // Reload user data so LiveData updates immediately
            loadUser(firebaseUser.uid)
        }
    }

    fun updateProfilePicture(imageUri: Uri) = viewModelScope.launch {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val localUser = _user.value

        if (firebaseUser != null && localUser != null) {
            try{
                val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/${firebaseUser.uid}.jpg")
                storageRef.putFile(imageUri).await()

                val downloadUrl = storageRef.downloadUrl.await()

                val profileUpdates = userProfileChangeRequest {
                    photoUri = downloadUrl
                }

                firebaseUser.updateProfile(profileUpdates).await()

                val updatedLocalUser = localUser.copy(photoUrl = downloadUrl.toString())
                userRepository.update(updatedLocalUser)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}
