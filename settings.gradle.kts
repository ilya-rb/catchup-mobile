rootProject.name = "catchup-mobile"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  includeBuild("gradle/build-logic")

  repositories {
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
  }
}

include(
  ":catchup-service",
  ":composeApp",
  ":core:coroutines",
  ":core:logging",
  ":core:network",
  ":core:app-info",
  ":core:arch",
  ":core:data",
  "features:home",
  "features:reader",
  "features:settings",
  ":ui-kit:core",
  ":ui-kit:resources",
  ":ui-kit:image-loader",
)
