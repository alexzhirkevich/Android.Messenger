package com.alexz.messenger.app.data.entities.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

@ProvidedTypeConverter
class GsonTypeConverter<T> {

    private val gson = Gson()

    @TypeConverter
    fun fromJson(string: String): T {
        val mapType = object : TypeToken<T>() {}.type
        return gson.fromJson(string, mapType)
    }

    @TypeConverter
    fun toJson(obj: T): String {
        return gson.toJson(obj)
    }
}