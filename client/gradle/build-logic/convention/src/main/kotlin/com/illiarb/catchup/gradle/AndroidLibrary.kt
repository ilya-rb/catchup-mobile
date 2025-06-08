package com.illiarb.catchup.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibrary : Plugin<Project> {
  
  override fun apply(target: Project) = with(target) {
    with(pluginManager) {
      apply("com.android.library")
      apply("org.gradle.android.cache-fix")
    }
    configureAndroid()
  }
}
