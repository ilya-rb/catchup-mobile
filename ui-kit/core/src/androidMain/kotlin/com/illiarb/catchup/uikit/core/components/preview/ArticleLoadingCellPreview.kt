package com.illiarb.catchup.uikit.core.components.preview

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.catchup.uikit.core.components.cell.ArticleLoadingCell

@Composable
@Preview
internal fun ArticleCellLoadingPreviewLight() {
  PreviewTheme(darkMode = false) {
    LazyColumn {
      items(count = 3) {
        ArticleLoadingCell()
        HorizontalDivider()
      }
    }
  }
}

@Composable
@Preview
internal fun ArticleCellLoadingPreviewDark() {
  PreviewTheme(darkMode = true) {
    LazyColumn {
      items(count = 3) {
        ArticleLoadingCell()
        HorizontalDivider()
      }
    }
  }
}