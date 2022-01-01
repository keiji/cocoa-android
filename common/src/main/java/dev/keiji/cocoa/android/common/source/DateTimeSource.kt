package dev.keiji.cocoa.android.common.source

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

abstract class DateTimeSource {

    abstract fun utcNow(): DateTime

    fun epoch(): Long = utcNow().millis / 1000

    fun today(): DateTime = utcNow()
        .withHourOfDay(0)
        .withMinuteOfHour(0)
        .withSecondOfMinute(0)
        .withMillisOfSecond(0)

    fun offsetDateTime(offsetDays: Int): DateTime = today().plusDays(offsetDays)
}

class DateTimeSourceImpl : DateTimeSource() {
    override fun utcNow(): DateTime = DateTime(DateTimeZone.UTC)
}
