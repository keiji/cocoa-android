package dev.keiji.cocoa.android.exposure_notification.cappuccino

class ExposureNotificationException(
    val code: Code,
    message: String?
) : Exception(message) {

    enum class Code {
        ApiNotConnected,
        ResolutionRequired,
        NoKeyFileIsIncluded,
        Unknown
    }
}
