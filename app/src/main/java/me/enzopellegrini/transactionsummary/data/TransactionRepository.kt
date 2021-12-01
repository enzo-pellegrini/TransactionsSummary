package me.enzopellegrini.transactionsummary.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import com.google.firebase.firestore.ListenerRegistration
import me.enzopellegrini.transactionsummary.authInstance
import me.enzopellegrini.transactionsummary.firestoreInstance
import me.enzopellegrini.transactionsummary.ui.home.TotalState
import java.util.*
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

            val queryRef = db.collection("items")
                .whereArrayContains("read_access", uid)

            queryRef.get().addOnSuccessListener { query ->
                val items = query.toObjects(ItemWithTransactions::class.java)
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
                if (snapshot != null) {
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


    val categorySummary: LiveData<Map<String, SegmentSummary>> by lazy {
        Transformations.map(transactions) { all ->
            all
                .filter { it.amount >= 0 }
                .groupBy { it.category }
                .map {
                    Pair(
                        it.key,
                        SegmentSummary(it.value)
                    )
                }
                .toMap()
        }
    }


    val summaryPerAccountPerDay: LiveData<Map<String, Map<Date, SegmentSummary>>> by lazy {
        Transformations.map(transactions) { all ->
            all
                .filter { it.amount >= 0 }
                .groupBy { it.institution }
                .map { entry ->
                    Pair(
                        entry.key,
                        entry.value
                            .groupBy {
                                it.date.withoutTime()
                            }
                            .map { Pair(it.key, SegmentSummary(it.value)) }
                            .toMap()
                    )
                }
                .toMap()
        }
    }


    fun amountSince(period: TotalState): LiveData<Double> =
        transactions.map {
            it.sumOf { it.amount }
        }


    companion object {
        val TAG = "TransactionRepository"
    }
}


// Why not be messy
fun Date.withoutTime(): Date {
    val c = Calendar.getInstance(TimeZone.getDefault())
    c.time = this
    c.set(c.get(Calendar.YEAR) + 1900, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0)
    return c.time
}


data class SegmentSummary(
    val count: Int,
    val sum: Double
) {
    constructor(l: List<Transaction>) : this(l.count(), l.sumOf { it.amount })
}

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

data class FirestoreTransaction(
    val name: String = "",
    val transaction_id: String = "", // Used to hide the transaction
    val amount: Double = 0.0,
    val category: String = "default",  // In firestore should be the category name
    val date: Date = Date(),
)
