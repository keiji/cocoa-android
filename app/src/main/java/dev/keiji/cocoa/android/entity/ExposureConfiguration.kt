package dev.keiji.cocoa.android.entity

import com.google.android.gms.nearby.exposurenotification.DiagnosisKeysDataMapping
import com.google.android.gms.nearby.exposurenotification.Infectiousness
import com.google.android.gms.nearby.exposurenotification.ReportType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.google.android.gms.nearby.exposurenotification.DailySummariesConfig as NativeDailySummariesConfig
import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration as NativeExposureConfiguration

@Serializable
data class ExposureConfiguration(
    @SerialName("google_exposure_config")
    val v1Config: V1Config = V1Config(),

    @SerialName("google_diagnosis_keys_data_mapping_config")
    val diagnosisKeysDataMappingConfig: DiagnosisKeysDataMappingConfig = DiagnosisKeysDataMappingConfig(),

    @SerialName("google_daily_summaries_config")
    val dailySummaryConfig: DailySummariesConfig = DailySummariesConfig(),
) {

    @Serializable
    data class V1Config(
        @SerialName("attenuation_scores")
        val attenuationScores: IntArray = intArrayOf(4, 4, 4, 4, 4, 4, 4, 4),

        @SerialName("attenuation_weight")
        val attenuationWeight: Int = 50,

        @SerialName("days_since_last_exposure_scores")
        val daysSinceLastExposureScores: IntArray = intArrayOf(4, 4, 4, 4, 4, 4, 4, 4),

        @SerialName("days_since_last_exposure_weight")
        val daysSinceLastExposureWeight: Int = 50,

        @SerialName("duration_at_attenuation_thresholds")
        val durationAtAttenuationThresholds: IntArray = intArrayOf(50, 74),

        @SerialName("duration_scores")
        val durationScores: IntArray = intArrayOf(4, 4, 4, 4, 4, 4, 4, 4),

        @SerialName("duration_weight")
        val durationWeight: Int = 50,

        @SerialName("minimum_risk_score")
        val minimumRiskScore: Int = 4,

        @SerialName("transmission_risk_scores")
        val transmissionRiskScores: IntArray = intArrayOf(4, 4, 4, 4, 4, 4, 4, 4),

        @SerialName("transmission_risk_weight")
        val transmissionRiskWeight: Int = 50,
    ) {
        fun toNative(): NativeExposureConfiguration =
            NativeExposureConfiguration.ExposureConfigurationBuilder()
                .setAttenuationScores(*attenuationScores)
                .setAttenuationWeight(attenuationWeight)
                .setDaysSinceLastExposureScores(*daysSinceLastExposureScores)
                .setDaysSinceLastExposureWeight(daysSinceLastExposureWeight)
                .setDurationAtAttenuationThresholds(*durationAtAttenuationThresholds)
                .setDurationScores(*durationScores)
                .setDurationWeight(durationWeight)
                .setMinimumRiskScore(minimumRiskScore)
                .setTransmissionRiskScores(*transmissionRiskScores)
                .setTransmissionRiskWeight(transmissionRiskWeight)
                .build()
    }

    @Serializable
    data class DiagnosisKeysDataMappingConfig(

        @SerialName("infectiousness_for_days_since_onset_of_symptoms")
        val daysSinceOnsetToInfectiousness: Map<Int, Int> = mapOf(
            -14 to Infectiousness.NONE,
            -13 to Infectiousness.NONE,
            -12 to Infectiousness.NONE,
            -11 to Infectiousness.NONE,
            -10 to Infectiousness.NONE,
            -9 to Infectiousness.NONE,
            -8 to Infectiousness.NONE,
            -7 to Infectiousness.NONE,
            -6 to Infectiousness.NONE,
            -5 to Infectiousness.STANDARD,
            -4 to Infectiousness.STANDARD,
            -3 to Infectiousness.STANDARD,
            -2 to Infectiousness.HIGH,
            -1 to Infectiousness.HIGH,
            0 to Infectiousness.HIGH,
            1 to Infectiousness.HIGH,
            2 to Infectiousness.HIGH,
            3 to Infectiousness.HIGH,
            4 to Infectiousness.HIGH,
            5 to Infectiousness.HIGH,
            6 to Infectiousness.STANDARD,
            7 to Infectiousness.STANDARD,
            8 to Infectiousness.STANDARD,
            9 to Infectiousness.STANDARD,
            10 to Infectiousness.STANDARD,
            11 to Infectiousness.NONE,
            12 to Infectiousness.NONE,
            13 to Infectiousness.NONE,
            14 to Infectiousness.NONE,
        ),

        @SerialName("infectiousness_when_days_since_onset_missing")
        val infectiousnessWhenDaysSinceOnsetMissing: Int = Infectiousness.STANDARD,

        @SerialName("report_type_when_missing")
        val reportTypeWhenMissing: Int = ReportType.CONFIRMED_TEST,
    ) {
        fun toNative(): DiagnosisKeysDataMapping =
            DiagnosisKeysDataMapping.DiagnosisKeysDataMappingBuilder()
                .setDaysSinceOnsetToInfectiousness(daysSinceOnsetToInfectiousness)
                .setInfectiousnessWhenDaysSinceOnsetMissing(infectiousnessWhenDaysSinceOnsetMissing)
                .setReportTypeWhenMissing(reportTypeWhenMissing)
                .build()
    }

    @Serializable
    data class DailySummariesConfig(
        @SerialName("attenuation_bucket_threshold_db")
        val attenuationBacketThresholdDb: List<Int> = listOf(50, 70, 90),

        @SerialName("attenuation_bucket_weights")
        val attenuationBucketWeights: List<Double> = listOf(1.0, 1.0, 1.0, 1.0),

        @SerialName("days_since_exposure_threshold")
        val daysSinceExposureThreshold: Int = 0,

        @SerialName("infectiousness_weights")
        val infectiousnessWeights: Map<Int, Double> = mapOf(
            Infectiousness.HIGH to 1.0,
            Infectiousness.STANDARD to 1.0,
        ),

        @SerialName("minimum_window_score")
        val minimumWindowScore: Double = 0.0,

        @SerialName("report_type_weights")
        val reportTypeWeights: Map<Int, Double> = mapOf(
            ReportType.CONFIRMED_CLINICAL_DIAGNOSIS to 1.0,
            ReportType.CONFIRMED_TEST to 1.0,
            ReportType.SELF_REPORT to 1.0,
            ReportType.RECURSIVE to 1.0,
        ),
    ) {
        fun toNative(): NativeDailySummariesConfig {
            val builder = NativeDailySummariesConfig.DailySummariesConfigBuilder()
                .setAttenuationBuckets(attenuationBacketThresholdDb, attenuationBucketWeights)
                .setDaysSinceExposureThreshold(daysSinceExposureThreshold)
                .setMinimumWindowScore(minimumWindowScore)

            infectiousnessWeights.keys.forEach { infectiousness ->
                val weight = infectiousnessWeights[infectiousness] ?: return@forEach
                builder.setInfectiousnessWeight(infectiousness, weight)
            }
            reportTypeWeights.keys.forEach { reportType ->
                val weight = reportTypeWeights[reportType] ?: return@forEach
                builder.setReportTypeWeight(reportType, weight)
            }

            return builder.build()
        }
    }
}