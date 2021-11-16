package me.enzopellegrini.transactionsummary.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.plaid.link.result.*
import javax.inject.Inject



class AccountsRepository @Inject constructor() {
    private val _accounts = MutableLiveData<List<AccountUi>>()
    val accounts: LiveData<List<AccountUi>> = _accounts

    private var registration: ListenerRegistration? = null

    private val db = Firebase.firestore
    private val functions: FirebaseFunctions by lazy { Firebase.functions }
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    // Get the connected bank accounts and recheck every time the auth state changes
    init {
        recheckAccount()
        auth.addAuthStateListener {
            recheckAccount()
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
                "account_ids" to result.metadata.accounts.map { it.id }
            ))
            .continueWith { task ->
                task.result?.data
            }


    fun recheckAccount() {
        if (registration != null) {
            registration?.remove()
            registration = null
        }

        if (auth.currentUser == null) {
            _accounts.value = listOf()
        } else {
            val uid = auth.currentUser?.uid!!
            fetchAccounts(uid)
            registration = db.collection("items-per-group")
                .document(uid)
                .collection("items")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.d(TAG, "Query failed")
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        _accounts.value = snapshot.documents
                            .map { d -> d.get("institutionName") as String }
                            .map { n -> AccountUi(n) }
                            .toList()
                    }
                }
        }
    }


    private fun fetchAccounts(uid: String) {
        db.collection("items-per-user")
            .document(uid)
            .collection("items")
            .get()
            .addOnCompleteListener {
                it.addOnSuccessListener {
                    _accounts.value = it.documents
                        .map { d -> d.get("institution_name") as String }
                        .map { n -> AccountUi(n) }
                        .toList()
                }
            }
    }


    companion object {
        const val TAG = "AccountsRepository"
    }
}

// TODO: Move to model folder
data class AccountUi(val institutionName: String)
