// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion = "1.6.0"
    val hiltVersion = "2.40.5"
    val composeVersion = "1.1.0-rc01"
    val roomVersion = "2.4.0-beta02"

    extra.apply {
        set("kotlinVersion", kotlinVersion)
        set("hiltVersion", hiltVersion)
        set("composeVersion", composeVersion)
        set("roomVersion", roomVersion)
    }

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
