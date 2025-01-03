package com.illiarb.catchup.service.db.dao

import com.illiarb.catchup.core.coroutines.AppDispatchers
import com.illiarb.catchup.core.coroutines.suspendRunCatching
import com.illiarb.catchup.service.Database
import com.illiarb.catchup.service.domain.NewsSource
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class NewsSourcesDao(
  private val db: Database,
  private val appDispatchers: AppDispatchers,
) {

  public suspend fun getAll(): Result<Set<NewsSource>?> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.news_sourcesQueries
          .all(mapper = { kind, imageUrl -> NewsSource(kind, imageUrl) })
          .executeAsList()
          .toSet()
          .takeIf { it.isNotEmpty() }
      }
    }
  }

  public suspend fun insert(sources: Set<NewsSource>): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.transactionWithResult {
          sources.forEach { source ->
            db.news_sourcesQueries.insert(source.kind, source.imageUrl)
          }
        }
      }
    }
  }

  public suspend fun delete(): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching { db.news_sourcesQueries.delete() }
    }
  }
}