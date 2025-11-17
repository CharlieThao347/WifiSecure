/*
This file contains the UI code for the main page.
 */

package com.example.wifisecure.mainactivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Wifi1Bar
import androidx.compose.material.icons.filled.Wifi2Bar
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
Composable that renders the main page.
Renders the app bar and
calls other composables that render everything below the app bar.
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass,
    wifiList: List<CleanedWifiList>,
    onScan: () -> Unit,
    isScanning: Boolean
) {
    val sizing = rememberSizing(windowSizeClass)
    // Renders the app bar.
    Scaffold(
        topBar = {
            TopAppBar(
                sizing,
                onScan,
                isScanning
            )
        }
    )
    // Renders everything below the app bar.
    { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            FoundText(
                sizing,
                wifiList
            )
            WifiList(
                sizing,
                wifiList
            )
            VPNButton(sizing)
        }
    }
}

// Composable that renders the app bar.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    sizing: Sizing,
    onScan: () -> Unit,
    isScanning: Boolean
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.fillMaxHeight(sizing.appBarWidth),
        // Renders the "WifiSecure" text that appears in the center of the app bar.
        title = {
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    textAlign = TextAlign.Center, text = "WifiSecure",
                    fontSize = sizing.appBarText
                )
            }
        },
        // Renders the "Scan" button that appears in the right of the app bar.
        actions = {
                Row (
                    modifier = Modifier.fillMaxHeight().width(sizing.actionsBox),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    ScanButtons(sizing, onScan, isScanning)
                }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF27619b),
            titleContentColor = Color.White
        )
    )
}

// Composable that renders the two scan buttons.
@Composable
fun ScanButtons(
    sizing: Sizing,
    onScan: () -> Unit,
    isScanning: Boolean)
{
    // State for showing/hiding the manual scan button.
    var showManualScanButton by remember { mutableStateOf(true) }
    // Used for launching the coroutine that performs the Wi-Fi scan every 5 seconds.
    val scope = rememberCoroutineScope()

    // Displays the automatic scan button.
    Button(
        enabled = !isScanning,
        onClick = {
            // Hides the manual scan button and toggles on automatic Wi-Fi scanning.
            if (showManualScanButton) {
                showManualScanButton = false
                scope.launch{
                    while (!showManualScanButton && !isScanning) {
                        // Invokes the Wi-Fi scanning function.
                        onScan()
                        // 5 second delay.
                        delay(5000L)
                    }
                }
            }
            // Unhides the manual scan button and toggles off automatic Wi-Fi scanning.
            else {
                showManualScanButton = true
            }
        },
        modifier = Modifier
            .padding(end = sizing.scanButtonPaddingEnd)
            .size(sizing.scanButtonWidth, sizing.scanButtonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6082B6),
            contentColor = Color.White
        )
    ) {
        Text(
            if (isScanning && !showManualScanButton) {
                "Scan…"
            } else {
                "Auto"
            },
            fontSize = sizing.scanButtonText
        )
    }

    if (showManualScanButton) {
        // Displays the manual scan button.
        Button(
            onClick = onScan,
            enabled = !isScanning,
            modifier = Modifier
                .padding(end = sizing.scanButtonPaddingEnd)
                .size(sizing.scanButtonWidth, sizing.scanButtonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00A300),
                contentColor = Color.White
            )
        ) {
            Text(
                if (isScanning) {
                    "Scan…"
                } else {
                    "Scan"
                },
                fontSize = sizing.scanButtonText
            )
        }
    }
}

// Composable that renders "Found" text.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundText(
    sizing: Sizing,
    wifiList: List<CleanedWifiList>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Found(${wifiList.size})",
            modifier = Modifier.padding(sizing.foundPaddingWidth,
                sizing.foundPaddingHeight),
            fontSize = sizing.foundText,
            fontWeight = FontWeight.Bold
        )
    }
}

