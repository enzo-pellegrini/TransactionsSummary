package me.enzopellegrini.transactionsummary.data

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Transaction(
    val name: String = "",
    val transaction_id: String = "", // Used to hide the transaction
    val amount: Double = 0.0,
    val category_id: String = "default",  // In firestore should be the category name
    val date: Date = Date(),
    val item_id: String = "" // Used to filter transactions based on choices in accounts fragment
)