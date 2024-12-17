package com.example.dreamsync.data.services

import android.util.Log
import com.example.dreamsync.data.models.Account
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccountService {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val accountsRef: DatabaseReference = database.getReference("accounts")

    fun saveAccount(account: Account, onAccountSaved: (Boolean) -> Unit) {
        accountsRef.push().setValue(account).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("AccountService", "Account written successfully.")
                onAccountSaved(true)
            } else {
                Log.e("AccountService", "Failed to write account.", task.exception)
                onAccountSaved(false)
            }
        }
    }

    fun getAccountByEmail(email: String, onAccountFetched: (Account?) -> Unit) {
        accountsRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val account = snapshot.children.firstOrNull()?.getValue(Account::class.java)
                    onAccountFetched(account)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AccountService", "Failed to fetch account: $email", error.toException())
                    onAccountFetched(null)
                }
            }
        )
    }
}