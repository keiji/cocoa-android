package dev.keiji.cocoa.android.ui

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.nearby.exposurenotification.ReportType.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keiji.cocoa.android.entity.TemporaryExposureKey
import dev.keiji.cocoa.android.ExposureNotificationWrapper
import dev.keiji.cocoa.android.toEntity
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

    fun stop(activity: Activity) {
        Timber.d("Stop ExposureNotification.")

        viewModelScope.launch {
            exposureNotificationWrapper.stop(activity)
        }
    }

    private val _temporaryExposureKey = MutableLiveData<List<TemporaryExposureKey>?>()
    val temporaryExposureKey: LiveData<List<TemporaryExposureKey>?>
        get() = _temporaryExposureKey

    private var reportType: Int = CONFIRMED_TEST

    fun getTemporaryExposureKeyHistory(activity: Activity) =
        getTemporaryExposureKeyHistory(activity, reportType)

    fun getTemporaryExposureKeyHistory(activity: Activity, reportType: Int) {
        Timber.d("Get TemporaryExposureKeyHistory.")

        this.reportType = reportType

        viewModelScope.launch {
            _temporaryExposureKey.value =
                exposureNotificationWrapper.getTemporaryExposureKeyHistory(activity)
                    ?.map { it.toEntity(reportType) }
        }
    }
}
