/*
This file contains the UI code for displaying the Wi-Fi metrics.
 */

package com.example.wifisecure.wifi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Wifi1Bar
import androidx.compose.material.icons.filled.Wifi2Bar
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.example.wifisecure.main.WifiSizing

// Composable that renders the SSID metric.
@Composable
fun DisplaySSID(sizing: WifiSizing, result: WifiList) {
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
fun DisplayBSSID(sizing: WifiSizing, result: WifiList) {
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
            .clickable { showBSSIDDialog = true }
            .widthIn(max = sizing.subfieldWidth)
            .fillMaxWidth(),
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
fun DisplayRSSI(sizing: WifiSizing, result: WifiList) {
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
fun DisplayEncryption(sizing: WifiSizing, result: WifiList) {
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
            .clickable { showEncryptionDialog = true }
            .widthIn(max = sizing.subfieldWidth)
            .fillMaxWidth(),
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
fun DisplayFrequency(sizing: WifiSizing, result: WifiList) {
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