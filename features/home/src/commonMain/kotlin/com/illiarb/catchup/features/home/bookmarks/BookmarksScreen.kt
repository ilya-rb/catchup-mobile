package com.illiarb.catchup.features.home.bookmarks

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.features.home.articles.ArticlesContent
import com.illiarb.catchup.features.home.articles.ArticlesEmpty
import com.illiarb.catchup.features.home.articles.ArticlesLoading
import com.illiarb.catchup.features.home.bookmarks.BookmarksScreen.Event
import com.illiarb.catchup.uikit.core.components.ErrorStateKind
import com.illiarb.catchup.uikit.core.components.FullscreenErrorState
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.acsb_navigation_back
import com.illiarb.catchup.uikit.resources.bookmarks_screen_title
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.stringResource

@Inject
public class BookmarksScreenFactory : Ui.Factory {
  override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
    return when (screen) {
      is BookmarksScreen -> {
        ui<BookmarksScreen.State> { state, _ ->
          BookmarksScreen(state)
        }
      }

      else -> null
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookmarksScreen(state: BookmarksScreen.State) {
  val articlesEventSink = state.articlesEventSink
  val eventSink = state.eventSink

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Text(stringResource(Res.string.bookmarks_screen_title))
        },
        navigationIcon = {
          IconButton(onClick = { eventSink.invoke(Event.NavigationButtonClicked) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Default.ArrowBack,
              contentDescription = stringResource(Res.string.acsb_navigation_back),
            )
          }
        },
      )
    }
  ) { innerPadding ->
    AnimatedContent(
      contentKey = { state.articlesStateKey() },
      targetState = state.articles,
      transitionSpec = { fadeIn().togetherWith(fadeOut()) },
    ) { targetState ->
      when (targetState) {
        is Async.Error -> {
          FullscreenErrorState(Modifier.padding(innerPadding), ErrorStateKind.UNKNOWN) {
            eventSink.invoke(Event.ErrorRetryClicked)
          }
        }

        is Async.Loading -> {
          ArticlesLoading(contentPadding = innerPadding)
        }

        is Async.Content -> {
          if (targetState.content.isEmpty()) {
            ArticlesEmpty(contentPadding = innerPadding, eventSink = articlesEventSink)
          } else {
            ArticlesContent(
              contentPadding = innerPadding,
              articles = targetState.content,
              eventSink = articlesEventSink,
            )
          }
        }
      }
    }
  }
}