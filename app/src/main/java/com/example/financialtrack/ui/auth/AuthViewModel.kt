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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    private val auth: FirebaseAuth

    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    lateinit var currentUser: LiveData<User?>

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _firebaseUser.value = firebaseAuth.currentUser
    }

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)

        currentUser = _firebaseUser.switchMap { firebaseUser: FirebaseUser? ->
            if (firebaseUser == null) {
                MutableLiveData<User?>(null)
            } else {
                repository.getUser(firebaseUser.uid)
            }
        }

        auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener(authStateListener)
    }

    fun signInWithGoogle(credential: AuthCredential) = viewModelScope.launch {
        try {
            auth.signInWithCredential(credential).await()
            val firebaseUser = auth.currentUser
            firebaseUser?.let {
                val user = User(
                    id = it.uid,
                    displayName = it.displayName,
                    email = it.email!!,
                    photoUrl = it.photoUrl?.toString()
                )
                insertUser(user)
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        auth.removeAuthStateListener(authStateListener)
    }
}
