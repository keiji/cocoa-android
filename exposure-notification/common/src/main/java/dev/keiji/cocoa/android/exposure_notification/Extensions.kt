package dev.keiji.cocoa.android.exposure_notification

import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureWindow
import dev.keiji.util.Base64
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.security.MessageDigest

fun DateTime.toRFC3339Format(): String = ISODateTimeFormat.dateTime().print(this)

fun ExposureWindow.clearText(): String {
    return arrayOf(
        "$calibrationConfidence",
        "$dateMillisSinceEpoch",
        "$infectiousness",
        "$reportType",
    ).joinToString(",")
}

fun ExposureWindow.uniqueKey(): String {
    val sha256 = MessageDigest.getInstance("SHA-256")
    return Base64.encode(sha256.digest(clearText().encodeToByteArray()))
}
