package dev.keiji.cocoa.android.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.ExposureNotificationWrapperMock
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.ExposureDetectionService
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.common.source.DateTimeSource

@Module
@InstallIn(SingletonComponent::class)
object ExposureNotificationModule {

    @Provides
    fun provideExposureNotificationWrapper(
        @ApplicationContext applicationContext: Context,
        pathSource: PathSource,
        dateTimeSource: DateTimeSource,
        exposureDetectionService: ExposureDetectionService,
    ): ExposureNotificationWrapper {
        return ExposureNotificationWrapperMock(
            applicationContext,
            dateTimeSource,
            pathSource,
            exposureDetectionService
        )
    }
}
