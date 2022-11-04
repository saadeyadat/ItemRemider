package com.example.itemreminder.other.managers

import android.content.Context
import com.example.itemreminder.model.Item
import com.example.itemreminder.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseManager private constructor(val context: Context) {

    private val fireStore = Firebase.firestore

    companion object {
        private lateinit var instance: FirebaseManager
        fun getInstance(context: Context): FirebaseManager {
            if (!::instance.isInitialized)
                instance = FirebaseManager(context)
            return instance
        }
    }

    fun addUser(user: User) {
        fireStore.collection("Users").document(user.email!!).set(user)
    }

    fun addItem(item: Item) {
        val email = SharedPrefManager.getInstance(context).getUserEmail()
        fireStore.collection("Users").document(email)
    }
}