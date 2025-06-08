package com.illiarb.peek.summarizer.network

import com.illiarb.peek.summarizer.BuildKonfig
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpHeaders

internal fun authPlugin() = createClientPlugin(
  name = "AuthPlugin",
  createConfiguration = {},
) {
  onRequest { requestBuilder, _ ->
    requestBuilder.headers.append(
      name = HttpHeaders.Authorization,
      value = "Bearer ${BuildKonfig.OPENAI_KEY}"
    )
  }
}