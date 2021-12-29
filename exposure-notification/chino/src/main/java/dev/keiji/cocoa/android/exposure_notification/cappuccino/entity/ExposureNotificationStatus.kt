package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

enum class ExposureNotificationStatus {
    ACTIVATED,
    INACTIVATED,
    BLUETOOTH_DISABLED,
    LOCATION_DISABLED,
    NO_CONSENT,
    NOT_IN_ALLOWLIST,
    BLUETOOTH_SUPPORT_UNKNOWN,
    HW_NOT_SUPPORT,
    FOCUS_LOST,
    LOW_STORAGE,
    UNKNOWN,
    EN_NOT_SUPPORT,
    USER_PROFILE_NOT_SUPPORT;
}