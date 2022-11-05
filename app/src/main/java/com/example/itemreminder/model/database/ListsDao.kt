package com.example.itemreminder.model.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.itemreminder.model.Item
import com.example.itemreminder.model.Lists

@Dao
interface ListsDao {

    @Insert
    fun addList(list: Lists)

    @Delete
    fun deleteList(list: Lists)

    @Update
    fun updateList(list: Lists)

    @Query("Select * from allLists")
    fun getAllLists(): LiveData<List<Lists>>
}