package com.illiarb.catchup.uikit.core.components.preview

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.catchup.uikit.core.components.cell.ArticleCell
import com.illiarb.catchup.uikit.core.components.cell.ArticleLoadingCell
import com.illiarb.catchup.uikit.core.theme.UiKitTheme

@Composable
internal fun ArticleCellPreview(darkTheme: Boolean) {
  UiKitTheme(useDynamicColors = false, useDarkTheme = darkTheme) {
    LazyColumn {
      items(
        count = 3,
        key = { index -> index },
        itemContent = {
          ArticleCell(
            title = "Title Title Title Title Title Title ".repeat(2),
            caption = "Caption",
            author = "Author",
            saved = false,
            onClick = {},
            onBookmarkClick = {},
            onSummarizeClick = {},
          )

          HorizontalDivider()
        }
      )
    }
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
