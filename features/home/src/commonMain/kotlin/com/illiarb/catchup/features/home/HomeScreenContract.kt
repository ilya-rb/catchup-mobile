package com.illiarb.catchup.features.home

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.illiarb.catchup.core.arch.CommonParcelable
import com.illiarb.catchup.core.arch.CommonParcelize
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.features.home.overlay.TagFilterContract
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.service.domain.Tag
import com.illiarb.catchup.summarizer.ui.SummaryScreen
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

@CommonParcelize
public object HomeScreen : Screen, CommonParcelable {

  @Stable
  internal data class State(
    val articles: Async<SnapshotStateList<Article>>,
    val newsSources: Async<ImmutableList<NewsSource>>,
    val selectedTags: ImmutableSet<Tag>,
    val selectedNewsSourceIndex: Int,
    val filtersShowing: Boolean,
    val articleSummaryToShow: Article?,
    val eventSink: (Event) -> Unit,
  ) : CircuitUiState {

    val articlesTags: Set<Tag> by lazy(LazyThreadSafetyMode.NONE) {
      when (articles) {
        is Async.Content -> {
          articles.content
            .map(Article::tags)
            .flatten()
            // TODO: To remove this and fix database tags insert
            .filter { it.value.isNotBlank() }
            .toSet()
        }

        else -> emptySet()
      }
    }

    fun articlesStateKey(): Any {
      return when (articles) {
        is Async.Content -> articles.content.isEmpty()
        else -> this::class
      }
    }
  }

  internal sealed interface Event : CircuitUiEvent {
    data class ArticleBookmarkClicked(val item: Article) : Event
    data class ArticleClicked(val item: Article) : Event
    data class ArticleSummarizeClicked(val item: Article) : Event
    data class ArticleShareClicked(val item: Article) : Event
    data class TagFilterResult(val result: TagFilterContract.Output) : Event
    data class SummaryResult(val result: SummaryScreen.Result) : Event
    data class TabClicked(val source: NewsSource) : Event
    data object ErrorRetryClicked : Event
    data object FiltersClicked : Event
    data object SettingsClicked : Event
  }
}
