/*
This file contains the code for making the UI adapt to different screen sizes.
 */

package com.example.wifisecure.main

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
    val drawerWidth: Dp,
    val drawerPadding: Dp,
    val drawerSpacer: Dp,
    val accountTextPadding: Dp,
    val accountTextSize: TextUnit,
    val drawerVpnTextPadding: Dp,
    val drawerVpnTextSize: TextUnit,
    val logoutButtonPadding: Dp,
    val logoutButtonWidth: Dp,
    val logoutButtonHeight: Dp,


    val appBarWidth: Float,
    val appBarText: TextUnit,
    val menuIcon: Dp,
    val menuPaddingStart: Dp,
    val actionsBox: Dp,

    val scanButtonPaddingEnd: Dp,
    val scanButtonWidth: Dp,
    val scanButtonHeight: Dp,
    val scanButtonText: TextUnit,

    val foundText: TextUnit,
    val foundPaddingWidth: Dp,
    val foundPaddingHeight: Dp,

    val wifiListPaddingTop: Dp,
    val wifiListHeight: Float,
    val wifiListSpacer: Dp,

    val cardHeight: Dp,
    val cardElevation: Dp,
    val cardPaddingWidth: Dp,

    val ssidText: TextUnit,
    val subfieldText: TextUnit,
    val dialogText: TextUnit,

    val ssidPaddingHorizontal: Dp,
    val ssidPaddingVertical: Dp,
    val bssidPaddingHorizontal: Dp,
    val bssidPaddingVertical: Dp,
    val rssiPaddingHorizontal: Dp,
    val rssiPaddingVertical: Dp,
    val encryptionPaddingHorizontal: Dp,
    val encryptionPaddingVertical: Dp,
    val frequencyPaddingHorizontal: Dp,
    val frequencyPaddingVertical: Dp,

    val vpnButtonPadding: Dp,
    val vpnButton: Dp,
    val powerIcon: Dp,
    val vpnSpacerHeight: Dp,
    val vpnText: TextUnit
)

