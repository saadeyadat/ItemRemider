package com.example.itemreminder.model.database

import androidx.room.Dao
import androidx.room.Insert
import com.example.itemreminder.model.User

@Dao
interface UsersDao {

    @Insert
    fun addUser(user: User)
}