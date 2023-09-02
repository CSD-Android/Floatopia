pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// java toolchain
plugins {
    id ("org.gradle.toolchains.foojay-resolver-convention") version ("0.4.0")
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Floatopia"
include(":app")
include(":lib_common")
include(":lib_framework")
include(":lib_network")
include(":lib_room")
include(":lib_pic")
//include(":mod_main")
