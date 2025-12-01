/*
This file contains the code for making
the UI adapt to different screen sizes for the wifi page.
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
data class VpnSizing(
    val drawerWidth: Dp,
    val drawerPadding: Dp,
    val drawerSpacer: Dp,
    val accountTextPadding: Dp,
    val accountTextSize: TextUnit,
    val drawerTextPadding: Dp,
    val drawerTextSize: TextUnit,
    val logoutButtonPadding: Dp,
    val logoutButtonWidth: Dp,
    val logoutButtonHeight: Dp,

    val appBarWidth: Float,
    val appBarText: TextUnit,
    val menuIcon: Dp,
    val menuPaddingStart: Dp,

    val countText: TextUnit,
    val countPaddingWidth: Dp,
    val countPaddingHeight: Dp,

    val vpnListPaddingTop: Dp,
    val vpnListHeight: Float,
    val vpnListSpacer: Dp,

    val cardHeight: Dp,
    val cardElevation: Dp,
    val cardPaddingWidth: Dp,

    val nameText: TextUnit,
    val subfieldText: TextUnit,

    val namePaddingHorizontal: Dp,
    val namePaddingVertical: Dp,
    val ipPaddingHorizontal: Dp,
    val ipPaddingVertical: Dp,
    val splitTunnelTextPaddingHorizontal: Dp,
    val splitTunnelTextPaddingVertical: Dp,
    val splitTunnelIcon: Dp,
    val cityPaddingHorizontal: Dp,
    val cityPaddingVertical: Dp,
    val countryPaddingHorizontal: Dp,
    val countryPaddingVertical: Dp,

    val vpnButtonPadding: Dp,
    val vpnButton: Dp,
    val powerIcon: Dp,
    val vpnSpacerHeight: Dp,
    val vpnText: TextUnit
)

// Composable that applies different item sizings based on the screen size
@Composable
fun rememberSizingVpn(window: WindowSizeClass): VpnSizing = remember(window) {
    when (window.widthSizeClass) {
        WindowWidthSizeClass.Compact -> VpnSizing(
            drawerWidth = 200.dp,
            drawerPadding = 5.dp,
            drawerSpacer = 10.dp,
            accountTextPadding = 20.dp,
            accountTextSize = 20.sp,
            drawerTextPadding = 13.dp,
            drawerTextSize = 13.sp,
            logoutButtonPadding = 23.dp,
            100.dp,
            logoutButtonHeight = 40.dp,

            appBarWidth = .1f,
            appBarText = 25.sp,
            menuIcon = 35.dp,
            menuPaddingStart = 5.dp,

            countText = 17.sp,
            countPaddingWidth = 8.dp,
            countPaddingHeight = 8.dp,

            vpnListPaddingTop = 2.dp,
            vpnListHeight = .8f,
            vpnListSpacer = 10.dp,

            cardHeight = 100.dp,
            cardElevation = 10.dp,
            cardPaddingWidth = 20.dp,

            nameText = 18.sp,
            subfieldText = 15.sp,

            namePaddingHorizontal = 8.dp,
            namePaddingVertical = 6.dp,
            ipPaddingHorizontal = 20.dp,
            ipPaddingVertical = 6.dp,
            splitTunnelTextPaddingHorizontal = 15.dp,
            splitTunnelTextPaddingVertical = 8.dp,
            splitTunnelIcon = 20.dp,
            cityPaddingHorizontal = 20.dp,
            cityPaddingVertical = 9.dp,
            countryPaddingHorizontal = 20.dp,
            countryPaddingVertical = 9.dp,

            vpnButtonPadding = 10.dp,
            vpnButton = 90.dp,
            powerIcon = 90.dp,
            vpnSpacerHeight = 3.dp,
            vpnText = 18.sp
        )
        WindowWidthSizeClass.Medium -> VpnSizing(
            drawerWidth = 360.dp,
            drawerPadding = 16.dp,
            drawerSpacer = 12.dp,
            accountTextPadding = 30.dp,
            accountTextSize = 25.sp,
            drawerTextPadding = 20.dp,
            drawerTextSize = 18.sp,
            logoutButtonPadding = 20.dp,
            120.dp,
            logoutButtonHeight = 50.dp,

            appBarWidth = .1f,
            appBarText = 30.sp,
            menuIcon = 55.dp,
            menuPaddingStart = 10.dp,

            countText = 20.sp,
            countPaddingWidth = 8.dp,
            countPaddingHeight = 8.dp,

            vpnListPaddingTop = 2.dp,
            vpnListHeight = .8f,
            vpnListSpacer = 20.dp,

            cardHeight = 150.dp,
            cardElevation = 10.dp,
            cardPaddingWidth = 20.dp,

            nameText = 25.sp,
            subfieldText = 20.sp,

            namePaddingHorizontal = 8.dp,
            namePaddingVertical = 8.dp,
            ipPaddingHorizontal = 20.dp,
            ipPaddingVertical = 8.dp,
            splitTunnelTextPaddingHorizontal = 75.dp,
            splitTunnelTextPaddingVertical = 8.dp,
            splitTunnelIcon = 40.dp,
            cityPaddingHorizontal = 20.dp,
            cityPaddingVertical = 8.dp,
            countryPaddingHorizontal = 108.dp,
            countryPaddingVertical = 8.dp,

            vpnButtonPadding = 40.dp,
            vpnButton = 90.dp,
            powerIcon = 90.dp,
            vpnSpacerHeight = 3.dp,
            vpnText = 18.sp
        )
        WindowWidthSizeClass.Expanded -> VpnSizing(
            drawerWidth = 360.dp,
            drawerPadding = 16.dp,
            drawerSpacer = 12.dp,
            accountTextPadding = 30.dp,
            accountTextSize = 25.sp,
            drawerTextPadding = 20.dp,
            drawerTextSize = 18.sp,
            logoutButtonPadding = 20.dp,
            120.dp,
            logoutButtonHeight = 50.dp,

            appBarWidth = .125f,
            appBarText = 35.sp,
            menuIcon = 60.dp,
            menuPaddingStart = 10.dp,

            countText = 20.sp,
            countPaddingWidth = 8.dp,
            countPaddingHeight = 8.dp,

            vpnListPaddingTop = 2.dp,
            vpnListHeight = .8f,
            vpnListSpacer = 20.dp,

            cardHeight = 175.dp,
            cardElevation = 10.dp,
            cardPaddingWidth = 20.dp,

            nameText = 30.sp,
            subfieldText = 25.sp,

            namePaddingHorizontal = 8.dp,
            namePaddingVertical = 8.dp,
            ipPaddingHorizontal = 20.dp,
            ipPaddingVertical = 8.dp,
            splitTunnelTextPaddingHorizontal = 70.dp,
            splitTunnelTextPaddingVertical = 8.dp,
            splitTunnelIcon = 40.dp,
            cityPaddingHorizontal = 20.dp,
            cityPaddingVertical = 8.dp,
            countryPaddingHorizontal = 108.dp,
            countryPaddingVertical = 8.dp,

            vpnButtonPadding = 10.dp,
            vpnButton = 75.dp,
            powerIcon = 75.dp,
            vpnSpacerHeight = 3.dp,
            vpnText = 18.sp
        )
        // placeholder
        else -> VpnSizing(
            drawerWidth = 360.dp,
            drawerPadding = 16.dp,
            drawerSpacer = 12.dp,
            accountTextPadding = 30.dp,
            accountTextSize = 25.sp,
            drawerTextPadding = 20.dp,
            drawerTextSize = 18.sp,
            logoutButtonPadding = 20.dp,
            120.dp,
            logoutButtonHeight = 50.dp,

            appBarWidth = .125f,
            35.sp,
            menuIcon = 30.dp,
            menuPaddingStart = 10.dp,

            20.sp,
            countPaddingWidth = 8.dp,
            countPaddingHeight = 8.dp,

            vpnListPaddingTop = 2.dp,
            vpnListHeight = 0.8f,
            vpnListSpacer = 20.dp,

            175.dp,
            cardElevation = 5.dp,
            cardPaddingWidth = 20.dp,

            30.sp,
            subfieldText = 25.sp,

            namePaddingHorizontal = 8.dp,
            namePaddingVertical = 8.dp,
            ipPaddingHorizontal = 20.dp,
            ipPaddingVertical = 8.dp,
            splitTunnelTextPaddingHorizontal = 40.dp,
            splitTunnelTextPaddingVertical = 8.dp,
            splitTunnelIcon = 40.dp,
            cityPaddingHorizontal = 20.dp,
            cityPaddingVertical = 8.dp,
            countryPaddingHorizontal = 108.dp,
            countryPaddingVertical = 8.dp,

            vpnButtonPadding = 10.dp,
            75.dp,
            powerIcon = 75.dp,
            vpnSpacerHeight = 3.dp,
            vpnText = 18.sp)
    }
}