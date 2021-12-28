package dev.keiji.cocoa.android.exposure_notification.source


interface ConfigurationSource {
    fun isEnabledExposureWindowMode(): Boolean

    fun regions(): List<String>
    fun subregions(): List<String>

    fun submitDiagnosisApiEndpoint(): String
    fun diagnosisKeysApiEndpoint(): String
    fun exposureDataCollectionApiEndpoint(): String

    fun exposureConfigurationUrl(): String
}

class ConfigurationSourceImpl(
    private val isEnabledExposureWindowMode: Boolean,
    regionsStr: String,
    subregionsStr: String,
    private val submitDiagnosisApiEndpoint: String,
    private val diagnosisKeysApiEndpoint: String,
    private val exposureDataCollectionApiEndpoint: String,
    private val exposureConfigurationUrl: String,
) : ConfigurationSource {

    private val regions = regionsStr
        .replace(" ", "")
        .split(",")
        .filter { it.isNotEmpty() }

    private val subregions = subregionsStr
        .replace(" ", "")
        .split(",")
        .filter { it.isNotEmpty() }

    override fun isEnabledExposureWindowMode(): Boolean = isEnabledExposureWindowMode

    override fun regions(): List<String> = regions

    override fun subregions(): List<String> = subregions

    override fun submitDiagnosisApiEndpoint(): String = submitDiagnosisApiEndpoint
    override fun diagnosisKeysApiEndpoint(): String = diagnosisKeysApiEndpoint
    override fun exposureDataCollectionApiEndpoint(): String = exposureDataCollectionApiEndpoint
    override fun exposureConfigurationUrl(): String = exposureConfigurationUrl

}