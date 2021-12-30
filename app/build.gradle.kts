import org.jetbrains.kotlin.konan.properties.Properties
import com.android.build.api.dsl.VariantDimension

plugins {
    id("com.android.application")
    kotlin("android")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("plugin.serialization")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

val sdkVersion: Int by rootProject.extra
val minVersion: Int by rootProject.extra
val targetVersion: Int by rootProject.extra

val hiltVersion: String by rootProject.extra
val composeVersion: String by rootProject.extra
val roomVersion: String by rootProject.extra

android {
    compileSdk = sdkVersion

    defaultConfig {
        applicationId = "dev.keiji.cocoa.android"
        minSdk = minVersion
        targetSdk = targetVersion
        versionCode = 1
        versionName = "1.0.0"

        val props = loadProperties("api-settings.properties")
            ?: loadProperties("api-settings-sample.properties")

        addBuildConfigStringField(props, "REGION_IDs")
        addBuildConfigStringField(props, "SUBREGION_IDs")

        addBuildConfigStringField(props, "SUBMIT_DIAGNOSIS_API_ENDPOINT")
        addBuildConfigStringField(props, "DIAGNOSIS_KEY_API_ENDPOINT")
        addBuildConfigStringField(props, "EXPOSURE_CONFIGURATION_URL")
        addBuildConfigStringField(props, "EXPOSURE_DATA_COLLECTION_API_ENDPOINT")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        dataBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.0-rc01"
    }

    flavorDimensions.add("enApiMode")
    flavorDimensions.add("enApiVersion")
    productFlavors {
        create("enMock") {
            dimension = "enApiMode"
        }
        create("enProd") {
            dimension = "enApiMode"
        }
        create("legacyV1") {
            dimension = "enApiVersion"
            buildConfigField(
                "Boolean",
                "USE_EXPOSURE_WINDOW_MODE",
                "false"
            )
        }
        create("exposureWindow") {
            dimension = "enApiVersion"
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    packagingOptions {
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
    }
}

dependencies {
    implementation(project(mapOf("path" to ":common")))

    implementation(project(mapOf("path" to ":exposure-notification:common")))
    implementation(project(mapOf("path" to ":exposure-notification:diagnosis-submission")))
    implementation(project(mapOf("path" to ":exposure-notification:exposure-detection")))
    implementation(project(mapOf("path" to ":exposure-notification:ui")))
    implementation(
        fileTree(
            mapOf(
                "dir" to "$rootDir/exposure-notification/chino/libs/",
                "include" to listOf("play-services-nearby-exposurenotification-*.aar"),
            )
        )
    )

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")

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

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")

    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-work:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    testImplementation("junit:junit:4.+")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.5")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
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

fun VariantDimension.addBuildConfigStringField(
    props: Properties?,
    propertyName: String
) {
    props ?: return

    val value = props.getProperty(propertyName) ?: "\"\""
    buildConfigField("String", propertyName, value)
}
