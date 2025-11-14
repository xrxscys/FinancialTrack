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
import com.google.firebase.storage.FirebaseStorage


class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        userRepository = UserRepository(userDao)
        loadCurrentUser()
    }

    private fun loadCurrentUser(){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let {fbUser ->
            viewModelScope.launch {
                userRepository.getUser(fbUser.uid).observeForever { dbUser ->
                    _user.postValue(dbUser)
                }
            }
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
