package dev.keiji.cocoa.android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.ExposureDataCollectionApiMock
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataCollectionApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExposureDataCollectionApiModule {

    @Singleton
    @Provides
    fun provideExposureDataCollectionApi(
    ): ExposureDataCollectionApi = ExposureDataCollectionApiMock()
}
