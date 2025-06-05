package com.illiarb.catchup.uikit.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

@Composable
public fun TextSwitcher(
  firstText: String,
  secondText: String,
  seconds: Int,
  modifier: Modifier = Modifier
) {
  var showFirst by remember { mutableStateOf(true) }
  val alpha = remember { Animatable(1f) }

  LaunchedEffect(showFirst) {
    alpha.snapTo(1f)
    delay(seconds * 1000L)
    alpha.animateTo(0f, animationSpec = tween(400))
    showFirst = !showFirst
    alpha.snapTo(1f)
  }

  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    if (showFirst) {
      Text(text = firstText, modifier = Modifier.graphicsLayer(alpha = alpha.value))
    } else {
      Text(text = secondText, modifier = Modifier.graphicsLayer(alpha = alpha.value))
    }
  }
} 