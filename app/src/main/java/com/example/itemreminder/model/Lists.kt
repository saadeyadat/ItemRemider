package com.example.itemreminder.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "allLists")
data class Lists(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "owner") val owner: String,
    @ColumnInfo(name = "image") var image: String? = null,
    @ColumnInfo(name = "participants") var participants: String? = "Participants: "
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}