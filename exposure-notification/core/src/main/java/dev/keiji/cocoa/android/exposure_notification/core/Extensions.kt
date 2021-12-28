package dev.keiji.cocoa.android.exposure_notification.core

import dev.keiji.cocoa.android.exposure_notification.core.entity.ExposureNotificationStatus
import java.util.*
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationStatus as NativeExposureNotificationStatus

private const val TIME_WINDOW_INTERVAL: Long = 60 * 10

fun Date.toEnTimeWindow(): Long {
    return this.time / 1000 / TIME_WINDOW_INTERVAL
}

fun NativeExposureNotificationStatus.toExposureNotificationStatus(): ExposureNotificationStatus {
    return when (this) {
        NativeExposureNotificationStatus.ACTIVATED -> ExposureNotificationStatus.ACTIVATED
        NativeExposureNotificationStatus.INACTIVATED -> ExposureNotificationStatus.INACTIVATED
        NativeExposureNotificationStatus.BLUETOOTH_DISABLED -> ExposureNotificationStatus.BLUETOOTH_DISABLED
        NativeExposureNotificationStatus.LOCATION_DISABLED -> ExposureNotificationStatus.LOCATION_DISABLED
        NativeExposureNotificationStatus.NO_CONSENT -> ExposureNotificationStatus.NO_CONSENT
        NativeExposureNotificationStatus.NOT_IN_ALLOWLIST -> ExposureNotificationStatus.NOT_IN_ALLOWLIST
        NativeExposureNotificationStatus.BLUETOOTH_SUPPORT_UNKNOWN -> ExposureNotificationStatus.BLUETOOTH_SUPPORT_UNKNOWN
        NativeExposureNotificationStatus.HW_NOT_SUPPORT -> ExposureNotificationStatus.HW_NOT_SUPPORT
        NativeExposureNotificationStatus.FOCUS_LOST -> ExposureNotificationStatus.FOCUS_LOST
        NativeExposureNotificationStatus.LOW_STORAGE -> ExposureNotificationStatus.LOW_STORAGE
        NativeExposureNotificationStatus.EN_NOT_SUPPORT -> ExposureNotificationStatus.EN_NOT_SUPPORT
        NativeExposureNotificationStatus.USER_PROFILE_NOT_SUPPORT -> ExposureNotificationStatus.USER_PROFILE_NOT_SUPPORT
        else -> ExposureNotificationStatus.UNKNOWN
    }
}
