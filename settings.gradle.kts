pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Add two lines below to the repositories block (In setting.gradle file)
        // Add two lines below to the repositories block (In setting.gradle.kts file)
        maven {
            url = uri("https://plugins.gradle.org/m2")
        }
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "donona"
include(":app")

// Unity
//include(":unityLibrary")
//project(":unityLibrary").projectDir = file(".\\UnityGame\\unityLibrary")
