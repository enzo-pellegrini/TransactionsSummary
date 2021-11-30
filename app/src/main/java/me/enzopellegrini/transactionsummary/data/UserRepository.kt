package me.enzopellegrini.transactionsummary.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import me.enzopellegrini.transactionsummary.authInstance
import javax.inject.Inject
import javax.inject.Singleton

class UserRepository @Inject constructor() {
    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = _currentUser


    init {
        authInstance.addAuthStateListener {
            _currentUser.value = it.currentUser
        }
    }
}