package com.illiarb.catchup.uikit.core.components.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.catchup.uikit.core.components.cell.ArticleLoadingCell

@Composable
@Preview
internal fun ArticleCellLoadingPreviewLight() {
  PreviewTheme(darkMode = false) {
    ArticleLoadingCell()
  }
}

@Composable
@Preview
internal fun ArticleCellLoadingPreviewDark() {
  PreviewTheme(darkMode = true) {
    ArticleLoadingCell()
  }
}