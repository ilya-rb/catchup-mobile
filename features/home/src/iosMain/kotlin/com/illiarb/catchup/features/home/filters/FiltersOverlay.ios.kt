package com.illiarb.catchup.features.home.filters

import com.slack.circuit.overlay.OverlayHost

internal actual suspend fun OverlayHost.showFiltersOverlay(model: FiltersOverlayModel): FiltersOverlayResult =
  FiltersOverlayResult.Cancel