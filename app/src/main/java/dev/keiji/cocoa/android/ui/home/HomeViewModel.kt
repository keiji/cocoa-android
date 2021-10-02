package dev.keiji.cocoa.android.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keiji.cocoa.android.exposure_notiffication.ExposureNotificationWrapper
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val exposureNotificationWrapper: ExposureNotificationWrapper
) : ViewModel() {
}
