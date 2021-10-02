package dev.keiji.cocoa.android.ui.home

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keiji.cocoa.android.exposure_notiffication.ExposureNotificationWrapper
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val exposureNotificationWrapper: ExposureNotificationWrapper
) : ViewModel() {
}
