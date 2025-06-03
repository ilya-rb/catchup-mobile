package com.illiarb.catchup.uikit.core.components.cell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
public fun ArticleLoadingCell() {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.shimmer()
  ) {
    val shimmerColor = Color.LightGray
    val shimmerCornerRadius = 8.dp

    Column(modifier = Modifier.weight(1f)) {
      Box(
        modifier = Modifier
          .padding(start = 16.dp, top = 16.dp)
          .size(width = 140.dp, height = 16.dp)
          .clip(RoundedCornerShape(shimmerCornerRadius))
          .background(shimmerColor)
      )
      Box(
        modifier = Modifier
          .padding(start = 16.dp, end = 16.dp, top = 12.dp)
          .size(width = 200.dp, height = 12.dp)
          .clip(RoundedCornerShape(shimmerCornerRadius))
          .background(shimmerColor)
      )

      Box(
        modifier = Modifier
          .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
          .size(width = 80.dp, height = 10.dp)
          .clip(RoundedCornerShape(shimmerCornerRadius))
          .background(shimmerColor)
      )
    }

    Column(verticalArrangement = Arrangement.SpaceAround) {
      Box(
        Modifier
          .padding(end = 16.dp)
          .padding(top = 8.dp)
          .size(24.dp)
          .clip(RoundedCornerShape(shimmerCornerRadius))
          .background(shimmerColor)
      )
      Box(
        Modifier
          .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
          .size(24.dp)
          .clip(RoundedCornerShape(shimmerCornerRadius))
          .background(shimmerColor)
      )
    }
  }
}