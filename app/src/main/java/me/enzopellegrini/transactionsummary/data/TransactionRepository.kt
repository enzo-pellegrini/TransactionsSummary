package me.enzopellegrini.transactionsummary.data

import androidx.lifecycle.MutableLiveData
import java.util.*
import javax.inject.Inject

class TransactionRepository @Inject constructor() {
}

data class Transaction (
    val name: String = "",
    val transaction_id: String = "", // Used to hide the transaction
    val amount: Int = 0,
    val category: String = "",  // In firestore should be the category name
    val date: Date,
    val item_id: String = "" // Used to filter transactions based on choices in accounts fragment
)