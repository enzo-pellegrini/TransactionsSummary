package me.enzopellegrini.transactionsummary.ui.transactionPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import me.enzopellegrini.transactionsummary.SingleLiveEvent
import me.enzopellegrini.transactionsummary.data.Transaction
import me.enzopellegrini.transactionsummary.data.TransactionRepository
import javax.inject.Inject

@HiltViewModel
class TransactionPageViewModel @Inject constructor(
    val transactionsRepository: TransactionRepository
): ViewModel() {
    fun getTransaction(position: Int): LiveData<Transaction> =
        transactionsRepository.transactions.map { it[position] }
}