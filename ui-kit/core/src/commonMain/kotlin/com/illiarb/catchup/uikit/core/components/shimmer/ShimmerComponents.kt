package com.illiarb.catchup.uikit.core.components.shimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
public fun ShimmerBox(
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(Color.LightGray)
  )
}

@Composable
public fun ShimmerCircle(
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .clip(CircleShape)
      .background(Color.LightGray)
  )
}