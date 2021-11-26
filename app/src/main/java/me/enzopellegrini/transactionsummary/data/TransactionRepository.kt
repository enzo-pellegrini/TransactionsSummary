package me.enzopellegrini.transactionsummary.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import javax.inject.Inject

class TransactionRepository @Inject constructor() {
    val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    private var prevUser: String? = null
    private var registration: ListenerRegistration? = null

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    init {
        recheckTransactions()
    }

    private fun recheckTransactions() {
        if (registration != null) {
            registration!!.remove()
            registration = null
        }

        if (auth.currentUser == null) {
            _transactions.value = listOf()
        } else {
            val uid = auth.currentUser?.uid!!
            fetchTransactions(uid)
            // This should work, must show the prof
            registration = db.collection("transactions-per-user")
                .document(uid)
                .collection("transactions")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.d(AccountsRepository.TAG, "Query failed")
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        Log.d(AccountsRepository.TAG, snapshot.toString())
                        _transactions.value = snapshot.toObjects(Transaction::class.java)
                    }
                }
        }
    }

    private fun fetchTransactions(uid: String) {
        db.collection("transactions-per-user")
            .document(uid)
            .collection("transactions")
            .get()
            .addOnSuccessListener { query ->
                _transactions.value = query.toObjects(Transaction::class.java)
            }
    }

    companion object {
        val TAG = "TransactionRepository"
    }
}

data class Transaction (
    val name: String = "",
    val transaction_id: String = "", // Used to hide the transaction
    val amount: Int = 0,
    val category: String = "",  // In firestore should be the category name
    val date: Date = Date(),
    val item_id: String = "" // Used to filter transactions based on choices in accounts fragment
)