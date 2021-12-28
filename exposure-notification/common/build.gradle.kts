import org.jetbrains.kotlin.konan.properties.Properties
import com.android.build.api.dsl.VariantDimension

plugins {
    id("com.android.library")
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
val roomVersion: String by rootProject.extra

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

android {
    compileSdk = sdkVersion

    defaultConfig {
        minSdk = minVersion
        targetSdk = targetVersion

        val props = loadProperties("api-settings.properties")
            ?: loadProperties("api-settings-sample.properties")

        addBuildConfigStringField(props, "REGION_IDs")
        addBuildConfigStringField(props, "SUBREGION_IDs")
        addBuildConfigStringField(props, "DIAGNOSIS_SUBMISSION_API_ENDPOINT")
        addBuildConfigStringField(props, "DIAGNOSIS_KEY_API_ENDPOINT")
        addBuildConfigStringField(props, "EXPOSURE_CONFIGURATION_URL")
        addBuildConfigStringField(props, "EXPOSURE_DATA_COLLECTION_SERVICE_API_ENDPOINT")
        addBuildConfigStringField(props, "EXPOSURE_CONFIGURATION_URL")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        }
        release {
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
}

dependencies {
    api(project(mapOf("path" to ":common")))
    api(project(mapOf("path" to ":exposure-notification:core")))

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")

    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-work:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")

    implementation("dev.keiji.rfc4648:rfc4648:1.0.0")

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