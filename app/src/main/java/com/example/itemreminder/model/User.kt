package com.example.itemreminder.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.itemreminder.view.activities.ItemsActivity

@Entity(tableName = "usersTable")
data class User (
    @PrimaryKey
    @ColumnInfo(name = "email")
    var email: String,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "lists")
    var lists: String? = null,
    @ColumnInfo(name = "image")
    var image: String? = null
)
{
    //constructor(): this("", "", ItemsList())
}