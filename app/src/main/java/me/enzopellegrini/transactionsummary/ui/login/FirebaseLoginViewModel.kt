package me.enzopellegrini.transactionsummary.ui.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.enzopellegrini.transactionsummary.data.UserRepository
import javax.inject.Inject

@HiltViewModel
class FirebaseLoginViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {

    val isLoggedIn = userRepository.isLoggedIn
}