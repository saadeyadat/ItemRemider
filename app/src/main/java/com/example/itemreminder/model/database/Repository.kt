package com.example.itemreminder.model.database

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.itemreminder.model.Lists
import com.example.itemreminder.model.Item
import com.example.itemreminder.model.User

class Repository private constructor(application: Context?) {

    private val itemDao = AppDatabase.getDatabase(application).getItemDao()
    private val userDao = AppDatabase.getDatabase(application).getUserDao()
    private val listsDao = AppDatabase.getDatabase(application).getListsDao()

    companion object {
        private lateinit var instance: Repository
        fun getInstance(application: Context?): Repository {
            if (!::instance.isInitialized)
                instance = Repository(application)
            return instance
        }
    }

    //------------- User Functions -------------//
    fun addUser(user: User) {
        userDao.addUser(user)
    }

    fun addUserList(user: User, list: String) {
        user.lists += "-$list"
        updateUser(user)
    }

    fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    fun getAllUsers(): LiveData<kotlin.collections.List<User>> {
        return userDao.getAllUsers()
    }

    //------------- Lists Functions -------------//
    fun addList(list: Lists) {
        listsDao.addList(list)
    }

    fun deleteList(list: Lists) {
        listsDao.deleteList(list)
    }

    fun updateList(list: Lists) {
        listsDao.updateList(list)
    }

    fun getAllLists(): LiveData<List<Lists>> {
        return listsDao.getAllLists()
    }

    fun addParticipant(list: Lists, participant: String) {
        list.participants += " - $participant"
        updateList(list)
    }

    //------------- Item Functions -------------//
    fun addItem(item: Item) {
        itemDao.addItem(item)
    }

    fun deleteItem(item: Item) {
        itemDao.deleteItem(item)
    }

    private fun updateItem(item: Item) {
        itemDao.updateItem(item)
    }

    fun updateItemInfo(item: Item, info: String) {
        item.info = info
        updateItem(item)
    }

    fun updateItemImage(item: Item, image: String) {
        item.image = image
        updateItem(item)
    }

    fun getLiveDataAllItems(): LiveData<kotlin.collections.List<Item>> {
        return itemDao.getAllItems()
    }
}