package com.example.itemreminder.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usersTable")
data class User (
    @PrimaryKey
    @ColumnInfo(name = "email")
    var email: String,
    @ColumnInfo(name = "name")
    var name: String,
    //@ColumnInfo(name = "items")
    //val items: ItemsList,
    @ColumnInfo(name = "image")
    val image: String? = null
)
{
    constructor(): this("", "")
}