package dev.keiji.cocoa.android.exposure_notiffication

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.nearby.Nearby
import kotlinx.coroutines.tasks.await

class ExposureNotificationWrapper(applicationContext: Context) {

    companion object {
        private val TAG = ExposureNotificationWrapper::class.java.simpleName
        const val REQUEST_EXPOSURE_NOTIFICATION_START = 0x01
    }

    private val exposureNotificationClient =
        Nearby.getExposureNotificationClient(applicationContext)

    suspend fun start(activity: Activity) {
        try {
            exposureNotificationClient.start().await()
        } catch (exception: ApiException) {
            Log.d(TAG, "${exception.status}")

            if (exception.status.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                exception.status.startResolutionForResult(
                    activity,
                    REQUEST_EXPOSURE_NOTIFICATION_START
                )
            }
        }
    }


}
