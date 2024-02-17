package com.example.neurosmg.data.local.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverter {
    @TypeConverter
    fun fromDataString(value: String): List<List<String>> {
        val listType = object : TypeToken<List<List<String>>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toDataString(value: List<List<String>>): String {
        return Gson().toJson(value)
    }
}