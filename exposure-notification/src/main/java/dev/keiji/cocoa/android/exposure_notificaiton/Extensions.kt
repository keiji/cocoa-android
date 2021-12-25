package dev.keiji.cocoa.android

import java.util.*


private const val TIME_WINDOW_INTERVAL: Long = 60 * 10

fun Date.toEnTimeWindow(): Long {
    return this.time / 1000 / TIME_WINDOW_INTERVAL
}
