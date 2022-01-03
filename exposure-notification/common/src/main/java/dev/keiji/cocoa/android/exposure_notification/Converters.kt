package dev.keiji.cocoa.android.exposure_notification

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromStringToIntArray(value: String?): IntArray? {
        value ?: return null
        return Json.decodeFromString<IntArray>(value)
    }

    @TypeConverter
    fun fromStringIntArray(intArray: IntArray?): String? {
        intArray ?: return null
        return Json.encodeToString(intArray)
    }

    @TypeConverter
    fun fromStringToStringList(value: String?): List<String>? {
        value ?: return null
        return Json.decodeFromString<List<String>>(value)
    }

    @TypeConverter
    fun fromStringList(stringList: List<String>?): String? {
        stringList ?: return null
        return Json.encodeToString(stringList)
    }
}
