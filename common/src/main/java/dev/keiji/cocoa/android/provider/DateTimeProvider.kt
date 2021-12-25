package dev.keiji.cocoa.android.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton

abstract class DateTimeProvider {
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

class DateTimeProviderImpl : DateTimeProvider() {
    companion object {
        private const val TIMEZONE_ID_UTC = "UTC"
    }

    private val TIMEZONE_UTC = TimeZone.getTimeZone(TIMEZONE_ID_UTC)

    override fun utcNow(): Calendar = Calendar.getInstance(TIMEZONE_UTC)
}

@Module
@InstallIn(SingletonComponent::class)
object DateTimeProviderModule {

    @Singleton
    @Provides
    fun provideDateTimeProvider(
    ): DateTimeProvider = DateTimeProviderImpl()
}
