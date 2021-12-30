package dev.keiji.cocoa.android.exposure_notification.ui

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationException
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ReportType
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.TemporaryExposureKey
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ExposureNotificationViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val exposureNotificationWrapper: ExposureNotificationWrapper
) : ViewModel() {
    companion object {
        private const val KEY_STATE_IS_EXPOSURE_NOTIFICATION_EXCEPTION_OCCURRED =
            "is_exposure_notification_exception_occurred"
    }

    val isExposureNotificationExceptionOccurred: LiveData<Boolean>
        get() = state.getLiveData(KEY_STATE_IS_EXPOSURE_NOTIFICATION_EXCEPTION_OCCURRED, true)

    fun start(activity: Activity) {
        Timber.d("Start ExposureNotification.")

        viewModelScope.launch {
            try {
                exposureNotificationWrapper.start(activity)
                state[KEY_STATE_IS_EXPOSURE_NOTIFICATION_EXCEPTION_OCCURRED] = false
            } catch (exception: ExposureNotificationException) {
                state[KEY_STATE_IS_EXPOSURE_NOTIFICATION_EXCEPTION_OCCURRED] = true
            }
        }
    }

    fun stop(activity: Activity) {
        Timber.d("Stop ExposureNotification.")

        viewModelScope.launch {
            try {
                exposureNotificationWrapper.stop(activity)
                state[KEY_STATE_IS_EXPOSURE_NOTIFICATION_EXCEPTION_OCCURRED] = false
            } catch (exception: ExposureNotificationException) {
                state[KEY_STATE_IS_EXPOSURE_NOTIFICATION_EXCEPTION_OCCURRED] = true
            }
        }
    }

    private val _temporaryExposureKey = MutableLiveData<List<TemporaryExposureKey>?>()
    val temporaryExposureKey: LiveData<List<TemporaryExposureKey>?>
        get() = _temporaryExposureKey

    private var reportType: Int = ReportType.CONFIRMED_TEST.ordinal

    fun getTemporaryExposureKeyHistory(activity: Activity) =
        getTemporaryExposureKeyHistory(activity, reportType)

    fun getTemporaryExposureKeyHistory(activity: Activity, reportType: Int) {
        Timber.d("Get TemporaryExposureKeyHistory.")

        this.reportType = reportType

        viewModelScope.launch {
            try {
                _temporaryExposureKey.value =
                    exposureNotificationWrapper.getTemporaryExposureKeyHistory(activity)
            } catch (exception: ExposureNotificationException) {
                // Do nothing
            }
        }
    }
}
