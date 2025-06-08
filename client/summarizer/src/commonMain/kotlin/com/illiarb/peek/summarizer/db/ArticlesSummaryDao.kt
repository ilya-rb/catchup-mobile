package com.illiarb.peek.summarizer.db

import com.illiarb.peek.core.coroutines.AppDispatchers
import com.illiarb.peek.core.coroutines.suspendRunCatching
import com.illiarb.peek.summarizer.Database
import com.illiarb.peek.summarizer.Summaries
import com.illiarb.peek.summarizer.di.SummarizerApi
import com.illiarb.peek.summarizer.domain.ArticleSummary
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject

@Inject
public class ArticlesSummaryDao(
  private val appDispatchers: AppDispatchers,
  @SummarizerApi private val db: Database,
) {

  public suspend fun summaryByUrl(url: String): Result<ArticleSummary?> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.summariesQueries.summaryByUrl(url).executeAsOneOrNull()?.toDomain()
      }
    }
  }

  public suspend fun saveSummary(summary: ArticleSummary): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.summariesQueries.saveSummary(
          url = summary.url,
          summary = summary.content,
          createdAt = Clock.System.now(),
        )
      }
    }
  }

  private fun Summaries.toDomain(): ArticleSummary {
    return ArticleSummary(url, summary)
  }
}