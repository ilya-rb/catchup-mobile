package com.illiarb.catchup.core.arch

import com.slack.circuit.runtime.screen.Screen

public expect class OpenUrlScreen(url: String) : Screen {
  public val url: String
}