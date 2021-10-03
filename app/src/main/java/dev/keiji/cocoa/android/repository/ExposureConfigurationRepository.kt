package dev.keiji.cocoa.android.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.api.ExposureConfigurationProvideServiceApi
import dev.keiji.cocoa.android.entity.ExposureConfiguration
import timber.log.Timber
import javax.inject.Singleton

class ExposureConfigurationRepository(
    private val exposureConfigurationProvideServiceApi: ExposureConfigurationProvideServiceApi
) {
    suspend fun getExposureConfiguration(slot: String): ExposureConfiguration {
        val exposureConfiguration = exposureConfigurationProvideServiceApi.getConfiguration(slot)
        if (exposureConfiguration == null) {
            Timber.w("ExposureConfiguration is null")
            return ExposureConfiguration()
        }
        return exposureConfiguration
    }

    suspend fun getExposureConfiguration(): ExposureConfiguration {
        val exposureConfiguration = exposureConfigurationProvideServiceApi.getConfiguration()
        if (exposureConfiguration == null) {
            Timber.w("ExposureConfiguration is null")
            return ExposureConfiguration()
        }
        return exposureConfiguration
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ExposureConfigurationRepositoryModule {

    @Singleton
    @Provides
    fun provideExposureConfigurationRepository(
        exposureConfigurationProvideServiceApi: ExposureConfigurationProvideServiceApi
    ): ExposureConfigurationRepository {
        return ExposureConfigurationRepository(exposureConfigurationProvideServiceApi)
    }
}