// Composable that applies different item sizings based on the screen size
@Composable
fun rememberSizing(window: WindowSizeClass): Sizing = remember(window) {
    when (window.widthSizeClass) {
        WindowWidthSizeClass.Compact -> Sizing(
            drawerWidth = 200.dp,
            drawerPadding = 5.dp,
            drawerSpacer = 10.dp,
            accountTextPadding = 20.dp,
            accountTextSize = 20.sp,
            drawerVpnTextPadding = 13.dp,
            drawerVpnTextSize = 13.sp,
            logoutButtonPadding = 23.dp,
            100.dp,
            logoutButtonHeight = 40.dp,

            appBarWidth = .1f,
            appBarText = 25.sp,
            menuIcon = 35.dp,
            menuPaddingStart = 5.dp,
            actionsBox = 150.dp,

            scanButtonPaddingEnd = 10.dp,
            scanButtonWidth = 60.dp,
            scanButtonHeight = 35.dp,
            scanButtonText = 10.sp,

            foundText = 17.sp,
            foundPaddingWidth = 8.dp,
            foundPaddingHeight = 8.dp,

            wifiListPaddingTop = 2.dp,
            wifiListHeight = 0.8f,
            wifiListSpacer = 10.dp,

            cardHeight = 120.dp,
            cardElevation = 5.dp,
            cardPaddingWidth = 20.dp,

            ssidText = 18.sp,
            subfieldText = 15.sp,
            dialogText = 13.sp,

            ssidPaddingHorizontal = 8.dp,
            ssidPaddingVertical = 6.dp,
            bssidPaddingHorizontal = 20.dp,
            bssidPaddingVertical = 6.dp,
            rssiPaddingHorizontal = 15.dp,
            rssiPaddingVertical = 6.dp,
            encryptionPaddingHorizontal = 20.dp,
            encryptionPaddingVertical = 9.dp,
            frequencyPaddingHorizontal = 20.dp,
            frequencyPaddingVertical = 9.dp,

            vpnButtonPadding = 10.dp,
            vpnButton = 90.dp,
            powerIcon = 90.dp,
            vpnSpacerHeight = 3.dp,
            vpnText = 18.sp
        )
        WindowWidthSizeClass.Medium -> Sizing(
            drawerWidth = 360.dp,
            drawerPadding = 16.dp,
            drawerSpacer = 12.dp,
            accountTextPadding = 30.dp,
            accountTextSize = 25.sp,
            drawerVpnTextPadding = 20.dp,
            drawerVpnTextSize = 18.sp,
            logoutButtonPadding = 20.dp,
            120.dp,
            logoutButtonHeight = 50.dp,

            appBarWidth = .1f,
            appBarText = 30.sp,
            menuIcon = 55.dp,
            menuPaddingStart = 10.dp,
            actionsBox = 300.dp,

            scanButtonPaddingEnd = 10.dp,
            scanButtonWidth = 120.dp,
            scanButtonHeight = 60.dp,
            scanButtonText = 20.sp,

            foundText = 20.sp,
            foundPaddingWidth = 8.dp,
            foundPaddingHeight = 8.dp,

            wifiListPaddingTop = 2.dp,
            wifiListHeight = 0.8f,
            wifiListSpacer = 20.dp,

            cardHeight = 150.dp,
            cardElevation = 5.dp,
            cardPaddingWidth = 20.dp,

            ssidText = 25.sp,
            subfieldText = 20.sp,
            dialogText = 18.sp,

            ssidPaddingHorizontal = 8.dp,
            ssidPaddingVertical = 8.dp,
            bssidPaddingHorizontal = 20.dp,
            bssidPaddingVertical = 8.dp,
            rssiPaddingHorizontal = 40.dp,
            rssiPaddingVertical = 8.dp,
            encryptionPaddingHorizontal = 20.dp,
            encryptionPaddingVertical = 8.dp,
            frequencyPaddingHorizontal = 108.dp,
            frequencyPaddingVertical = 8.dp,

            vpnButtonPadding = 40.dp,
            vpnButton = 90.dp,
            powerIcon = 90.dp,
            vpnSpacerHeight = 3.dp,
            vpnText = 18.sp
        )
        WindowWidthSizeClass.Expanded -> Sizing(
            drawerWidth = 360.dp,
            drawerPadding = 16.dp,
            drawerSpacer = 12.dp,
            accountTextPadding = 30.dp,
            accountTextSize = 25.sp,
            drawerVpnTextPadding = 20.dp,
            drawerVpnTextSize = 18.sp,
            logoutButtonPadding = 20.dp,
            120.dp,
            logoutButtonHeight = 50.dp,

            appBarWidth = .125f,
            appBarText = 35.sp,
            menuIcon = 60.dp,
            menuPaddingStart = 10.dp,
            actionsBox = 300.dp,

            scanButtonPaddingEnd = 10.dp,
            scanButtonWidth = 150.dp,
            scanButtonHeight = 60.dp,
            scanButtonText = 20.sp,

            foundText = 20.sp,
            foundPaddingWidth = 8.dp,
            foundPaddingHeight = 8.dp,

            wifiListPaddingTop = 2.dp,
            wifiListHeight = 0.8f,
            wifiListSpacer = 20.dp,

            cardHeight = 175.dp,
            cardElevation = 5.dp,
            cardPaddingWidth = 20.dp,

            ssidText = 30.sp,
            subfieldText = 25.sp,
            dialogText = 18.sp,

            ssidPaddingHorizontal = 8.dp,
            ssidPaddingVertical = 8.dp,
            bssidPaddingHorizontal = 20.dp,
            bssidPaddingVertical = 8.dp,
            rssiPaddingHorizontal = 40.dp,
            rssiPaddingVertical = 8.dp,
            encryptionPaddingHorizontal = 20.dp,
            encryptionPaddingVertical = 8.dp,
            frequencyPaddingHorizontal = 108.dp,
            frequencyPaddingVertical = 8.dp,

            vpnButtonPadding = 10.dp,
            vpnButton = 75.dp,
            powerIcon = 75.dp,
            vpnSpacerHeight = 3.dp,
            vpnText = 18.sp
        )
        // placeholder
        else -> Sizing(
            drawerWidth = 360.dp,
            drawerPadding = 16.dp,
            drawerSpacer = 12.dp,
            accountTextPadding = 30.dp,
            accountTextSize = 25.sp,
            drawerVpnTextPadding = 20.dp,
            drawerVpnTextSize = 18.sp,
            logoutButtonPadding = 20.dp,
            120.dp,
            logoutButtonHeight = 50.dp,

            appBarWidth = .125f,
            35.sp,
            menuIcon = 30.dp,
            menuPaddingStart = 10.dp,
            actionsBox = 300.dp,

            scanButtonPaddingEnd = 20.dp,
            scanButtonWidth = 150.dp,
            scanButtonHeight = 60.dp,
            scanButtonText = 20.sp,

            20.sp,
            foundPaddingWidth = 8.dp,
            foundPaddingHeight = 8.dp,

            wifiListPaddingTop = 2.dp,
            wifiListHeight = 0.8f,
            wifiListSpacer = 20.dp,

            175.dp,
            cardElevation = 5.dp,
            cardPaddingWidth = 20.dp,

            30.sp,
            subfieldText = 25.sp,
            dialogText = 18.sp,

            ssidPaddingHorizontal = 8.dp,
            ssidPaddingVertical = 8.dp,
            bssidPaddingHorizontal = 20.dp,
            bssidPaddingVertical = 8.dp,
            rssiPaddingHorizontal = 40.dp,
            rssiPaddingVertical = 8.dp,
            encryptionPaddingHorizontal = 20.dp,
            encryptionPaddingVertical = 8.dp,
            frequencyPaddingHorizontal = 108.dp,
            frequencyPaddingVertical = 8.dp,

            vpnButtonPadding = 10.dp,
            75.dp,
            powerIcon = 75.dp,
            vpnSpacerHeight = 3.dp,
            vpnText = 18.sp)
    }
}
