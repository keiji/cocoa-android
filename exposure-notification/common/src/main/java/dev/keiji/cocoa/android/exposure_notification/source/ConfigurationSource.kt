package dev.keiji.cocoa.android.exposure_notification.source

import timber.log.Timber
import java.util.*

interface ConfigurationSource {
    fun isEnabledExposureWindowMode(): Boolean

    val regions: List<Int>
    val subregions: List<String>

    val submitDiagnosisApiEndpoint: String
    val diagnosisKeysApiEndpoint: String
    val exposureDataCollectionApiEndpoint: String

    val exposureConfigurationUrl: String
}

class ConfigurationSourceImpl(
    private val isEnabledExposureWindowMode: Boolean,
    regionsStr: String,
    subregionsStr: String,
    override val submitDiagnosisApiEndpoint: String,
    override val diagnosisKeysApiEndpoint: String,
    override val exposureDataCollectionApiEndpoint: String,
    override val exposureConfigurationUrl: String,
) : ConfigurationSource {

    private val regionsStrArray = regionsStr
        .replace(" ", "")
        .split(",")
        .filter { it.isNotEmpty() }

    private val subregionsArray = subregionsStr
        .replace(" ", "")
        .split(",")
        .filter { it.isNotEmpty() }

    override val regions: List<Int> =
        regionsStrArray.map { regionStr ->
            val regionInt = regionStr.toIntOrNull()
            if (regionInt == null) {
                Timber.e("Region must be Int: $regionStr")
            }
            return@map regionInt
        }.filterNotNull()

    override val subregions = subregionsArray

    override fun isEnabledExposureWindowMode(): Boolean = isEnabledExposureWindowMode

    init {
        Timber.d("isEnabledExposureWindowMode: $isEnabledExposureWindowMode")

        Timber.d("diagnosisKeysApiEndpoint: $diagnosisKeysApiEndpoint")
        Timber.d("exposureConfigurationUrl: $exposureConfigurationUrl")
        Timber.d("regions: $regions")
        Timber.d("subregions: $subregions")

        Timber.d("submitDiagnosisApiEndpoint: $submitDiagnosisApiEndpoint")
        Timber.d("exposureDataCollectionApiEndpoint: $exposureDataCollectionApiEndpoint")
    }
}
