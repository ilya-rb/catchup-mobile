package com.illiarb.catchup.features.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.features.home.HomeScreen.Event
import com.illiarb.catchup.features.home.overlay.TagFilterContract
import com.illiarb.catchup.features.home.overlay.showTagFilterOverlay
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.summarizer.ui.SummaryScreen
import com.illiarb.catchup.summarizer.ui.showSummaryOverlay
import com.illiarb.catchup.uikit.core.components.ErrorStateKind
import com.illiarb.catchup.uikit.core.components.FullscreenErrorState
import com.illiarb.catchup.uikit.core.components.FullscreenState
import com.illiarb.catchup.uikit.core.components.HorizontalList
import com.illiarb.catchup.uikit.core.components.LocalLottieAnimation
import com.illiarb.catchup.uikit.core.components.LottieAnimationType
import com.illiarb.catchup.uikit.core.components.SelectableCircleAvatar
import com.illiarb.catchup.uikit.core.components.SelectableCircleAvatarLoading
import com.illiarb.catchup.uikit.core.components.cell.ArticleCell
import com.illiarb.catchup.uikit.core.components.cell.ArticleLoadingCell
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.acsb_action_filter
import com.illiarb.catchup.uikit.resources.acsb_action_settings
import com.illiarb.catchup.uikit.resources.home_articles_empty_action
import com.illiarb.catchup.uikit.resources.home_articles_empty_title
import com.illiarb.catchup.uikit.resources.home_screen_title
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.overlay.OverlayEffect
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.collections.immutable.ImmutableList
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.stringResource

