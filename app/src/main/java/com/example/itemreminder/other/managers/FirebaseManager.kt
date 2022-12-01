package com.example.itemreminder.other.managers

import android.content.Context
import android.net.Uri
import com.example.itemreminder.model.Item
import com.example.itemreminder.model.Lists
import com.example.itemreminder.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseManager private constructor(val context: Context) {

    private val fireStore = Firebase.firestore
    private val fbStorage = FirebaseStorage.getInstance()

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

    fun addUserImage(uri: Uri, currentUser: User) {
        fbStorage.reference.child(currentUser.email).putFile(uri)
    }

    fun getUserImage(currentUser: User): StorageReference {
        return fbStorage.reference.child(currentUser.email)
    }

    //------------- Lists Functions -------------//
    fun addList(list: Lists) {
        fireStore.collection("Lists").document(list.name.split("-")[1]).set(list)
    }

    fun deleteList(list: Lists) {
        fireStore.collection("Lists").document(list.name.split("-")[1]).delete()
    }

    fun updateListParticipants(list: Lists) {
        fireStore.collection("Lists").document(list.name.split("-")[1]).update("participants", list.participants)
    }

    //------------- Item Functions -------------//
    fun addItem(item: Item) {
        fireStore.collection("Items").document(item.name).set(item)
    }

    fun deleteItem(item: Item) {
        fireStore.collection("Items").document(item.name).delete()
    }

    fun updateItemImage(item: Item) {
        fireStore.collection("Items").document(item.name).update("image", item.image)
    }

    fun updateItemInfo(item: Item) {
        fireStore.collection("Items").document(item.name).update("info", item.info)
    }
}