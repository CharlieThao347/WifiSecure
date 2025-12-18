/*
This file contains the code for the ConnectedWifi View Model.
Holds and updates UI logic and state. This is the presentation
layer for the connected wifi portion of the wifi screen.
 */

package com.example.wifisecure.wifi

import android.content.Context
import android.net.wifi.WifiInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Class that holds the details of the connected Wifi.
data class ConnectedWifiDetails(
    val ssid: String? = null,
    val bssid: String? = null,
    val rssi: Int? = null,
    val encryption: String? = null,
    val frequency: String? = null,
    val connected: Boolean = false
)

// ConnectedWifi View Model, which takes a ConnectedWifi object as an argument.
class ConnectedWifiViewModel (
    private val connectedWifi: ConnectedWifi
) : ViewModel() {

    // Stores connected wifi details. Only accessible by the ViewModel.
    private val _connectedWifiDetails = MutableStateFlow(ConnectedWifiDetails())
    // Read-only version of _connectedWifiDetails that is used by the UI layer.
    val connectedWifiDetails: StateFlow<ConnectedWifiDetails> = _connectedWifiDetails

    // Job responsible for collecting the results from
    // the monitoring of connectivity changes.
    private var job: Job? = null

    // Starts monitoring for connectivity changes.
    // Gets called when user clicks the scan button
    // and the required permissions are allowed.
    fun startMonitoring(context: Context) {
        if (job != null)
            return
        job = viewModelScope.launch {
            connectedWifi.resultsFlow.collectLatest { details: WifiInfo? ->
                if (details != null) {
                        _connectedWifiDetails.value = ConnectedWifiDetails(
                            ssid = details.ssid.replace("\"", ""),
                            bssid = details.bssid,
                            rssi = details.rssi,
                            encryption = getEncryptionType(context),
                            frequency = bandConversionToString(details.frequency),
                            connected = true
                        )
                }
                else{
                    _connectedWifiDetails.value = ConnectedWifiDetails(connected = false)
                }
            }
        }
    }

    // Stops monitoring and frees resources.
    // Automatically called when activity is destroyed.
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}