package dev.keiji.cocoa.android.exposure_notification

import dev.keiji.cocoa.android.exposure_notificaiton.BuildConfig.REGION_IDs
import dev.keiji.cocoa.android.exposure_notificaiton.BuildConfig.SUBREGION_IDs

fun regions(): List<String> {
    return REGION_IDs
        .replace(" ", "")
        .split(",")
        .filter { it.isNotEmpty() }
}

fun subregions(): List<String> {
    return SUBREGION_IDs
        .replace(" ", "")
        .split(",")
        .filter { it.isNotEmpty() }
}
