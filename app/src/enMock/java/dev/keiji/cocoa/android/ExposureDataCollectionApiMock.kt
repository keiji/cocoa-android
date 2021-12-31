package dev.keiji.cocoa.android

import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureConfiguration
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureSummary
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataCollectionApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataRequest
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataResponse

class ExposureDataCollectionApiMock : ExposureDataCollectionApi {
    override suspend fun submit(
        region: String,
        exposureDataRequest: ExposureDataRequest
    ): ExposureDataResponse = ExposureDataResponse(
        "",
        "",
        ExposureConfiguration(),
        ExposureSummary(IntArray(0), 0, 0, 0, 0),
        emptyList(),
        emptyList(),
        emptyList(),
        "",
        ""
    )
}