pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // Alternative source
        maven { url = uri("https://maven.aliyun.com/repository/google") } // China mirror
        maven { url = uri("https://maven.aliyun.com/repository/public") } // China mirror
    }
}

rootProject.name = "MealMate"
include(":app")