package com.illiarb.catchup.uikit.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
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
import com.illiarb.catchup.uikit.core.components.ToastMessage.ToastType
import com.illiarb.catchup.uikit.core.configuration.getScreenHeight
import com.illiarb.catchup.uikit.core.model.VectorIcon
import kotlinx.coroutines.delay

public data class ToastMessage(
  val content: String,
  val type: ToastType,
) {
  public enum class ToastType {
    DEFAULT,
    SUCCESS,
    ERROR,
  }
}

@Composable
public fun ToastMessage(
  message: ToastMessage?,
  modifier: Modifier = Modifier,
  bottomPadding: Dp = 48.dp,
  durationMillis: Int = 2000,
  onDismiss: (() -> Unit)? = null,
) {
  val density = LocalDensity.current
  val screenHeightPx = with(density) { getScreenHeight().toPx() }
  val offsetY = remember { Animatable(screenHeightPx) }
  val alpha = remember { Animatable(0f) }
  val show = message != null

  LaunchedEffect(show, screenHeightPx) {
    if (show) {
      offsetY.snapTo(screenHeightPx)
      alpha.snapTo(0f)

      // Animate in with bounce (overshoot)
      offsetY.animateTo(
        targetValue = -20f,
        animationSpec = tween(durationMillis = 420, easing = EaseOutCubic)
      )
      offsetY.animateTo(
        targetValue = 0f,
        animationSpec = tween(durationMillis = 320, easing = EaseInCubic)
      )
      alpha.animateTo(1f, animationSpec = tween(400))

      // Stay visible
      delay(durationMillis.toLong())

      // Animate out with bounce
      offsetY.animateTo(-20f, animationSpec = tween(durationMillis = 180, easing = EaseOutCubic))
      offsetY.animateTo(
        screenHeightPx,
        animationSpec = tween(durationMillis = 420, easing = EaseInCubic)
      )
      alpha.animateTo(0f, animationSpec = tween(300))

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
      val background = when (message) {
        null -> MaterialTheme.colorScheme.surfaceVariant
        else -> {
          when (message.type) {
            ToastType.ERROR -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.surfaceVariant
          }
        }
      }

      Surface(
        color = background,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 8.dp,
        modifier = modifier
          .padding(bottom = bottomPadding)
          .wrapContentHeight()
          .alpha(alpha.value)
          .graphicsLayer(translationY = offsetY.value),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
          if (message != null) {
            val icon = when (message.type) {
              ToastType.DEFAULT -> Icons.Default.Info
              ToastType.SUCCESS -> Icons.Default.Check
              ToastType.ERROR -> Icons.Default.Error
            }

            val color = when (message.type) {
              ToastType.ERROR -> MaterialTheme.colorScheme.onError
              else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            Icon(
              imageVector = icon,
              contentDescription = "",
              tint = color,
              modifier = Modifier.padding(end = 12.dp)
            )

            Text(
              text = message.content,
              color = color,
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }
      }
    }
  }
}