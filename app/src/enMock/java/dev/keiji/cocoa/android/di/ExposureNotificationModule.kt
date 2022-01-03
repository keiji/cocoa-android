package dev.keiji.cocoa.android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.ExposureNotificationWrapperMock
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.common.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.ExposureResultService

@Module
@InstallIn(SingletonComponent::class)
object ExposureNotificationModule {

    @Provides
    fun provideExposureNotificationWrapper(
        pathSource: PathSource,
        dateTimeSource: DateTimeSource,
        exposureResultService: ExposureResultService
    ): ExposureNotificationWrapper {
        return ExposureNotificationWrapperMock(
            dateTimeSource,
            pathSource,
            exposureResultService
        )
    }
}
