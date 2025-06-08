package com.illiarb.peek.summarizer.ui.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.summarizer.ui.SummaryScreen
import com.illiarb.peek.summarizer.ui.SummaryScreen.Event
import com.illiarb.peek.uikit.core.components.shimmer.ShimmerBox
import com.illiarb.peek.uikit.core.components.shimmer.ShimmerColumn
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_action_close
import com.illiarb.peek.uikit.resources.acsb_action_open_in_browser
import com.illiarb.peek.uikit.resources.acsb_icon_assistant
import com.illiarb.peek.uikit.resources.summary_loading_title
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.stringResource

@Inject
public class SummaryScreenFactory : Ui.Factory {
  override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
    return when (screen) {
      is SummaryScreen -> ui<SummaryScreen.State> { state, _ ->
        SummaryScreen(state, screen)
      }

      else -> null
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SummaryScreen(
  state: SummaryScreen.State,
  screen: SummaryScreen,
) {
  val eventSink = state.eventSink
  val containerColor = MaterialTheme.colorScheme.surfaceContainerLow

  Scaffold(
    containerColor = containerColor,
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor),
        windowInsets = WindowInsets(0, 0, 0, 0),
        title = {},
        navigationIcon = {
          IconButton(onClick = { eventSink.invoke(Event.NavigationIconClick) }) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = stringResource(Res.string.acsb_action_close),
            )
          }
        },
        actions = {
          when {
            screen.context == SummaryScreen.Context.READER -> Unit
            state.articleWithSummary is Async.Content -> {
              IconButton(
                onClick = {
                  eventSink.invoke(Event.OpenInReaderClick(state.articleWithSummary.content.article))
                },
                content = {
                  Icon(
                    imageVector = Icons.Filled.OpenInBrowser,
                    contentDescription = stringResource(Res.string.acsb_action_open_in_browser),
                  )
                },
              )
            }

            else -> {
              CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.padding(end = 16.dp).size(24.dp)
              )
            }
          }
        },
      )
    },
  ) { innerPadding ->
    Box(
      Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (val content = state.articleWithSummary) {
        is Async.Error -> TODO()
        is Async.Loading -> SummaryLoading()
        is Async.Content -> {
          Text(
            text = content.content.summary.content,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }
    }
  }
}

@Composable
internal fun SummaryLoading() {
  ShimmerColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = Icons.Filled.Assistant,
        contentDescription = stringResource(Res.string.acsb_icon_assistant),
      )
      Text(
        modifier = Modifier.padding(horizontal = 8.dp),
        text = stringResource(Res.string.summary_loading_title),
        style = MaterialTheme.typography.bodyLarge,
      )
    }
    ShimmerBox(
      modifier = Modifier
        .padding(top = 16.dp)
        .size(width = 250.dp, height = 16.dp),
    )
    ShimmerBox(
      modifier = Modifier
        .padding(top = 8.dp)
        .size(width = 150.dp, height = 16.dp),
    )
  }
}