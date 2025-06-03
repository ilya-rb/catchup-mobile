package com.illiarb.catchup.features.home.bookmarks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.core.data.mapContent
import com.illiarb.catchup.features.home.articles.ArticlesUiEvent
import com.illiarb.catchup.service.CatchupService
import com.illiarb.catchup.service.domain.Article
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import me.tatarka.inject.annotations.Inject

@Inject
public class BookmarksScreenPresenterFactory(
  private val catchupService: CatchupService,
) : Presenter.Factory {

  override fun create(
    screen: Screen,
    navigator: Navigator,
    context: CircuitContext
  ): Presenter<*>? {
    return when (screen) {
      is BookmarksScreen -> BookmarksPresenter(navigator, catchupService)
      else -> null
    }
  }
}

internal class BookmarksPresenter(
  private val navigator: Navigator,
  private val catchupService: CatchupService,
) : Presenter<BookmarksScreen.State> {

  @Composable
  override fun present(): BookmarksScreen.State {
    val articles by produceRetainedState<Async<SnapshotStateList<Article>>>(Async.Loading) {
      catchupService.collectSavedArticles()
        .mapContent { it.toMutableStateList() }
        .collect { value = it }
    }

    return BookmarksScreen.State(
      articles = articles,
      articlesEventSink = { event ->
        when (event) {
          is ArticlesUiEvent.ArticleBookmarkClicked -> TODO()
          is ArticlesUiEvent.ArticleClicked -> TODO()
          is ArticlesUiEvent.ArticleShareClicked -> TODO()
          is ArticlesUiEvent.ArticleSummarizeClicked -> TODO()
          is ArticlesUiEvent.ArticlesRefreshClicked -> TODO()
        }
      },
      eventSink = { event ->
        when (event) {
          BookmarksScreen.Event.ErrorRetryClicked -> Unit
          BookmarksScreen.Event.NavigationButtonClicked -> navigator.pop()
        }
      },
    )
  }
}