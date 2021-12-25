package dev.keiji.cocoa.android.repository

import dev.keiji.cocoa.android.toRFC3339Format
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.*

private const val TIMEZONE_ID_UTC = "UTC"

@RunWith(MockitoJUnitRunner::class)
class DateTimeRepositoryTest {
    private val TIMEZONE_UTC = TimeZone.getTimeZone(TIMEZONE_ID_UTC)

    @Before
    fun setup() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun readStringFromContext_LocalizedString() {

        val now = Calendar.getInstance(TIMEZONE_UTC).apply {
            set(2021, 12, 25, 1, 8, 17)
            set(Calendar.MILLISECOND, 235)
        }
        val today = Calendar.getInstance(TIMEZONE_UTC).apply {
            set(2021, 12, 25, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val yesterday = Calendar.getInstance(TIMEZONE_UTC).apply {
            set(2021, 12, 24, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val mockDateTimeRepository =
            mock<DateTimeRepository> {
                onBlocking { utcNow() } doReturn now
            }

        assertEquals(
            now.timeInMillis / 1000,
            mockDateTimeRepository.epoch()
        )

        assertEquals(
            today.time.toRFC3339Format(),
            mockDateTimeRepository.today().time.toRFC3339Format()
        )

        assertEquals(
            yesterday.time.toRFC3339Format(),
            mockDateTimeRepository.offsetDate(-1).time.toRFC3339Format()
        )
    }
}