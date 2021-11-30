package me.enzopellegrini.transactionsummary

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

val functionsInstance: FirebaseFunctions by lazy {
    val i = Firebase.functions
//    i.useEmulator("10.0.2.2", 5001)
    i
}
val authInstance: FirebaseAuth by lazy {
    val i = Firebase.auth
//    i.useEmulator("10.0.2.2", 9099)
    i
}
val authUIInstance by lazy {
    val i = AuthUI.getInstance()
//    i.useEmulator("10.0.2.2", 9099)
    i
}
val firestoreInstance: FirebaseFirestore by lazy {
    val i = FirebaseFirestore.getInstance()
//    i.useEmulator("10.0.2.2", 8080)
    i
}