package me.enzopellegrini.transactionsummary.ui.home

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import me.enzopellegrini.transactionsummary.data.TransactionRepository
import me.enzopellegrini.transactionsummary.data.UserRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionsRepository: TransactionRepository
) : ViewModel() {
    val transactions = transactionsRepository.transactions
}