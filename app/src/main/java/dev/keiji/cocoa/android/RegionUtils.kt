package dev.keiji.cocoa.android

internal fun regions(): List<String> {
    return BuildConfig.REGION_IDs
        .replace(" ", "")
        .split(",")
        .filter { it.isNotEmpty() }
}

internal fun subregions(): List<String> {
    return BuildConfig.SUBREGION_IDs
        .replace(" ", "")
        .split(",")
        .filter { it.isNotEmpty() }
}
