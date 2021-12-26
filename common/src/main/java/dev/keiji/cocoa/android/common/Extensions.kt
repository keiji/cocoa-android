package dev.keiji.cocoa.android.common

import java.text.SimpleDateFormat
import java.util.*

private const val RFC3339Format = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"

fun Date.toRFC3339Format(): String =
    SimpleDateFormat(RFC3339Format, Locale.getDefault()).format(this)
