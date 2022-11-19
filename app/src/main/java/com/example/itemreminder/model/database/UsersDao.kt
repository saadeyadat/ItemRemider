package com.example.itemreminder.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.itemreminder.model.Item
import com.example.itemreminder.model.User

@Dao
interface UsersDao {

    @Insert
    fun addUser(user: User)

    @Update
    fun updateUser(user: User)

    @Query("Select * from usersTable")
    fun getAllUsers(): LiveData<List<User>>
}