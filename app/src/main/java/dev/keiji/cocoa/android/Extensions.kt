package dev.keiji.cocoa.android

import java.text.SimpleDateFormat
import java.util.*

private const val TIME_WINDOW_INTERVAL: Long = 60 * 10

private const val RFC3339Format = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"

fun Date.toRFC3339Format(): String =
    SimpleDateFormat(RFC3339Format, Locale.getDefault()).format(this)

fun Date.ToEnTimeWindow(): Long {
    return this.time / 1000 / TIME_WINDOW_INTERVAL
}
