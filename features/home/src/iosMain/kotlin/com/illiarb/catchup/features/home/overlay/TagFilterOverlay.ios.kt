package com.illiarb.catchup.features.home.overlay

import com.slack.circuit.overlay.OverlayHost

context(OverlayHost)
internal actual suspend fun showTagFilterOverlay(input: TagFilterContract.Input): TagFilterContract.Output {
  return TagFilterContract.Output.Cancel
}