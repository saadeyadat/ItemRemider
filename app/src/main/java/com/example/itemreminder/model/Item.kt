package com.example.itemreminder.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fruitsTable")
data class Item(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "image") var image: String? = null,
    @ColumnInfo(name = "info") var info: String,
    @ColumnInfo(name = "timesTamp") var timesTamp: Long = System.currentTimeMillis()
    ){
    @PrimaryKey(autoGenerate = true)
    var id = 0
}