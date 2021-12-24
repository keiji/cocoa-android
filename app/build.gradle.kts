import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("plugin.serialization")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

val hiltVersion: String by rootProject.extra

fun loadProperties(filename: String): Properties? {
    val file = File(rootProject.rootDir, filename)
    if (!file.exists()) {
        print("Properties file ${file.absolutePath} not found.")
        return null
    }
    file.reader().use {
        return Properties().apply {
            load(it)
        }
    }
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "dev.keiji.cocoa.android"
        minSdk = 23
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        val props = loadProperties("api-settings.properties")

        val regionIds =
            props?.getProperty("REGION_IDs") ?: "\"\""
        buildConfigField(
            "String",
            "REGION_IDs",
            regionIds
        )

        val subregionIds =
            props?.getProperty("SUBREGION_IDs") ?: "\"\""
        buildConfigField(
            "String",
            "SUBREGION_IDs",
            subregionIds
        )

        val diagnosisSubmissionApiEndpoint =
            props?.getProperty("DIAGNOSIS_SUBMISSION_API_ENDPOINT") ?: "\"\""
        buildConfigField(
            "String",
            "DIAGNOSIS_SUBMISSION_API_ENDPOINT",
            diagnosisSubmissionApiEndpoint
        )

        val diagnosisKeyApiEndpoint =
            props?.getProperty("DIAGNOSIS_KEY_API_ENDPOINT") ?: "\"\""
        buildConfigField(
            "String",
            "DIAGNOSIS_KEY_API_ENDPOINT",
            diagnosisKeyApiEndpoint
        )

        val exposureConfigurationUrl =
            props?.getProperty("EXPOSURE_CONFIGURATION_URL") ?: "\"\""
        buildConfigField(
            "String",
            "EXPOSURE_CONFIGURATION_URL",
            exposureConfigurationUrl
        )

        val exposureDataCollectionServiceApiEndpoint =
            props?.getProperty("EXPOSURE_DATA_COLLECTION_SERVICE_API_ENDPOINT") ?: "\"\""
        buildConfigField(
            "String",
            "EXPOSURE_DATA_COLLECTION_SERVICE_API_ENDPOINT",
            exposureDataCollectionServiceApiEndpoint
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        dataBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.0-rc01"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    flavorDimensions.add("apiVersion")
    productFlavors {
        create("legacyV1") {
            dimension = "apiVersion"
            buildConfigField(
                "Boolean",
                "USE_EXPOSURE_WINDOW_MODE",
                "false"
            )
        }
        create("exposureWindow") {
            dimension = "apiVersion"
            buildConfigField(
                "Boolean",
                "USE_EXPOSURE_WINDOW_MODE",
                "true"
            )
        }
    }

    buildTypes {
        debug {
            buildConfigField(
                "Long",
                "EXPOSURE_DETECTION_WORKER_INTERVAL_IN_MINUTES",
                "16L"
            )
            buildConfigField(
                "Long",
                "EXPOSURE_DETECTION_WORKER_BACKOFF_DELAY_IN_MINUTES",
                "16L"
            )
        }
        release {
            buildConfigField(
                "Long",
                "EXPOSURE_DETECTION_WORKER_INTERVAL_IN_MINUTES",
                "4 * 60L"
            )
            buildConfigField(
                "Long",
                "EXPOSURE_DETECTION_WORKER_BACKOFF_DELAY_IN_MINUTES",
                "60L"
            )

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    val composeVersion = "1.1.0-rc01"

    implementation(
        fileTree(
            mapOf(
                "dir" to "libs",
                "include" to listOf("*.aar", "*.jar"),
                "exclude" to listOf<String>()
            )
        )
    )

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.compose.material:material:1.0.5")
    implementation("androidx.compose.material:material-icons-extended:1.1.0-rc01")

    implementation("androidx.compose.ui:ui-tooling:1.0.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")
    implementation("com.google.android.material:compose-theme-adapter:1.1.2")
    implementation("androidx.navigation:navigation-compose:2.4.0-rc01")

    implementation("androidx.compose.compiler:compiler:$composeVersion")
    implementation("androidx.compose.runtime:runtime:$composeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")

    api("androidx.navigation:navigation-fragment-ktx:2.3.5")

    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-work:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    implementation("com.google.android.gms:play-services-base:18.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.5.2")

    implementation("dev.keiji.rfc4648:rfc4648:1.0.0")

    testImplementation("junit:junit:4.+")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:4.0.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.5")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
