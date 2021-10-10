package dev.keiji.cocoa.android

import dev.keiji.cocoa.android.entity.TemporaryExposureKey
import dev.keiji.util.Base64
import java.text.SimpleDateFormat
import java.util.*

import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey as NativeTemporaryExposureKey

private const val TIME_WINDOW_INTERVAL: Long = 60 * 10


private const val RFC3339Format = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"

fun Date.toRFC3339Format(): String =
    SimpleDateFormat(RFC3339Format, Locale.getDefault()).format(this)

fun Date.toEnTimeWindow(): Long {
    return this.time / 1000 / TIME_WINDOW_INTERVAL
}

fun NativeTemporaryExposureKey.toEntity(reportType: Int): TemporaryExposureKey =
    TemporaryExposureKey(
        key = Base64.encode(keyData),
        rollingStartNumber = rollingStartIntervalNumber,
        rollingPeriod = rollingPeriod,
        reportType = reportType
    )
