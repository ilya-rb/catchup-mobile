package com.illiarb.catchup.uikit.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.configuration.getScreenHeight
import com.illiarb.catchup.uikit.core.model.VectorIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
public fun ToastMessage(
  show: Boolean,
  text: String,
  durationMillis: Int = 2000,
  modifier: Modifier = Modifier,
  bottomPadding: Dp = 48.dp,
  onDismiss: (() -> Unit)? = null,
  icon: VectorIcon? = null,
) {
  val density = LocalDensity.current
  val screenHeightPx = with(density) { getScreenHeight().toPx() }
  val offsetY = remember { Animatable(screenHeightPx) }
  val alpha = remember { Animatable(0f) }

  LaunchedEffect(show, screenHeightPx) {
    if (show) {
      offsetY.snapTo(screenHeightPx)
      alpha.snapTo(0f)
      // Animate in
      launch {
        offsetY.animateTo(0f, animationSpec = tween(350))
      }
      launch {
        alpha.animateTo(1f, animationSpec = tween(350))
      }
      // Stay visible
      delay(durationMillis.toLong())
      // Animate out with bounce
      offsetY.animateTo(-20f, animationSpec = tween(120))
      offsetY.animateTo(screenHeightPx, animationSpec = tween(300))
      alpha.animateTo(0f, animationSpec = tween(200))
      onDismiss?.invoke()
    } else {
      offsetY.snapTo(screenHeightPx)
      alpha.snapTo(0f)
    }
  }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.BottomCenter
  ) {
    if (alpha.value > 0f) {
      Surface(
        modifier = modifier
          .padding(bottom = bottomPadding)
          .wrapContentHeight()
          .alpha(alpha.value)
          .graphicsLayer(translationY = offsetY.value),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 8.dp
      ) {
        androidx.compose.foundation.layout.Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
          if (icon != null) {
            Icon(
              imageVector = icon.imageVector,
              contentDescription = icon.contentDescription,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(end = 12.dp)
            )
          }
          Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
    }
  }
}