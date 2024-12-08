package com.illiarb.catchup.uikit.core.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.duration_reading_time
import org.jetbrains.compose.resources.pluralStringResource
import kotlin.time.Duration

@Composable
fun ReadingTimeText(
  modifier: Modifier = Modifier,
  style: TextStyle,
  duration: Duration,
) {
  Text(
    modifier = modifier,
    text = pluralStringResource(
      Res.plurals.duration_reading_time,
      duration.inWholeMinutes.toInt(),
      duration.inWholeMinutes.toInt(),
    ),
    style = style,
  )
}