package dev.keiji.cocoa.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import dev.keiji.cocoa.android.exposure_notification.core.ExposureNotificationWrapper
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ExposureNotificationStateReceiver : BroadcastReceiver() {

    @Inject
    lateinit var exposureNotificationWrapper: ExposureNotificationWrapper

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.i("ExposureNotificationStateReceiver.onReceive")

        context ?: return
        intent ?: return

        val isExposureNotificationEnabled: Boolean =
            intent.getBooleanExtra(ExposureNotificationWrapper.EXTRA_SERVICE_STATE, false)
        Timber.i("Service state ${isExposureNotificationEnabled}")

    }
}
