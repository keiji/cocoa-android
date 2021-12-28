package dev.keiji.cocoa.android.source

import java.util.*

abstract class DateTimeSource {
    abstract fun utcNow(): Calendar

    fun epoch(): Long = utcNow().timeInMillis / 1000

    fun today(): Calendar = utcNow().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    fun offsetDate(offsetDays: Int) = today().apply {
        add(Calendar.DATE, offsetDays)
    }
}

class DateTimeSourceImpl : DateTimeSource() {
    companion object {
        private const val TIMEZONE_ID_UTC = "UTC"
    }

    private val TIMEZONE_UTC = TimeZone.getTimeZone(TIMEZONE_ID_UTC)

    override fun utcNow(): Calendar = Calendar.getInstance(TIMEZONE_UTC)
}
