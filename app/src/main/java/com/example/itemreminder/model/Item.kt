package com.example.itemreminder.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fruitsTable")
data class Item(
    @ColumnInfo(name = "list") val list: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "image") var image: String? = null,
    @ColumnInfo(name = "info") var info: String,
    @ColumnInfo(name = "timesTamp") var timesTamp: Long = System.currentTimeMillis()
    ){
    @PrimaryKey(autoGenerate = true)
    var id = 0
}
// adding @ColumnInfo named ID that have the list ID.
// sending the ID when creating the list and opening it by the user.
// when printing the items in the list we check the ID if equal with the clicked list by user.
// this will print only items of the specific chosen list by user by ID and wii stay with only one items database.