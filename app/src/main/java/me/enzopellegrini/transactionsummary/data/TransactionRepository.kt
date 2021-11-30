package me.enzopellegrini.transactionsummary.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import me.enzopellegrini.transactionsummary.authInstance
import me.enzopellegrini.transactionsummary.firestoreInstance
import javax.inject.Inject

class TransactionRepository @Inject constructor() {
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions
//        Transformations.map(_transactions) { it.filter { t -> t.amount >= 0 } }

    private var prevUser: String? = null
    private var registration: ListenerRegistration? = null

    private val auth = authInstance
    private val db = firestoreInstance

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
//            fetchTransactions(uid)
//            // This should work, must show the prof
//            registration = db.collection("transactions-per-user")
//                .document(uid)
//                .collection("transactions")
//                .addSnapshotListener { snapshot, e ->
//                    if (e != null) {
//                        Log.d(AccountsRepository.TAG, "Query failed")
//                        return@addSnapshotListener
//                    }
//
//                    if (snapshot != null) {
//                        Log.d(AccountsRepository.TAG, snapshot.toString())
//                        _transactions.value = snapshot.toObjects(Transaction::class.java)
//                    }
//                }

            val queryRef = db.collection("items")
                .whereArrayContains("read_access", uid)

            queryRef.get().addOnSuccessListener { query ->
                val items = query.toObjects(ItemWithTransactions::class.java);
                var out: MutableList<Transaction> = mutableListOf()
                for (item in items) {
                    val institution = item.institution_name
                    out.addAll(item.transactions.map { Transaction(it, institution) })
                }

                _transactions.value = out
            }

            queryRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d(TAG, "query failed")
                    return@addSnapshotListener
                }
                if (snapshot!= null) {
                    val items = snapshot.toObjects(ItemWithTransactions::class.java);
                    var out: MutableList<Transaction> = mutableListOf()
                    for (item in items) {
                        val institution = item.institution_name
                        out.addAll(item.transactions.map { Transaction(it, institution) })
                    }

                    _transactions.value = out
                }
            }
        }
    }

//    private fun fetchTransactions(uid: String) {
//        db.collection("transactions-per-user")
//            .document(uid)
//            .collection("transactions")
//            .orderBy("date", Query.Direction.DESCENDING)
//            .get()
//            .addOnSuccessListener { query ->
//                _transactions.value = query.toObjects(FirestoreTransaction::class.java)
//            }
//    }

    val categorySummary: LiveData<Map<String, SegmentSummary>> by lazy {
        Transformations.map(transactions) { all ->
            all
                .filter { it.amount >= 0 }
                .groupBy { it.category }
                .map {
                    Pair(
                        it.key,
                        SegmentSummary(
                            it.value.count(),
                            it.value.sumOf { it.amount }
                        )
                    )
                }
                .toMap()
        }
    }


    companion object {
        val TAG = "TransactionRepository"
    }
}

data class SegmentSummary(
    val count: Int,
    val sum: Double
)

class Transaction(val from: FirestoreTransaction, val institution: String) {
    val name = from.name
    val transactionId = from.transaction_id
    val amount = from.amount
    val category = from.category
    val date = from.date
}

data class ItemWithTransactions(
    val institution_name: String = "",
    val transactions: List<FirestoreTransaction> = listOf()
)