@Inject
public class HomeScreenFactory : Ui.Factory {
  override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
    return when (screen) {
      is HomeScreen -> {
        ui<HomeScreen.State> { state, _ ->
          HomeScreen(state)
        }
      }

      else -> null
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
private fun HomeScreen(state: HomeScreen.State) {
  ContentWithOverlays {
    val eventSink = state.eventSink

    val bottomSheetContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val bottomBarBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
    val bottomBarAlpha = 1 - bottomBarBehavior.state.collapsedFraction

    val topBarBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val hazeState = remember { HazeState() }
    val hazeStyle = HazeMaterials.thin(MaterialTheme.colorScheme.surface)

    when {
      state.filtersShowing -> {
        OverlayEffect(Unit) {
          val result = showTagFilterOverlay(
            TagFilterContract.Input(
              allTags = state.articlesTags,
              selectedTags = state.selectedTags,
              containerColor = bottomSheetContainerColor,
            ),
          )
          eventSink.invoke(Event.TagFilterResult(result))
        }
      }

      state.articleSummaryToShow != null -> {
        OverlayEffect(Unit) {
          val result = showSummaryOverlay(
            SummaryScreen(
              state.articleSummaryToShow.id,
              context = SummaryScreen.Context.HOME,
            ),
          )
          eventSink.invoke(Event.SummaryResult(result))
        }
      }
    }

    Scaffold(
      modifier = Modifier
        .nestedScroll(bottomBarBehavior.nestedScrollConnection)
        .nestedScroll(topBarBehavior.nestedScrollConnection),
      topBar = {
        CenterAlignedTopAppBar(
          scrollBehavior = topBarBehavior,
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
          ),
          modifier = Modifier.hazeEffect(state = hazeState, style = hazeStyle),
          title = {
            Text(stringResource(Res.string.home_screen_title))
          },
          actions = {
            IconButton(onClick = { eventSink.invoke(Event.SettingsClicked) }) {
              Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(Res.string.acsb_action_settings),
              )
            }
          },
        )
      },
      bottomBar = {
        BottomAppBar(
          modifier = Modifier.hazeEffect(state = hazeState, style = hazeStyle),
          scrollBehavior = bottomBarBehavior,
          containerColor = Color.Transparent,
          actions = {
            AnimatedContent(
              targetState = state.newsSources,
              transitionSpec = { fadeIn().togetherWith(fadeOut()) },
              contentKey = { it is Async.Content },
            ) { targetState ->
              when (targetState) {
                is Async.Loading -> {
                  NewsSourcesLoading(Modifier.padding(start = 16.dp))
                }

                is Async.Content -> {
                  NewsSourcesContent(
                    newsSources = targetState.content,
                    selectedTabIndex = state.selectedNewsSourceIndex,
                    onTabClick = { eventSink.invoke(Event.TabClicked(it)) },
                    modifier = Modifier
                      .padding(start = 16.dp)
                      .alpha(bottomBarAlpha)
                  )
                }

                else -> Unit
              }
            }
          },
          floatingActionButton = {
            IconButton(
              modifier = Modifier.alpha(bottomBarAlpha),
              onClick = { eventSink.invoke(Event.FiltersClicked) }
            ) {
              Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = stringResource(Res.string.acsb_action_filter),
              )
            }
          }
        )
      },
    ) { innerPadding ->
      AnimatedContent(
        contentKey = { it is Async.Content },
        targetState = state.articles,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
      ) { targetState ->
        when {
          targetState is Async.Error || state.newsSources is Async.Error -> {
            FullscreenErrorState(Modifier.padding(innerPadding), ErrorStateKind.UNKNOWN) {
              eventSink.invoke(Event.ErrorRetryClicked)
            }
          }

          targetState is Async.Loading -> {
            ArticlesLoading(contentPadding = innerPadding)
          }

          targetState is Async.Content -> {
            if (targetState.content.isEmpty()) {
              ArticlesEmpty(contentPadding = innerPadding) {
                eventSink.invoke(Event.ErrorRetryClicked)
              }
            } else {
              ArticlesContent(
                modifier = Modifier.hazeSource(state = hazeState),
                contentPadding = innerPadding,
                articles = targetState.content,
                eventSink = eventSink,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun NewsSourcesLoading(modifier: Modifier = Modifier) {
  LazyRow(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    items(
      count = 3,
      itemContent = { index ->
        SelectableCircleAvatarLoading(selected = index == 0)
      },
    )
  }
}

@Composable
private fun NewsSourcesContent(
  modifier: Modifier = Modifier,
  newsSources: ImmutableList<NewsSource>,
  selectedTabIndex: Int,
  onTabClick: (NewsSource) -> Unit,
) {
  HorizontalList(
    modifier = modifier,
    items = newsSources,
    keyProvider = { _, source -> source.kind.key },
    itemContent = { index, source ->
      SelectableCircleAvatar(
        imageUrl = source.imageUrl.url,
        selected = index == selectedTabIndex,
        fallbackText = source.kind.key.uppercase(),
        onClick = { onTabClick.invoke(source) }
      )
    },
  )
}

@Composable
private fun ArticlesLoading(modifier: Modifier = Modifier, contentPadding: PaddingValues) {
  LazyColumn(modifier = modifier, contentPadding = contentPadding) {
    items(
      count = 5,
      itemContent = {
        ArticleLoadingCell()
      }
    )
  }
}

@Composable
private fun ArticlesEmpty(
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues,
  onRefreshClick: () -> Unit,
) {
  Column(
    modifier = modifier.fillMaxSize().padding(contentPadding),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    FullscreenState(
      title = stringResource(Res.string.home_articles_empty_title),
      buttonText = stringResource(Res.string.home_articles_empty_action),
      onButtonClick = onRefreshClick,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clip(shape = RoundedCornerShape(size = 24.dp))
        .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
      LocalLottieAnimation(
        modifier = Modifier.size(200.dp),
        animationType = LottieAnimationType.ARTICLES_EMPTY,
      )
    }
  }
}

@Composable
private fun ArticlesContent(
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues,
  articles: SnapshotStateList<Article>,
  eventSink: (Event) -> Unit,
) {
  LazyColumn(modifier, contentPadding = contentPadding) {
    items(
      items = articles,
      key = { article -> article.id },
      itemContent = { article ->
        ArticleCell(
          title = article.title,
          author = article.authorName,
          caption = article.tags.firstOrNull()?.value.orEmpty(),
          saved = article.saved,
          onClick = {
            eventSink.invoke(Event.ArticleClicked(article))
          },
          onBookmarkClick = {
            eventSink.invoke(Event.ArticleBookmarkClicked(article))
          },
          onSummarizeClick = {
            eventSink.invoke(Event.ArticleSummarizeClicked(article))
          },
        )
        HorizontalDivider(
          color = DividerDefaults.color.copy(alpha = 0.5f)
        )
      }
    )
  }
}