package dev.keiji.cocoa.android.exposure_notification

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.keiji.cocoa.android.exposure_notification.dao.DailySummaryDao
import dev.keiji.cocoa.android.exposure_notification.dao.DiagnosisKeysFileDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureDataDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureInformationDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureSummaryDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureWindowDao
import dev.keiji.cocoa.android.exposure_notification.model.DailySummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.DiagnosisKeysFileModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataBaseModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureInformationModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureSummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureWindowModel
import dev.keiji.cocoa.android.exposure_notification.model.ScanInstanceModel

@Database(
    entities = [
        DiagnosisKeysFileModel::class,

        ExposureDataBaseModel::class,
        ExposureWindowModel::class, ScanInstanceModel::class,
        DailySummaryModel::class,

        ExposureSummaryModel::class,
        ExposureInformationModel::class,
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diagnosisKeysFileDao(): DiagnosisKeysFileDao

    abstract fun exposureDataDao(): ExposureDataDao

    abstract fun dailySummaryDao(): DailySummaryDao
    abstract fun exposureWindowDao(): ExposureWindowDao

    abstract fun exposureInformationDao(): ExposureInformationDao
    abstract fun exposureSummaryDao(): ExposureSummaryDao
}
