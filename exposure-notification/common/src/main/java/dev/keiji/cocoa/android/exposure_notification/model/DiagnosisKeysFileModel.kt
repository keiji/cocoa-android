package dev.keiji.cocoa.android.exposure_notification.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diagnosiskeysfile")
data class DiagnosisKeysFileModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "region") val region: String,
    @ColumnInfo(name = "subregion") val subregion: String?,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "created") val created: Long,
    @ColumnInfo(name = "is_processed") var isProcessed: Boolean = false,
    @ColumnInfo(name = "is_listed") var isListed: Boolean,
)
