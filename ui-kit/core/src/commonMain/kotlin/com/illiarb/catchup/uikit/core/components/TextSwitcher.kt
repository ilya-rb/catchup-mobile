package com.illiarb.catchup.uikit.core.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
public fun TextSwitcher(
    modifier: Modifier = Modifier,
    intervalMillis: Int = 2000,
    animationDuration: Int = 250,
    firstText: @Composable () -> Unit,
    secondText: @Composable () -> Unit,
) {
    var showFirst by remember { mutableStateOf(true) }
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val height = 40f // You may want to make this configurable or measure dynamically

    LaunchedEffect(Unit) {
        while (true) {
            delay(intervalMillis.toLong())
            // Animate out current (up and fade out)
            launch {
                offsetY.animateTo(
                    targetValue = -height,
                    animationSpec = tween(durationMillis = animationDuration)
                )
            }
            launch {
                alpha.animateTo(0f, animationSpec = tween(durationMillis = animationDuration))
            }
            delay(animationDuration.toLong())
            showFirst = false
            offsetY.snapTo(height)
            alpha.snapTo(0f)
            // Animate in next (from bottom and fade in)
            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = animationDuration)
                )
            }
            launch {
                alpha.animateTo(1f, animationSpec = tween(durationMillis = animationDuration))
            }
            delay(animationDuration.toLong())
            delay(intervalMillis.toLong())
            // Animate out current (down and fade out)
            launch {
                offsetY.animateTo(
                    targetValue = height,
                    animationSpec = tween(durationMillis = animationDuration)
                )
            }
            launch {
                alpha.animateTo(0f, animationSpec = tween(durationMillis = animationDuration))
            }
            delay(animationDuration.toLong())
            showFirst = true
            offsetY.snapTo(-height)
            alpha.snapTo(0f)
            // Animate in next (from top and fade in)
            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = animationDuration)
                )
            }
            launch {
                alpha.animateTo(1f, animationSpec = tween(durationMillis = animationDuration))
            }
            delay(animationDuration.toLong())
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (showFirst) {
            Box(modifier = Modifier.graphicsLayer(translationY = offsetY.value, alpha = alpha.value)) {
                firstText()
            }
        } else {
            Box(modifier = Modifier.graphicsLayer(translationY = offsetY.value, alpha = alpha.value)) {
                secondText()
            }
        }
    }
} 