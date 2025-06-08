package com.illiarb.catchup.features.reader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.features.reader.ReaderScreen.Event
import com.illiarb.catchup.summarizer.ui.SummaryScreen
import com.illiarb.catchup.summarizer.ui.showSummaryOverlay
import com.illiarb.catchup.uikit.core.components.TopAppBarTitleLoading
import com.illiarb.catchup.uikit.core.components.WebView
import com.illiarb.catchup.uikit.core.components.cell.ArticleReaderLoading
import com.illiarb.catchup.uikit.core.components.cell.FullscreenErrorState
import com.illiarb.catchup.uikit.core.components.popup.OpenInBrowserAction
import com.illiarb.catchup.uikit.core.components.popup.ShareAction
import com.illiarb.catchup.uikit.core.components.popup.SummarizeAction
import com.illiarb.catchup.uikit.core.configuration.getScreenWidth
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.acsb_action_more
import com.illiarb.catchup.uikit.resources.acsb_navigation_back
import com.slack.circuit.overlay.OverlayEffect
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.stringResource

@Inject
public class ReaderScreenFactory : Ui.Factory {
  override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
    return when (screen) {
      is ReaderScreen -> {
        ui<ReaderScreen.State> { state, modifier ->
          ReaderScreen(modifier, screen, state)
        }
      }

      else -> null
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderScreen(
  modifier: Modifier,
  screen: ReaderScreen,
  state: ReaderScreen.State,
) {
  val eventSink = state.eventSink

  val progressColor = MaterialTheme.colorScheme.primary
  val progressSize = 8.dp
  val screenWidth = getScreenWidth()

  val contentScrollState = rememberScrollState()
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
  val percent = (contentScrollState.value.toFloat() / contentScrollState.maxValue.toFloat()) * 100f
  val scrolledWidth = percent / 100f * screenWidth.value

  if (state.summaryShowing) {
    OverlayEffect(Unit) {
      showSummaryOverlay(
        SummaryScreen(articleId = screen.articleId, context = SummaryScreen.Context.READER)
      )
      eventSink.invoke(Event.SummarizeCloseClicked)
    }
  }

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      TopAppBar(
        modifier = Modifier.drawWithContent {
          drawContent()
          drawRect(
            brush = SolidColor(progressColor),
            size = Size(
              height = progressSize.value,
              width = scrolledWidth.coerceIn(0f, this.size.width),
            ),
            topLeft = Offset(
              x = 0f,
              y = this.size.height - progressSize.value,
            )
          )
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
          scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        navigationIcon = {
          IconButton(onClick = { eventSink.invoke(Event.NavigationIconClicked) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(Res.string.acsb_navigation_back),
            )
          }
        },
        actions = {
          Box {
            IconButton(
              onClick = { eventSink.invoke(Event.TopBarMenuClicked) },
              enabled = state.article is Async.Content,
            ) {
              Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(Res.string.acsb_action_more),
              )
            }

            DropdownMenu(
              expanded = state.topBarPopupShowing,
              onDismissRequest = { eventSink.invoke(Event.TopBarMenuDismissed) },
            ) {
              OpenInBrowserAction {
                eventSink.invoke(Event.TopBarOpenInBrowser)
              }
              SummarizeAction {
                eventSink.invoke(Event.TopBarSummarize)
              }
              ShareAction {
                eventSink.invoke(Event.TopBarShare)
              }
            }
          }
        },
        title = {
          when (val content = state.article) {
            is Async.Loading, is Async.Error -> TopAppBarTitleLoading()
            is Async.Content -> {
              Column {
                Text(
                  text = content.content.title,
                  style = MaterialTheme.typography.bodyLarge,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
                Text(
                  text = content.content.link.url,
                  style = MaterialTheme.typography.bodySmall,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            }
          }
        },
      )
    },
  ) { innerPadding ->
    Box(Modifier.fillMaxSize().padding(innerPadding)) {
      when (state.article) {
        is Async.Loading -> {
          ArticleReaderLoading()
        }

        is Async.Error -> {
          FullscreenErrorState {
            eventSink.invoke(Event.ErrorRetryClicked)
          }
        }

        is Async.Content -> {
          WebView(
            url = state.article.content.link.url,
            modifier = modifier.fillMaxSize().verticalScroll(contentScrollState),
          )
        }
      }
    }
  }
}