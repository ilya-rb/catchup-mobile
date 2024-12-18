package com.illiarb.catchup.features.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.components.SwitchCell
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.acsb_navigation_back
import com.illiarb.catchup.uikit.resources.acsb_switch_checked
import com.illiarb.catchup.uikit.resources.settings_dynamic_colors_subtitle
import com.illiarb.catchup.uikit.resources.settings_dynamic_colors_title
import com.illiarb.catchup.uikit.resources.settings_screen_title
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.stringResource

@Inject
public class SettingsScreenFactory : Ui.Factory {
  override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
    return when (screen) {
      is SettingsScreen -> {
        ui<SettingsScreenContract.State> { state, _ ->
          SettingsScreen(state)
        }
      }

      else -> null
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(state: SettingsScreenContract.State) {
  val events = state.events

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(stringResource(Res.string.settings_screen_title))
        },
        navigationIcon = {
          IconButton(onClick = { events.invoke(SettingsScreenContract.Event.NavigationIconClick) }) {
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
      SwitchCell(
        checked = state.dynamicColorsEnabled,
        title = stringResource(Res.string.settings_dynamic_colors_title),
        subtitle = stringResource(Res.string.settings_dynamic_colors_subtitle),
      ) { checked ->
        events.invoke(SettingsScreenContract.Event.MaterialColorsToggleChecked(checked))
      }
    }
  }
}

