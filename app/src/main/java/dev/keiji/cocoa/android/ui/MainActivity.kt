package dev.keiji.cocoa.android.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import dev.keiji.cocoa.android.R
import dev.keiji.cocoa.android.exposure_notification.core.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.ui.ExposureNotificationViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: ExposureNotificationViewModel by viewModels()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }

        when (requestCode) {
            ExposureNotificationWrapper.REQUEST_EXPOSURE_NOTIFICATION_START -> {
                viewModel.start(this)
            }
            ExposureNotificationWrapper.REQUEST_TEMPORARY_EXPOSURE_KEY_HISTORY -> {
                viewModel.getTemporaryExposureKeyHistory(this)
            }
        }
    }
}
