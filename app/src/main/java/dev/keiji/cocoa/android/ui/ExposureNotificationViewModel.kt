package dev.keiji.cocoa.android.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keiji.cocoa.android.exposure_notiffication.ExposureNotificationWrapper
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ExposureNotificationViewModel @Inject constructor(
    private val exposureNotificationWrapper: ExposureNotificationWrapper
) : ViewModel() {

    fun start(activity: Activity) {
        Timber.d("Start ExposureNotification.")

        viewModelScope.launch {
            exposureNotificationWrapper.start(activity)
        }
    }
}
