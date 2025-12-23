package com.example.financialtrack.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.financialtrack.data.database.AppDatabase
import com.example.financialtrack.data.model.User
import com.example.financialtrack.data.repository.UserRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.userProfileChangeRequest

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    val passwordChangeResult = MutableLiveData<Boolean>()

    init {
        repository = UserRepository(AppDatabase.getDatabase(application).userDao())
        _currentUser.value = auth.currentUser
    }

    fun signInWithEmail(email: String, password: String) = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            _currentUser.value = auth.currentUser
        } catch (e: Exception) {
            e.printStackTrace()
            _currentUser.value = null
        }
    }


    fun createUserWithEmail(email: String, password: String, displayName: String) = viewModelScope.launch {
        try{
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            firebaseUser?.let { userToUpdate ->
                val profileUpdates = userProfileChangeRequest {
                    this.displayName = displayName
                }
                userToUpdate.updateProfile(profileUpdates).await()

                val newUserForDb = User(
                    id = userToUpdate.uid,
                    displayName = userToUpdate.displayName,
                    email = userToUpdate.email!!,
                    photoUrl = null
                )
                repository.insert(newUserForDb)
                _currentUser.value = auth.currentUser
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _currentUser.value = null
        }
    }

    fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun signInWithGoogle(credential: AuthCredential, displayName: String?, photoUrl: String?) = viewModelScope.launch {
        try {
            auth.signInWithCredential(credential).await()
            val firebaseUser = auth.currentUser
            firebaseUser?.let { fbUser ->
                val existingUser = repository.getUserById(fbUser.uid)
                if (existingUser == null) {
                    val newUser = User(
                        id = fbUser.uid,
                        displayName = fbUser.displayName,
                        email = fbUser.email!!,
                        photoUrl = photoUrl
                    )
                    repository.insert(newUser)
                }
            }
            _currentUser.value = auth.currentUser
        } catch (e: Exception) {
            e.printStackTrace()
            _currentUser.value = null
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private fun getUser(userId: String): LiveData<User?> {
        return repository.getUser(userId)
    }

    private fun insertUser(user: User) = viewModelScope.launch {
        repository.insert(user)
    }

    fun updateUser(user: User) = viewModelScope.launch {
        repository.update(user)
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun changeUserPassword(currentPassword: String, newPassword: String) = viewModelScope.launch {
        val user = auth.currentUser
        if (user?.email == null) {
            passwordChangeResult.value = false
            return@launch
        }
        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        passwordChangeResult.value = true
                    } else {
                        passwordChangeResult.value = false
                    }
                }
            } else {
                passwordChangeResult.postValue(false)
            }
        }
    }

}
