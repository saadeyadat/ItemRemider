package com.example.itemreminder.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allLists")
data class Lists(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "image") var image: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}