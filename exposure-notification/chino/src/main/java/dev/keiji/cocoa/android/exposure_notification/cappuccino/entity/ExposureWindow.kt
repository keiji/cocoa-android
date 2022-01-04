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
) : Comparable<ExposureWindow> {
    constructor(
        exposureWindow: NativeExposureWindow
    ) : this(
        calibrationConfidence = exposureWindow.calibrationConfidence,
        dateMillisSinceEpoch = exposureWindow.dateMillisSinceEpoch,
        infectiousness = exposureWindow.infectiousness,
        reportType = exposureWindow.reportType,
        scanInstances = exposureWindow.scanInstances.map { si -> ScanInstance(si) }
    )

    override fun compareTo(other: ExposureWindow): Int {
        return when {
            dateMillisSinceEpoch < other.dateMillisSinceEpoch -> -1
            dateMillisSinceEpoch > other.dateMillisSinceEpoch -> 1
            reportType < other.reportType -> -1
            reportType > other.reportType -> 1
            infectiousness < other.infectiousness -> +1
            infectiousness > other.infectiousness -> -1
            calibrationConfidence < other.calibrationConfidence -> +1
            calibrationConfidence > other.calibrationConfidence -> -1
            else -> 0
        }
    }
}

@Serializable
data class ScanInstance(
    @SerialName("MinAttenuationDb") val minAttenuationDb: Int,
    @SerialName("SecondsSinceLastScan") val secondsSinceLastScan: Int,
    @SerialName("TypicalAttenuationDb") val typicalAttenuationDb: Int,
) : Comparable<ScanInstance> {
    constructor(
        scanInstance: NativeScanInstance
    ) : this(
        minAttenuationDb = scanInstance.minAttenuationDb,
        secondsSinceLastScan = scanInstance.secondsSinceLastScan,
        typicalAttenuationDb = scanInstance.typicalAttenuationDb,
    )

    override fun compareTo(other: ScanInstance): Int {
        return when {
            minAttenuationDb < other.minAttenuationDb -> +1
            minAttenuationDb > other.minAttenuationDb -> -1
            secondsSinceLastScan < other.secondsSinceLastScan -> +1
            secondsSinceLastScan > other.secondsSinceLastScan -> -1
            typicalAttenuationDb < other.typicalAttenuationDb -> +1
            typicalAttenuationDb > other.typicalAttenuationDb -> -1
            else -> 0
        }
    }
}
