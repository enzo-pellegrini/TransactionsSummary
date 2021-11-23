package me.enzopellegrini.transactionsummary.ui

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.enzopellegrini.transactionsummary.data.UserRepository
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    val currentUser = userRepository.currentUser
    val isLoggedIn = Transformations.map(currentUser) { it != null }
}