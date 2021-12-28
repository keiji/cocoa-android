package dev.keiji.cocoa.android.exposure_notification.core.entity

enum class ReportType {
    UNKNOWN,
    CONFIRMED_TEST,
    CONFIRMED_CLINICAL_DIAGNOSIS,
    SELF_REPORT,
    RECURSIVE,
    REVOKED
}