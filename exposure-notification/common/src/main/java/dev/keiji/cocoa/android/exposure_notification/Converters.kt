package dev.keiji.cocoa.android.exposure_notification

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromString(value: String?): IntArray? {
        value ?: return null
        return Json.decodeFromString<IntArray>(value)
    }

    @TypeConverter
    fun fromArrayList(intArray: IntArray?): String? {
        intArray ?: return null
        return Json.encodeToString(intArray)
    }

}
