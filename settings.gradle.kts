dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
//        maven(url = "https://s01.oss.sonatype.org/content/repositories/devkeijirfc4648-1007/")
    }
}
rootProject.name = "COCOA"
include(":app")
include(":common")

include(":exposure-notification")
include(":exposure-notification:common")
include(":exposure-notification:exposure-detection")
include(":exposure-notification:diagnosis-submission")
include(":exposure-notification:chino")
include(":exposure-notification:ui")
