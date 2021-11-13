package me.enzopellegrini.transactionsummary.data

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.plaid.link.result.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class AccountsRepository @Inject constructor() {
    private val accounts: MutableList<LinkAccount> = mutableListOf() // Don't necessarily need this
    private val tokens: MutableList<String> = mutableListOf()

    private val db = Firebase.firestore
    private val functions: FirebaseFunctions by lazy { Firebase.functions }


    fun getLinkToken(): Task<Map<String, String>> {
        return functions.getHttpsCallable("getLinkToken")
            .call()
            .continueWith { task ->

                task.result?.data as Map<String, String>
            }
    }

    fun registerLinkResult(result: LinkSuccess) {
        tokens.add(result.publicToken)
        accounts.addAll(result.metadata.accounts)
    }

    companion object {
        const val TAG = "AccountsRepository"
    }
}