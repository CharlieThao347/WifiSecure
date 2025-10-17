package com.example.wifisecure.screensizesupport

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.remember

// Items that could have different sizes based on the screen size.
data class Sizing(
    val appBarWidth: Float,
    val appBarText: TextUnit,
    val foundText: TextUnit,
    val filterIcon: Dp,
    val cardHeight: Dp,
    val ssidText: TextUnit,
    val subfieldText: TextUnit,
    val vpnButtonPadding: Dp,
    val vpnButton: Dp,
    val powerIcon: Dp,
    val vpnText: TextUnit
)

// Composable that applies different item sizings based on the screen size
@Composable
fun rememberSizing(window: WindowSizeClass): Sizing = remember(window) {
    when (window.widthSizeClass) {
        WindowWidthSizeClass.Compact -> Sizing(
            appBarWidth = .1f,
            appBarText = 25.sp,
            foundText = 18.sp,
            filterIcon = 30.dp,
            cardHeight = 120.dp,
            ssidText = 20.sp,
            subfieldText = 15.sp,
            vpnButtonPadding = 10.dp,
            vpnButton = 90.dp,
            powerIcon = 90.dp,
            vpnText = 18.sp
        )
        WindowWidthSizeClass.Medium -> Sizing(
            appBarWidth = .1f,
            appBarText = 30.sp,
            foundText = 20.sp,
            filterIcon = 40.dp,
            cardHeight = 150.dp,
            ssidText = 25.sp,
            subfieldText = 20.sp,
            vpnButtonPadding = 40.dp,
            vpnButton = 90.dp,
            powerIcon = 90.dp,
            vpnText = 18.sp
        )
        WindowWidthSizeClass.Expanded -> Sizing(
            appBarWidth = .125f,
            appBarText = 35.sp,
            foundText = 20.sp,
            filterIcon = 50.dp,
            cardHeight = 175.dp,
            ssidText = 30.sp,
            subfieldText = 25.sp,
            vpnButtonPadding = 10.dp,
            vpnButton = 75.dp,
            powerIcon = 75.dp,
            vpnText = 18.sp
        )
        // placeholder
        else -> Sizing(appBarWidth = .125f,35.sp,20.sp,50.dp,175.dp,30.sp, subfieldText = 25.sp, vpnButtonPadding = 10.dp, 75.dp,
                        powerIcon = 75.dp, vpnText = 18.sp)
    }
}
