package com.example.itemreminder.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.itemreminder.model.Lists

@Dao
interface ListsDao {

    @Insert
    fun addList(list: Lists)

    @Query("Select * from allLists")
    fun getAllLists(): LiveData<List<Lists>>
}