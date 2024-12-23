package com.illiarb.catchup.features.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.core.appinfo.DebugConfig
import com.illiarb.catchup.core.arch.CommonParcelable
import com.illiarb.catchup.core.arch.CommonParcelize
import com.illiarb.catchup.features.settings.SettingsScreen.Event
import com.illiarb.catchup.uikit.core.components.cell.RowCell
import com.illiarb.catchup.uikit.core.components.cell.SwitchCell
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.acsb_navigation_back
import com.illiarb.catchup.uikit.resources.settings_dark_theme_title
import com.illiarb.catchup.uikit.resources.settings_dynamic_colors_subtitle
import com.illiarb.catchup.uikit.resources.settings_dynamic_colors_title
import com.illiarb.catchup.uikit.resources.settings_screen_title
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.stringResource

@CommonParcelize
public data object SettingsScreen : Screen, CommonParcelable {

  @Stable
  internal data class State(
    val events: (Event) -> Unit,
    val dynamicColorsEnabled: Boolean,
    val darkThemeEnabled: Boolean,
    val debugSettings: DebugConfig?,
  ) : CircuitUiState

  internal sealed interface Event {
    data object NavigationIconClick : Event
    data class MaterialColorsToggleChecked(val checked: Boolean) : Event
    data class DarkThemeEnabledChecked(val checked: Boolean) : Event
    data class NetworkDelayChanged(val checked: Boolean) : Event
  }
}

@Inject
public class SettingsScreenFactory : Ui.Factory {
  override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
    return when (screen) {
      is SettingsScreen -> {
        ui<SettingsScreen.State> { state, _ ->
          SettingsScreen(state)
        }
      }

      else -> null
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(state: SettingsScreen.State) {
  val events = state.events

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(stringResource(Res.string.settings_screen_title))
        },
        navigationIcon = {
          IconButton(onClick = { events.invoke(Event.NavigationIconClick) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(Res.string.acsb_navigation_back),
            )
          }
        }
      )
    }
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
      Column {
        SwitchCell(
          switchChecked = state.dynamicColorsEnabled,
          text = stringResource(Res.string.settings_dynamic_colors_title),
          subtitle = stringResource(Res.string.settings_dynamic_colors_subtitle),
          onChecked = { checked ->
            events.invoke(Event.MaterialColorsToggleChecked(checked))
          }
        )
        SwitchCell(
          switchChecked = state.darkThemeEnabled,
          text = stringResource(Res.string.settings_dark_theme_title),
          subtitle = null,
          onChecked = { checked ->
            events.invoke(Event.DarkThemeEnabledChecked(checked))
          }
        )
        if (state.debugSettings != null) {
          RowCell(
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            text = "Debug",
            startIcon = Icons.Filled.BugReport,
            startIconContentDescription = "Debug",
          )

          HorizontalDivider()

          DebugSettings(
            settings = state.debugSettings,
            onNetworkDelayChanged = { events.invoke(Event.NetworkDelayChanged(it)) }
          )
        }
      }
    }
  }
}

@Composable
private fun DebugSettings(
  modifier: Modifier = Modifier,
  settings: DebugConfig,
  onNetworkDelayChanged: (Boolean) -> Unit,
) {
  SwitchCell(
    modifier = modifier,
    switchChecked = settings.networkDelayEnabled,
    text = "Network request delay",
    subtitle = "Delay each network request by 3 sec",
    onChecked = onNetworkDelayChanged,
  )
}

