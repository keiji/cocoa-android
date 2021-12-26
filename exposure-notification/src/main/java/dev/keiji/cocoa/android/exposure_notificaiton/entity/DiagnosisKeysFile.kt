package dev.keiji.cocoa.android.exposure_notificaiton.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class DiagnosisKeysEntry(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "region") val region: Int,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "created") val created: Long,
)