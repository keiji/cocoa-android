package dev.keiji.cocoa.android.exposure_notification.cappuccino

import com.google.android.gms.nearby.exposurenotification.DiagnosisKeyFileProvider as NativeDiagnosisKeyFileProvider
import java.io.File

class DiagnosisKeyFileProvider(private val diagnosisKeyFileList: List<File>) {
    fun toNative(): NativeDiagnosisKeyFileProvider =
        NativeDiagnosisKeyFileProvider(diagnosisKeyFileList)
}
