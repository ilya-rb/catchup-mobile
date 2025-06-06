package com.illiarb.catchup.uikit.core.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.components.shimmer.ShimmerBox
import com.illiarb.catchup.uikit.core.components.shimmer.ShimmerColumn

@Composable
public fun TopAppBarTitleLoading() {
  ShimmerColumn {
    ShimmerBox(Modifier.size(height = 16.dp, width = 40.dp))
  }
}