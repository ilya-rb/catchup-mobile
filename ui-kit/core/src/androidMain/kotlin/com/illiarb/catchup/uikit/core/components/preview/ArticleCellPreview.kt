package com.illiarb.catchup.uikit.core.components.preview

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.catchup.uikit.core.components.cell.ArticleCell
import com.illiarb.catchup.uikit.core.components.cell.ArticleLoadingCell
import com.illiarb.catchup.uikit.core.theme.UiKitTheme
import kotlin.random.Random

@Composable
internal fun ArticleCellPreview(darkTheme: Boolean) {
  UiKitTheme(useDynamicColors = false, useDarkTheme = darkTheme) {
    LazyColumn {
      items(
        count = 3,
        key = { index -> index },
        itemContent = {
          val length = Random.nextInt(1, 3)
          val title = "Title Title Title Title Title Title ".repeat(length)

          ArticleCell(
            title = title,
            caption = "Caption",
            author = "Author",
            saved = false,
            onClick = {
            },
            onBookmarkClick = {
            },
            onSummarizeClick = {
            }
          )
        }
      )
    }
  }
}

@Composable
internal fun ArticleCellLoadingPreview(darkTheme: Boolean) {
  UiKitTheme(useDynamicColors = false, useDarkTheme = darkTheme) {
    ArticleLoadingCell()
  }
}

@Composable
@Preview(
  showBackground = true,
  backgroundColor = 0xFFF9F9FF,
)
internal fun ArticlePreviewLight() {
  ArticleCellPreview(darkTheme = false)
}

@Composable
@Preview(
  showBackground = true,
  backgroundColor = 0xFF111318,
)
internal fun ArticlePreviewDark() {
  ArticleCellPreview(darkTheme = true)
}

@Composable
@Preview(
  showBackground = true,
  backgroundColor = 0xFFF9F9FF,
)
internal fun ArticleCellLoadingPreviewLight() {
  ArticleCellLoadingPreview(darkTheme = false)
}

@Composable
@Preview(
  showBackground = true,
  backgroundColor = 0xFF111318,
)
internal fun ArticleCellLoadingPreviewDark() {
  ArticleCellLoadingPreview(darkTheme = true)
}
