package com.illiarb.peek.uikit.imageloader

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
public fun UrlImage(
  modifier: Modifier = Modifier,
  url: String,
  contentScale: ContentScale = ContentScale.Crop,
  error: Painter? = null,
) {
  AsyncImage(
    model = url,
    contentDescription = null,
    modifier = modifier,
    contentScale = contentScale,
    error = error,
  )
}
