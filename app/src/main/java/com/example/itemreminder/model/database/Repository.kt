package com.example.itemreminder.model.database

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.itemreminder.model.Item

class Repository private constructor(application: Context?) {

    private val fruitDao = ItemDatabase.getDatabase(application).getFruitDao()

    companion object {
        private lateinit var instance: Repository
        fun getInstance(application: Context?): Repository {
            if (!::instance.isInitialized)
                instance = Repository(application)
            return instance
        }
    }

    fun addItem(item: Item) {
        fruitDao.insertFruit(item)
    }

    fun deleteItem(item: Item) {
        fruitDao.deleteFruit(item)
    }

    private fun updateItem(item: Item) {
        fruitDao.updateFruit(item)
    }

    fun updateItemInfo(item: Item, info: String) {
        item.info = info
        updateItem(item)
    }

    fun updateItemImage(item: Item, image: String) {
        item.image = image
        updateItem(item)
    }

    fun getLiveDataAllItems(): LiveData<List<Item>> {
        return fruitDao.getAllFruits()
    }
}