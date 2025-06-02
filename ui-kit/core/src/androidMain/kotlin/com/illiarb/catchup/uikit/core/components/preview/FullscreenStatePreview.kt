package com.illiarb.catchup.uikit.core.components.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.illiarb.catchup.uikit.core.components.FullscreenState
import com.illiarb.catchup.uikit.core.theme.UiKitTheme

internal data class FullscreenStateData(
  val title: String,
  val buttonText: String?,
  val hasButton: Boolean
)

internal class FullscreenStatePreviewProvider : PreviewParameterProvider<FullscreenStateData> {
  override val values = sequenceOf(
    FullscreenStateData("Nothing to See Here", "Try Again", true),
    FullscreenStateData("Welcome!", null, false),
    FullscreenStateData("You are Offline, 2 line text, 2 line text", "Refresh", true),
  )
}

@Preview(
  name = "Light Mode - Parameterized",
  group = "FullscreenStateParameterized"
)
@Composable
internal fun FullscreenStatePreviewParameterizedLight(
  @PreviewParameter(FullscreenStatePreviewProvider::class)
  data: FullscreenStateData
) {
  PreviewTheme(darkMode = false) {
    FullscreenState(
      title = data.title,
      buttonText = if (data.hasButton) data.buttonText else null,
      onButtonClick = { },
      image = { modifier ->
        Image(
          painter = painterResource(id = android.R.drawable.ic_dialog_dialer),
          contentDescription = "Icon",
          modifier = modifier,
          colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
        )
      }
    )
  }
}

@Preview(
  name = "Dark Mode - Parameterized",
  group = "FullscreenStateParameterized",
)
@Composable
internal fun FullscreenStatePreviewParameterizedDark(
  @PreviewParameter(FullscreenStatePreviewProvider::class)
  data: FullscreenStateData
) {
  PreviewTheme(darkMode = true) {
    FullscreenState(
      title = data.title,
      buttonText = if (data.hasButton) data.buttonText else null,
      onButtonClick = { },
      image = { modifier ->
        Image(
          painter = painterResource(id = android.R.drawable.ic_dialog_dialer),
          contentDescription = "Icon",
          modifier = modifier,
          colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
        )
      }
    )
  }
}