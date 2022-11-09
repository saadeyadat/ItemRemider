package com.example.itemreminder.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {

    @TypeConverter
    fun itemsObjectToJson(itemsList: ItemsList): String {
        val gson = Gson()
        val type = object : TypeToken<ItemsList>() {}.type
        return gson.toJson(itemsList, type)
    }

    @TypeConverter
    fun jsonToItemsObject(itemsList: String): ItemsList {
        val gson = Gson()
        val type = object  : TypeToken<ItemsList>() {}.type
        return gson.fromJson(itemsList, type)
    }
}