package com.illiarb.catchup.features.home.overlay

import androidx.compose.material3.ExperimentalMaterial3Api
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuitx.overlays.BottomSheetOverlay

context(OverlayHost)
@OptIn(ExperimentalMaterial3Api::class)
internal actual suspend fun showTagFilterOverlay(
  input: TagFilterContract.Input,
): TagFilterContract.Output {
  return show(
    BottomSheetOverlay<TagFilterContract.Input, TagFilterContract.Output>(
      model = input,
      sheetContainerColor = input.containerColor,
      skipPartiallyExpandedState = true,
      onDismiss = {
        TagFilterContract.Output.Cancel
      },
    ) { _, navigator ->
      TagFilterOverlay(input, navigator)
    }
  )
}