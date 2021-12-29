package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import com.google.android.gms.nearby.exposurenotification.ExposureWindow as NativeExposureWindow
import com.google.android.gms.nearby.exposurenotification.ScanInstance as NativeScanInstance

@Serializable
data class ExposureWindow(
    @SerialName("CalibrationConfidence") val calibrationConfidence: Int,
    @SerialName("DateMillisSinceEpoch") val dateMillisSinceEpoch: Long,
    @SerialName("Infectiousness") val infectiousness: Int,
    @SerialName("ReportType") val reportType: Int,
    @SerialName("ScanInstances") val scanInstances: List<ScanInstance>,
) {
    constructor(
        exposureWindow: NativeExposureWindow
    ) : this(
        calibrationConfidence = exposureWindow.calibrationConfidence,
        dateMillisSinceEpoch = exposureWindow.dateMillisSinceEpoch,
        infectiousness = exposureWindow.infectiousness,
        reportType = exposureWindow.reportType,
        scanInstances = exposureWindow.scanInstances.map { si -> ScanInstance(si) }
    )
}

@Serializable
data class ScanInstance(
    @SerialName("MinAttenuationDb") val minAttenuationDb: Int,
    @SerialName("SecondsSinceLastScan") val secondsSinceLastScan: Int,
    @SerialName("TypicalAttenuationDb") val typicalAttenuationDb: Int,
) {
    constructor(
        scanInstance: NativeScanInstance
    ) : this(
        minAttenuationDb = scanInstance.minAttenuationDb,
        secondsSinceLastScan = scanInstance.secondsSinceLastScan,
        typicalAttenuationDb = scanInstance.typicalAttenuationDb,
    )
}
