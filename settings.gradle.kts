dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
    }
}
rootProject.name = "COCOA"
include(":app")
include(":common")

include(":exposure-notification")
include(":exposure-notification:common")
include(":exposure-notification:exposure-detection")
include(":exposure-notification:diagnosis-submission")
include(":exposure-notification:core")
include(":exposure-notification:ui")
