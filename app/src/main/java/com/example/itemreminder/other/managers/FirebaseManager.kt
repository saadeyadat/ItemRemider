package com.example.itemreminder.other.managers

import android.content.Context
import com.example.itemreminder.model.Item
import com.example.itemreminder.model.Lists
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

    //------------- User Functions -------------//
    fun addUser(user: User) {
        fireStore.collection("Users").document(user.email!!).set(user)
    }

    //------------- Lists Functions -------------//
    fun addList(list: Lists) {
        fireStore.collection("Lists").document(list.name.split("-")[1]).set(list)
    }

    fun deleteList(list: Lists) {
        fireStore.collection("Lists").document(list.name.split("-")[1]).delete()
    }

    //------------- Item Functions -------------//
    fun addItem(item: Item) {
        fireStore.collection("Items").document(item.name).set(item)
    }

    fun deleteItem(item: Item) {
        fireStore.collection("Items").document(item.name).delete()
    }
}