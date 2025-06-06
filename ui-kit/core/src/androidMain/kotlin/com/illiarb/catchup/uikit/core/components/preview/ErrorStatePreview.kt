package com.illiarb.catchup.uikit.core.components.preview

import androidx.compose.runtime.Composable
import com.illiarb.catchup.uikit.core.components.FullscreenErrorState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
internal fun ErrorStatePreviewLight() {
  PreviewTheme(darkMode = false) {
    FullscreenErrorState {}
  }
}

@Composable
@Preview
internal fun ErrorStatePreviewDark() {
  PreviewTheme(darkMode = true) {
    FullscreenErrorState {}
  }
}