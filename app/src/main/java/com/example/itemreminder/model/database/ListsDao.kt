package com.example.itemreminder.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.itemreminder.model.Item
import com.example.itemreminder.model.Lists

@Dao
interface ListsDao {

    @Insert
    fun addList(list: Lists)

    @Delete
    fun deleteList(list: Lists)

    @Query("Select * from allLists")
    fun getAllLists(): LiveData<List<Lists>>
}