package com.illiarb.catchup.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.illiarb.catchup.core.arch.OpenUrlScreen
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.core.data.mapContent
import com.illiarb.catchup.features.home.HomeScreenContract.Event
import com.illiarb.catchup.features.home.filters.FiltersOverlayResult
import com.illiarb.catchup.features.reader.ReaderScreen
import com.illiarb.catchup.features.settings.SettingsScreen
import com.illiarb.catchup.service.CatchupService
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.Tag
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import me.tatarka.inject.annotations.Inject

@Inject
public class HomeScreenPresenterFactory(
  private val catchupService: CatchupService,
) : Presenter.Factory {
  override fun create(
    screen: Screen,
    navigator: Navigator,
    context: CircuitContext
  ): Presenter<*>? {
    return when (screen) {
      is HomeScreen -> HomeScreenPresenter(catchupService, navigator)
      else -> null
    }
  }
}

internal class HomeScreenPresenter(
  private val catchupService: CatchupService,
  private val navigator: Navigator,
) : Presenter<HomeScreenContract.State> {

  @Composable
  override fun present(): HomeScreenContract.State {
    var selectedTabIndex by rememberRetained { mutableStateOf(value = 0) }
    var filtersShowing by rememberRetained { mutableStateOf(false) }
    var reloadData by rememberRetained { mutableStateOf(false) }
    var selectedTags by rememberRetained { mutableStateOf(emptySet<Tag>()) }

    val sources by produceRetainedState<Async<ImmutableList<HomeScreenContract.Tab>>>(
      initialValue = Async.Loading,
      key1 = catchupService,
    ) {
      catchupService.collectAvailableSources().mapContent { sources ->
        sources.map { source ->
          HomeScreenContract.Tab(
            id = source.kind.name,
            source = source,
            imageUrl = source.imageUrl.url,
          )
        }.toImmutableList()
      }.collect {
        value = it
      }
    }

    val articles by produceRetainedState<Async<ImmutableList<Article>>>(
      initialValue = Async.Loading,
      key1 = selectedTabIndex,
      key2 = sources,
      key3 = reloadData,
    ) {
      val source = when (val currentSources = sources) {
        is Async.Content -> currentSources.content.getOrNull(selectedTabIndex)
        else -> null
      }

      if (source == null) {
        value = Async.Loading
      } else {
        catchupService.collectLatestNewsFrom(source.source.kind)
          .mapContent { it.toImmutableList() }
          .collect { value = it }
      }
    }

    fun eventSink(event: Event) {
      when (event) {
        is Event.SavedClicked -> Unit
        is Event.SettingsClicked -> navigator.goTo(SettingsScreen)
        is Event.ErrorRetryClicked -> reloadData = !reloadData
        is Event.FiltersClicked -> filtersShowing = true
        is Event.ArticleClicked -> {
          if (event.item.content != null) {
            navigator.goTo(ReaderScreen(event.item.id))
          } else {
            navigator.goTo(OpenUrlScreen(event.item.link.url))
          }
        }

        is Event.FiltersResult -> {
          filtersShowing = false

          if (event.result is FiltersOverlayResult.Saved) {
            selectedTags = event.result.tags
          }
        }

        is Event.TabClicked -> {
          val value = sources
          require(value is Async.Content<ImmutableList<HomeScreenContract.Tab>>)

          selectedTabIndex = value.content
            .map { it.source }
            .indexOf(event.source)
        }
      }
    }

    return HomeScreenContract.State(
      articles = articles,
      tabs = sources,
      selectedTabIndex = selectedTabIndex,
      selectedTags = selectedTags,
      eventSink = ::eventSink,
      filtersShowing = filtersShowing,
    )
  }
}
