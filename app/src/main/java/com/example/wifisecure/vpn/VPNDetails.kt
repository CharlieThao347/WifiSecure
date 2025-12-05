package com.example.wifisecure.vpn

/*
This file contains the UI code for displaying the VPN server details.
 */

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.wifisecure.main.VpnSizing

// Composable that displays the card containing VPN details.
@Composable
fun SelectableCard(sizing: VpnSizing,
                   detail: VpnDetails, onClick: () -> Unit,
                   updateSplitTunnel: (String, String) -> Unit,
                   updateIsChecked: (String, Boolean) -> Unit
) {

    // UI of the card changes depending if its selected or not.
    val cardColor = if (detail.isSelected) {
        Color(0xFFC8E6C9)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderStroke = if (detail.isSelected) {
        BorderStroke(width = 2.dp, color = Color(0xFF388E3C))
    } else {
        BorderStroke(0.1.dp, Color.Black)
    }

    Card(
        onClick = onClick,
        border = borderStroke,
        elevation = CardDefaults.cardElevation(
            defaultElevation = sizing.cardElevation
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(sizing.cardHeight)
            .padding(horizontal = sizing.cardPaddingWidth),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    )
    // Displays the VPN details inside the card.
    {
        DisplayName(sizing, detail)
        Row {
            DisplayIP(sizing, detail)
            Spacer(
                modifier = Modifier
                    .width(sizing.subfieldSpacer)
            )
            DisplaySplitTunneling(sizing, detail, updateSplitTunnel, updateIsChecked)
        }
        Row {
            DisplayCity(sizing, detail)
            Spacer(
                modifier = Modifier
                    .width(sizing.subfieldSpacer)
            )
            DisplayCountry(sizing, detail)
        }
    }
}

// Composable that displays name.
@Composable
fun DisplayName(sizing: VpnSizing, detail: VpnDetails) {
    Text(detail.name,
        modifier = Modifier
            .padding(sizing.namePaddingHorizontal, sizing.namePaddingVertical),
        fontSize = sizing.nameText,
        fontWeight = FontWeight.Bold
    )
}

// Composable that displays IP address.
@Composable
fun DisplayIP(sizing: VpnSizing, detail: VpnDetails) {
    Text(buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold
            )
        ) {
            append("IP")
        }
        append(": ${detail.ip}")
    },
        modifier = Modifier
            .padding(start = sizing.ipPaddingHorizontal, sizing.ipPaddingVertical)
            .widthIn(max = sizing.subfieldWidth)
            .fillMaxWidth(),
        fontSize = sizing.subfieldText,
    )
}

// Composable that displays split tunneling status.
@Composable
fun DisplaySplitTunneling(sizing: VpnSizing,
                          detail: VpnDetails,
                          updateSplitTunnel: (String, String) -> Unit,
                          updateIsChecked: (String, Boolean) -> Unit) {

    var showDialog by remember { mutableStateOf(false) }
    var submittedText by remember { mutableStateOf("No text submitted yet.") }

    // If split tunneling is on.
    if (detail.splitTunnelStatus) {
        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Split Tunneling: ")
                }
                append("ON")
            },
            modifier = Modifier
                .padding(
                    top = sizing.splitTunnelTextPaddingVertical
                ),
            fontSize = sizing.subfieldText,
        )
    }
    // If it is off.
    else{
        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Split Tunneling: ")
                }
                append("OFF")
            },
            modifier = Modifier
                .padding(
                    top = sizing.splitTunnelTextPaddingVertical
                ),
            fontSize = sizing.subfieldText,
        )
    }
    // Switch that toggles split tunneling.
    Switch(
        checked = detail.isChecked,
        onCheckedChange = { newState ->
            updateIsChecked(detail.name, newState)
            if (newState) {
                showDialog = true
            }
            else {
                updateSplitTunnel(detail.name, "0.0.0.0/0, ::/0")
            }
        },
        enabled = detail.isSelected,
        modifier = Modifier.padding(start = 20.dp)
    )
    // Dialog pop up for split tunneling.
    if (showDialog) {
        SplitTunnelDialog(
            onDismiss = {
                showDialog = false
                updateIsChecked(detail.name, false)
            },
            onConfirm = { input ->
                submittedText = input
                updateSplitTunnel(detail.name, submittedText)
                showDialog = false
            }
        )
    }
}

// Composable that displays the split tunneling dialog.
@Composable
fun SplitTunnelDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Allowed IPs",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text("Enter an IP address or range: 140.82.114.3/32, 10.0.0.0/24, etc.") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Button(
                        onClick = {
                            onConfirm(textInput)
                        },
                        enabled = textInput.isNotBlank()
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

// Composable that displays country.
@Composable
fun DisplayCountry(sizing: VpnSizing, detail: VpnDetails) {
    Text(buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold
            )
        ) {
            append("Country")
        }
        append(": ${detail.country}")
    },
        modifier = Modifier
            .padding(top = sizing.countryPaddingVertical),
        fontSize = sizing.subfieldText,
    )
}

// Composable that displays city.
@Composable
fun DisplayCity(sizing: VpnSizing, detail: VpnDetails) {
    Text(buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold
            )
        ) {
            append("City")
        }
        append(": ${detail.city}")
    },
        modifier = Modifier
            .padding(start = sizing.cityPaddingHorizontal, sizing.cityPaddingVertical)
            .widthIn(max = sizing.subfieldWidth)
            .fillMaxWidth(),
        fontSize = sizing.subfieldText,

    )
}