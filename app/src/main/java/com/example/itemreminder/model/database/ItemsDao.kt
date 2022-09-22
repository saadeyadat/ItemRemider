package com.example.itemreminder.model.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.itemreminder.model.Item

@Dao
interface ItemsDao {

    @Insert
    fun insertFruit(item: Item)

    @Delete
    fun deleteFruit(item: Item)

    @Query("Select * from fruitsTable")
    fun getAllFruits(): LiveData<List<Item>>

    @Update
    fun updateFruit(item: Item)
}