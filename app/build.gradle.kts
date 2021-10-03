import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("plugin.serialization")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

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

        val exposureConfigurationApiEndpoint =
            props?.getProperty("EXPOSURE_CONFIGURATION_API_ENDPOINT") ?: "\"\""
        buildConfigField(
            "String",
            "EXPOSURE_CONFIGURATION_API_ENDPOINT",
            exposureConfigurationApiEndpoint
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        dataBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.0-alpha04"
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
    implementation(
        fileTree(
            mapOf(
                "dir" to "libs",
                "include" to listOf("*.aar", "*.jar"),
                "exclude" to listOf<String>()
            )
        )
    )

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.work:work-runtime-ktx:2.6.0")

    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    implementation("androidx.activity:activity-compose:1.3.1")
    implementation("androidx.compose.material:material:1.0.3")
    implementation("androidx.compose.material:material-icons-extended:1.1.0-alpha05")

    implementation("androidx.compose.ui:ui-tooling:1.0.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0-rc01")
    implementation("com.google.android.material:compose-theme-adapter:1.0.3")
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha10")

    implementation("androidx.compose.compiler:compiler:1.1.0-alpha05")
    implementation("androidx.compose.runtime:runtime:1.1.0-alpha05")
    implementation("androidx.compose.runtime:runtime-livedata:1.1.0-alpha05")
    implementation("androidx.compose.foundation:foundation:1.1.0-alpha05")
    implementation("androidx.compose.ui:ui:1.1.0-alpha05")

    api("androidx.navigation:navigation-fragment-ktx:2.3.5")

    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("com.google.dagger:hilt-android:2.38.1")
    implementation("androidx.hilt:hilt-work:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    kapt("com.google.dagger:hilt-android-compiler:2.38.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    implementation("com.google.guava:guava:31.0.1-android")

    implementation("com.google.android.gms:play-services-base:17.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.5.2")

    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.3")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
