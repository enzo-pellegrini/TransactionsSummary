package me.enzopellegrini.transactionsummary.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.plaid.link.result.*
import me.enzopellegrini.transactionsummary.authInstance
import me.enzopellegrini.transactionsummary.firestoreInstance
import me.enzopellegrini.transactionsummary.functionsInstance
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AccountsRepository @Inject constructor() {
    private val _accounts = MutableLiveData<List<Item>>()
    val accounts: LiveData<List<Item>> = _accounts
    val hasAccounts = accounts.map { it.isNotEmpty() }

    private var registration: ListenerRegistration? = null


    private val functions = functionsInstance
    private val auth = authInstance
    private val db = firestoreInstance


    private var prevUser: String? = null

    // Get the connected bank accounts and recheck every time the auth state changes
    init {
        recheckAccount()
        auth.addAuthStateListener {
            if (it.currentUser?.uid != prevUser) {
                recheckAccount()
                prevUser = it.currentUser?.uid
            }
        }
    }

    fun getLinkToken(): Task<Map<String, String>> =
        functions.getHttpsCallable("produceLinkToken")
            .call()
            .continueWith { task ->
                task.result?.data as Map<String, String>
            }

    fun registerLinkResult(result: LinkSuccess): Task<Any> =
        functions.getHttpsCallable("savePublicToken")
            .call(
                hashMapOf(
                    "public_token" to result.publicToken,
                    "institution_id" to result.metadata.institution?.id,
                    "institution_name" to result.metadata.institution?.name,
                    "account_ids" to result.metadata.accounts.map { it.id },
                )
            )
            .continueWith { task ->
                task.result?.data
            }


    private fun recheckAccount() {
        if (registration != null) {
            registration!!.remove()
            registration = null
        }

        if (auth.currentUser == null) {
            _accounts.value = listOf()
        } else {
            val uid = auth.currentUser?.uid!!

            val queryRef = db.collection("items")
                .whereArrayContains("read_access", uid);

            queryRef.get()
                .addOnSuccessListener { query ->
                    _accounts.value = query.toObjects(Item::class.java)
                }
            queryRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d(TAG, "Query failed")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _accounts.value = snapshot.toObjects(Item::class.java)
                }
            }
        }
    }

    fun deleteItem(itemId: String) {
        db.collection("items")
            .document(itemId)
            .update(
                mapOf(
                    Pair("read_access", FieldValue.arrayRemove(auth.currentUser?.uid!!))
                )
            )
    }


    companion object {
        const val TAG = "AccountsRepository"
    }
}


data class Item(
    val institution_name: String = "",
    val item_id: String = "",
    val owner: String = "",
    val read_access: List<String> = listOf()
)