package dev.keiji.cocoa.android.exposure_notification.repository

import dev.keiji.cocoa.android.common.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.DailySummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureWindow
import dev.keiji.cocoa.android.exposure_notification.source.PathSource

interface ExposureDataRepository {
    fun save(dailySummaryList: List<DailySummary>, exposureWindowList: List<ExposureWindow>)
}

class ExposureDataRepositoryImpl(
    pathSource: PathSource,
    dateTimeSource: DateTimeSource,
) : ExposureDataRepository {

    override fun save(
        dailySummaryList: List<DailySummary>,
        exposureWindowList: List<ExposureWindow>
    ) {
        TODO("Not yet implemented")
    }

}
