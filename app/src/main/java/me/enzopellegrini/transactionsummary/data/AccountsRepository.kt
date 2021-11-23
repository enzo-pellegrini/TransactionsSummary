package me.enzopellegrini.transactionsummary.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.plaid.link.result.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AccountsRepository @Inject constructor() {
    private val _accounts = MutableLiveData<List<Item>>()
    val accounts: LiveData<List<Item>> = _accounts

    private var registration: ListenerRegistration? = null

    private val db = Firebase.firestore
    private val functions: FirebaseFunctions by lazy { Firebase.functions }
    private val auth: FirebaseAuth by lazy { Firebase.auth }

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
        functions.getHttpsCallable("getLinkToken")
            .call()
            .continueWith { task ->
                task.result?.data as Map<String, String>
            }

    fun registerLinkResult(result: LinkSuccess): Task<Any> =
        functions.getHttpsCallable("registerPublicToken")
            .call(hashMapOf(
                "public_token" to result.publicToken,
                "institution_name" to result.metadata.institution?.name,
                "account_ids" to result.metadata.accounts.map { it.id },
            ))
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
            fetchAccounts(uid)
            // This should work, must show the prof
            registration = db.collection("items-per-user")
                .document(uid)
                .collection("items")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.d(TAG, "Query failed")
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        Log.d(TAG, snapshot.toString())
                        _accounts.value = snapshot.toObjects(Item::class.java)
                    }
                }
        }
    }

    private fun fetchAccounts(uid: String) {
        db.collection("items-per-user")
            .document(uid)
            .collection("items")
            .get()
            .addOnCompleteListener { task ->
                task.addOnSuccessListener { query ->
                    _accounts.value = query.toObjects(Item::class.java)
                }
            }
    }

    fun deleteItem(itemId: String) {
        // Does delete on firebase
        val ref = db.collection("items-per-user")
            .document(auth.currentUser?.uid!!)
            .collection("items")
            .document(itemId)
        ref.delete()
            .addOnSuccessListener {  }
            .addOnFailureListener {  }
    }

//    init {
//        db.collection("items-per-user/${}")
//    }


    companion object {
        const val TAG = "AccountsRepository"
    }
}


data class Item(
    val institution_name: String = "",
    val item_id: String = ""
)