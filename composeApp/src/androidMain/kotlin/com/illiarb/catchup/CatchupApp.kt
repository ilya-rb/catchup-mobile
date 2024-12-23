package com.illiarb.catchup

import android.app.Application
import android.content.Context
import com.illiarb.catchup.di.AndroidAppComponent
import com.illiarb.catchup.di.create
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

internal class CatchupApp : Application() {

  private val appComponent: AndroidAppComponent by lazy {
    AndroidAppComponent.create(application = this)
  }

  override fun onCreate() {
    super.onCreate()

    Napier.base(DebugAntilog())
  }

  fun appComponent(): AndroidAppComponent = appComponent
}

internal fun Context.appComponent(): AndroidAppComponent {
  return (this as CatchupApp).appComponent()
}
