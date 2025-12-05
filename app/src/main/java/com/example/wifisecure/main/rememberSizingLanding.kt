/*
This file contains the code for making
the UI adapt to different screen sizes for the landing pages.
 */

package com.example.wifisecure.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

// Items that could have different sizes based on the screen size.
data class LandingSizing(
    val lottieSize: Dp,
    val titleSize: TextUnit,
    val spacerHeight: Dp,
    val buttonHeight: Dp
    )

// Composable that applies different item sizings based on the screen size.
@Composable
fun rememberSizingLanding(window: WindowSizeClass): LandingSizing = remember(window) {
    when (window.widthSizeClass) {
        WindowWidthSizeClass.Compact -> LandingSizing(
            lottieSize = 200.dp,
            titleSize = 25.sp,
            spacerHeight = 10.dp,
            buttonHeight = 43.dp

        )
        WindowWidthSizeClass.Medium -> LandingSizing(
            lottieSize = 300.dp,
            titleSize = 28.sp,
            spacerHeight = 20.dp,
            buttonHeight = 47.dp
        )
        WindowWidthSizeClass.Expanded -> LandingSizing(
            lottieSize = 300.dp,
            titleSize = 28.sp,
            spacerHeight = 20.dp,
            buttonHeight = 47.dp
        )
        // placeholder
        else -> LandingSizing(
            lottieSize = 300.dp,
            titleSize = 28.sp,
            spacerHeight = 20.dp,
            buttonHeight = 47.dp
        )
    }
}