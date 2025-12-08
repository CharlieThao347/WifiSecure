/*
This file contains the code for the ConnectedWifi View Model.
Holds and updates UI logic and state. This is the presentation
layer for the connected wifi portion of the wifi screen.
 */

package com.example.wifisecure.wifi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Class that holds the details of the connected Wifi.
data class ConnectedWifiDetails(
    val ssid: String? = null,
    val bssid: String? = null,
    val rssi: Int? = null,
    val encryption: String? = null,
    val frequency: String? = null
)

// ConnectedWifi View Model, which takes a ConnectedWifi object as an argument.
class ConnectedWifiViewModelViewModel (
    private val connectedWifi: ConnectedWifi
) : ViewModel() {

    // Stores connected wifi details. Only accessible by the ViewModel.
    private val _connectedWifiDetails = MutableStateFlow(ConnectedWifiDetails())
    // Read-only version of _connectedWifiDetails that is used by the UI layer.
    val connectedWifiDetails: StateFlow<ConnectedWifiDetails> = _connectedWifiDetails
}