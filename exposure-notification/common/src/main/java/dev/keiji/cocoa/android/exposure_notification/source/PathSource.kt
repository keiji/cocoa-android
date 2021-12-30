package dev.keiji.cocoa.android.exposure_notification.source

import android.content.Context
import java.io.File

interface PathSource {
    companion object {
        const val FILENAME_DUMMY_EXPOSURE_SUMMARY_DATA = "exposuer_summary.json"
        const val FILENAME_DUMMY_EXPOSURE_INFOS_DATA = "exposure_informations.json"
        const val FILENAME_DUMMY_EXPOSURE_WINDOW_DATA = "exposure_windows.json"
        const val FILENAME_DUMMY_DAILY_SUMMARY_DATA = "daily_summaries.json"
    }

    fun exposureConfigurationDir(): File
    fun exposureConfigurationFile(): File

    fun exposureDataDir(): File
    fun dummyExposureDataDir(): File

    fun diagnosisKeysFileDir(): File
}

class PathSourceImpl(
    private val applicationContext: Context,
) : PathSource {
    companion object {
        private const val DIR_NAME_CONFIGURATION = "configuration"
        private const val FILENAME_CONFIGURATION = "exposure_configuration.json"

        private const val DIR_NAME_EXPOSURE_DATA = "exposure_data"
        private const val DIR_DUMMY_EXPOSURE_DATA = "dummy_exposure_data"

        private const val DIR_NAME_DIAGNOSIS_KEYS = "diagnosis_keys"
    }

    override fun exposureConfigurationDir() =
        File(applicationContext.filesDir, DIR_NAME_CONFIGURATION)

    override fun exposureConfigurationFile() =
        File(exposureConfigurationDir(), FILENAME_CONFIGURATION)

    override fun exposureDataDir() =
        File(applicationContext.filesDir, DIR_NAME_EXPOSURE_DATA)

    override fun dummyExposureDataDir() =
        File(applicationContext.filesDir, DIR_DUMMY_EXPOSURE_DATA)

    override fun diagnosisKeysFileDir(): File =
        File(applicationContext.filesDir, DIR_NAME_DIAGNOSIS_KEYS)
}
