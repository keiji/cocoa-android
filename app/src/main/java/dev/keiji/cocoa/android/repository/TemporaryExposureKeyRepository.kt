package dev.keiji.cocoa.android.repository

import android.content.Context
import com.google.common.io.BaseEncoding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.keiji.cocoa.android.toEnTimeWindow
import dev.keiji.cocoa.android.entity.TemporaryExposureKey
import java.util.*

class TemporaryExposureKeyRepository {

    fun getTemporaryExposureKeyList(): List<TemporaryExposureKey> {

        val random = Random()
        return listOf(-3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11).map {
            TemporaryExposureKey(
                key = generateRandomKey(random),
                reportType = 1,
                rollingPeriod = 144,
                rollingStartNumber = getRollingStartNumber(it),
            )
        }
    }

}

private const val KEY_LENGTH = 16

private fun generateRandomKey(random: Random): String {
    val keyBytes = ByteArray(KEY_LENGTH);
    random.nextBytes(keyBytes)
    return BaseEncoding.base64().encode(keyBytes)
}

private fun getRollingStartNumber(offsetDays: Int) =
    Calendar.getInstance(Locale.getDefault()).also {
        it.add(Calendar.DAY_OF_MONTH, offsetDays)
    }.time.toEnTimeWindow()

@Module
@InstallIn(ViewModelComponent::class)
object TemporaryExposureKeyRepositoryModule {

    @Provides
    fun provideTemporaryExposureKeyRepository(
        @ApplicationContext applicationContext: Context
    ): TemporaryExposureKeyRepository {
        return TemporaryExposureKeyRepository();
    }
}
