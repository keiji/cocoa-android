package dev.keiji.cocoa.android.exposure_notification

import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureWindow
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ScanInstance
import dev.keiji.util.Base64
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.security.MessageDigest

fun DateTime.toRFC3339Format(): String = ISODateTimeFormat.dateTime().print(this)

fun ScanInstance.toSerializeText(): String {
    return arrayOf(
        "$minAttenuationDb",
        "$secondsSinceLastScan",
        "$typicalAttenuationDb",
    ).joinToString(",")
}

fun ExposureWindow.toSerializeText(): String {
    val scanInstancesText = scanInstances.sorted().joinToString(",") { it.toSerializeText() }
    return arrayOf(
        "$calibrationConfidence",
        "$dateMillisSinceEpoch",
        "$infectiousness",
        "$reportType",
        "($scanInstancesText)",
    ).joinToString(",")
}

fun ExposureWindow.uniqueKey(): String {
    val sha256: MessageDigest = MessageDigest.getInstance("SHA-256")
    return Base64.encode(sha256.digest(toSerializeText().encodeToByteArray()))
}
