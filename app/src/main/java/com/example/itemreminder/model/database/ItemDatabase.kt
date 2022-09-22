package com.example.itemreminder.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.itemreminder.model.Item

@Database(entities = arrayOf(Item::class), version = 1)
abstract class ItemDatabase: RoomDatabase() {

    abstract fun getFruitDao(): ItemsDao

    companion object {
        fun getDatabase(context: Context?): ItemDatabase {
            return Room.databaseBuilder(
                context!!.applicationContext,
                ItemDatabase::class.java,
                "fruit_database"
            ).build()
        }
    }
}