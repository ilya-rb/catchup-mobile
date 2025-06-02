package com.illiarb.catchup.uikit.core.components.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.theme.UiKitTheme

@Composable
internal fun PreviewTheme(darkMode: Boolean, content: @Composable () -> Unit) {
  UiKitTheme(useDarkTheme = darkMode, useDynamicColors = false) {
    Column(
      modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .padding(40.dp)
    ) {
      content()
    }
  }
}