// Composable that renders the list of WiFi cards.
@Composable
fun WifiList(
    sizing: Sizing,
    wifiList: List<CleanedWifiList>
) {
    // Structures the list in a column.
    LazyColumn(
        modifier = Modifier
            .padding(top = sizing.wifiListPaddingTop)
            .fillMaxHeight(sizing.wifiListHeight),
        verticalArrangement = Arrangement.spacedBy(sizing.wifiListSpacer),
    ) {
        // Iterates through the cleaned scan results and displays a card for each result.
        items(wifiList, key = { it.ssid }) { result ->
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = sizing.cardElevation
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sizing.cardHeight)
                    .padding(horizontal = sizing.cardPaddingWidth),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE0E0E0)
                )
            )
            // Displays the Wi-Fi metrics (SSID, BSSID, RSSI, Encryption, and Frequency)
            // inside the card.
            {
                DisplaySSID(sizing, result)
                Row {
                    DisplayBSSID(sizing, result)
                    DisplayRSSI(sizing, result)
                }
                Row {
                    DisplayEncryption(sizing, result)
                    DisplayFrequency(sizing, result)
                }
            }
        }
    }
}

// Composable that renders the SSID metric.
@Composable
fun DisplaySSID(sizing: Sizing, result: CleanedWifiList) {
    // State for dialog pop up.
    var showSSIDDialog by remember { mutableStateOf(false) }
    // Shows "SSID" with blue text color and underlined.
    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF0000FF),
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("SSID")
            }
            append(": ${result.ssid}")
        },
        modifier = Modifier
            .padding(sizing.ssidPaddingHorizontal, sizing.ssidPaddingVertical)
            .clickable { showSSIDDialog = true },
        fontSize = sizing.ssidText,
        fontWeight = FontWeight.Bold
    )
    // Shows dialog pop up.
    if (showSSIDDialog) {
        AlertDialog(
            title = {
                Text(text = "What is the SSID?")
            },
            text = {
                Text(
                    text = "The SSID (Service Set Identifier) is the name of the Wi-Fi network.",
                    fontSize = sizing.dialogText
                )
            },
            onDismissRequest = {
                showSSIDDialog = false
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

// Composable that renders the BSSID metric.
@Composable
fun DisplayBSSID(sizing: Sizing, result: CleanedWifiList) {
    // State for dialog pop up.
    var showBSSIDDialog by remember { mutableStateOf(false) }
    // Shows "BSSID" with blue text color and underlined.
    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF0000FF),
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("BSSID")
            }
            append(": ${result.bssid}")
        },
        modifier = Modifier
            .padding(sizing.bssidPaddingHorizontal, sizing.bssidPaddingVertical)
            .clickable { showBSSIDDialog = true },
        fontSize = sizing.subfieldText,
    )
    // Shows dialog pop up.
    if (showBSSIDDialog) {
        AlertDialog(
            title = {
                Text(text = "What is the BSSID?")
            },
            text = {
                Text(
                    text = "The BSSID (Basic Service Set Identifier), also known as the MAC address, is a unique identifier for a specific Wi-Fi access point. This identifier ensures devices connect to the correct access point, especially when multiple access points share the same SSID.",
                    fontSize = sizing.dialogText
                )
            },
            onDismissRequest = {
                showBSSIDDialog = false
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

// Composable that renders the RSSI metric.
@Composable
fun DisplayRSSI(sizing: Sizing, result: CleanedWifiList) {
    // State for dialog pop up.
    var showRSSIDialog by remember { mutableStateOf(false) }
    // Shows "RSSI" with blue text color and underlined.
    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF0000FF),
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("RSSI")
            }
            append(": ${result.rssi} dBm")
        },
        modifier = Modifier
            .padding(sizing.rssiPaddingHorizontal, sizing.rssiPaddingVertical)
            .clickable { showRSSIDialog = true },
        fontSize = sizing.subfieldText,
    )
    // Shows dialog pop up.
    if (showRSSIDialog) {
        AlertDialog(
            title = {
                Text(text = "What is the RSSI?")
            },
            text = {
                Text(
                    text = "The RSSI (Received Signal Strength Indicator) is a measurement of how well your device can hear a signal from an access point. It is measured in a negative number of dBm, with values closer to 0 being stronger and better, and values closer to -100 being weaker. A higher RSSI value indicates a stronger, more reliable connection, while a lower value indicates a weaker connection.",
                    fontSize = sizing.dialogText
                )
            },
            onDismissRequest = {
                showRSSIDialog = false
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
    // Displays different number of Wi-Fi bars based on the RSSI value.
    val icon = when (result.rssi) {
        in -999..-86 -> Icons.Filled.WifiOff
        in -85..-71 -> Icons.Filled.Wifi1Bar
        in -70..-60 -> Icons.Filled.Wifi2Bar
        else -> Icons.Filled.Wifi
    }
    Icon(
        imageVector = icon,
        contentDescription = "Wi-Fi Signal Strength Icon"
    )
}

// Composable that renders the Encryption metric.
@Composable
fun DisplayEncryption(sizing: Sizing, result: CleanedWifiList) {
    // State for dialog pop up
    var showEncryptionDialog by remember { mutableStateOf(false) }

    // Shows "Encryption" with blue text color and underlined.
    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF0000FF),
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Encryption")
            }
            append(": ${result.encryption}")
        },
        modifier = Modifier
            .padding(sizing.encryptionPaddingHorizontal, sizing.encryptionPaddingVertical)
            .clickable { showEncryptionDialog = true },
        fontSize = sizing.subfieldText,
    )
    // Shows dialog pop up.
    if (showEncryptionDialog) {
        AlertDialog(
            title = {
                Text(text = "Main types of Wi-Fi encryption")
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(
                            text = "Open:\n" + "This indicates that no encryption is used. Connection to open networks do not require a password. Stay safe on these networks by using a VPN. If visiting a website, ensure the website is using HTTPS.\n\n\n" + "WEP (Wired Equivalent Privacy):\n" +
                                    "This is the oldest and weakest protocol. It only uses basic encryption and is highly vulnerable to attacks.\n\n\n" +
                                    "WPA (Wi-Fi Protected Access):\n" + "An improvement over WEP that uses stronger encryption. However, it is still considered insecure by modern standards.\n\n\n" + "WPA2 (Wi-Fi Protected Access 2):\n" + "The most common Wi-Fi encryption type that uses the robust Advanced Encryption Standard (AES). It is considered secure.\n\n\n"
                                    + "WPA3 (Wi-Fi Protected Access 3):\n" + "The latest and most secure standard, offering stronger encryption and better protection than the previous protocols.",
                            fontSize = sizing.dialogText
                        )
                    }
            },
            modifier = Modifier.fillMaxHeight(0.525f),
            onDismissRequest = {
                showEncryptionDialog = false
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

// Composable that renders the Frequency metric.
@Composable
fun DisplayFrequency(sizing: Sizing, result: CleanedWifiList) {
    // State for dialog pop up
    var showFrequencyBandDialog by remember { mutableStateOf(false) }
    // Shows "Frequency" with blue text color and underlined.
    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF0000FF),
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Frequency Band")
            }
            append(": ${result.frequency}")
        },
        modifier = Modifier
            .padding(sizing.frequencyPaddingHorizontal, sizing.frequencyPaddingVertical)
            .clickable { showFrequencyBandDialog = true },
        fontSize = sizing.subfieldText,
    )
    // Shows dialog pop up.
    if (showFrequencyBandDialog) {
        AlertDialog(
            title = {
                Text(text = "What is the Frequency Band?")
            },
            text = {
                Text(
                    text = "The Frequency Band refers to the range of radio waves that the wireless network uses to transmit data between devices. There are two main frequency bands, 2.4GHz and 5Ghz.\n\n" + "Compared to 5Ghz, 2.4GHz has longer range and is better at penetrating solid objects like walls. However, it is generally slower than 5GHz. Compared to 2.4GHz, 5GHz has less range, but it is generally faster.",
                    fontSize = sizing.dialogText
                )
            },
            onDismissRequest = {
                showFrequencyBandDialog = false
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

// Composable that renders the VPN toggle button.
@Composable
fun VPNButton(sizing: Sizing) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(sizing.vpnButtonPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {},
            shape = CircleShape,
            modifier = Modifier.size(sizing.vpnButton),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF27619b),
                contentColor = Color.White
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.PowerSettingsNew,
                contentDescription = "Power Button Icon",
                modifier = Modifier.size(sizing.powerIcon)
            )
        }
        Spacer(modifier = Modifier.height(sizing.vpnSpacerHeight))
        Text(
            text = "Connect to VPN",
            fontSize = sizing.vpnText,
            fontWeight = FontWeight.Bold
        )
    }
}