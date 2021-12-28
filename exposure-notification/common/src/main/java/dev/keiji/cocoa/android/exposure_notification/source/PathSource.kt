package dev.keiji.cocoa.android.exposure_notification.source

import android.content.Context
import java.io.File

interface PathSource {
    fun exposureConfigurationDir(): File
    fun diagnosisKeysFileDir(): File
}

class PathSourceImpl(
    private val applicationContext: Context,
) : PathSource {
    companion object {
        private const val DIR_NAME_CONFIGURATION = "configuration"
        private const val DIR_NAME_DIAGNOSIS_KEYS = "diagnosis_keys"
    }

    override fun exposureConfigurationDir() =
        File(applicationContext.filesDir, DIR_NAME_CONFIGURATION)

    override fun diagnosisKeysFileDir(): File =
        File(applicationContext.filesDir, DIR_NAME_DIAGNOSIS_KEYS)
}
