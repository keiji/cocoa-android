package dev.keiji.cocoa.android.exposure_notification.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DiagnosisKeysFile(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "region") val region: String,
    @ColumnInfo(name = "subregion") val subregion: String?,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "created") val created: Long,
    @ColumnInfo(name = "is_processed") var isProcessed: Boolean = false,
    @ColumnInfo(name = "is_listed") var isListed: Boolean,
)
