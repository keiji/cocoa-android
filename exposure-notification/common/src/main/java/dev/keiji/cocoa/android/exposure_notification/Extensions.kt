package dev.keiji.cocoa.android.exposure_notification

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

fun DateTime.toRFC3339Format(): String = ISODateTimeFormat.dateTime().print(this)
