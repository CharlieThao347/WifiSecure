/*
This file contains the code for the Wifi View Model.
Holds and updates UI logic and state. This is the presentation
layer for the wifi scan results of the wifi screen.
 */

package com.example.wifisecure.wifi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

// Class that holds the list of cleaned up scan results.
data class WifiList(
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val encryption: String,
    val frequency: String
)

// Wifi View Model, which takes a WifiScanner object as an argument.
class WifiViewModel (
    private val scanner: WifiScanner
) : ViewModel() {

    // Stores Wi-Fi scan results. Only accessible by the ViewModel.
    private val _wifiList = MutableStateFlow<List<WifiList>>(emptyList())
    // Read-only version of _wifiList that is used by the UI layer.
    val wifiList: StateFlow<List<WifiList>> = _wifiList

    // State for whether or not a Wi-Fi scan is occurring. Only accessible by the ViewModel.
    private val _isScanning = MutableStateFlow(false)
    // Read-only version of _isScanning that is used by the UI layer.
    val isScanning: StateFlow<Boolean> = _isScanning

    // Used to determine if Location is turned on in Android settings. Only accessible by the ViewModel.
    private val _isNetworkEnabled = MutableStateFlow(false)
    // Read-only version of _isNetworkEnabled that is used by the UI layer.
    val isNetworkEnabled: StateFlow<Boolean> = _isNetworkEnabled

    // Used to determine if Location is turned on in Android settings. Only accessible by the ViewModel.
    private val _isGpsEnabled = MutableStateFlow(false)
    // Read-only version of _isGpsEnabled that is used by the UI layer.
    val isGpsEnabled: StateFlow<Boolean> = _isGpsEnabled

    /* This following init block of code was generated using ChatGPT5.
       Comments were done by me.
       Runs after the ViewModel is created.
     */
    init {
        // Emits a new list each time new results come in.
        scanner.resultsFlow
            // Cleans results
            .map { collapseBySsid(it) }
            // Prevents emitting duplicates.
            .distinctUntilChanged()
            // Updates the below variables each time new results come in.
            .onEach {
                _wifiList.value = it
                _isScanning.value = false
            }
            // Runs the entire process above asynchronously.
            .launchIn(viewModelScope)
    }

    // Runs after permission to device's location is granted.
    // Starts the Wi-Fi scan.
    fun onPermissionGranted() {
        _isScanning.value = true
        val started = scanner.startScan()
        if (!started)
            _isScanning.value = false
    }

    // Updates the variables determining whether or not location
    // is enabled in Android settings.
    fun updateLocationSetting(
        isNetworkEnabled: Boolean,
        isGpsEnabled: Boolean
    ) {
        _isNetworkEnabled.value = isNetworkEnabled
        _isGpsEnabled.value = isGpsEnabled
    }

    // Runs when a scan button is clicked.
    // Checks whether or not location is enabled in Android settings
    // and if app has access to device's location.
    fun onScanClicked(checkForPrerequisites: () -> Unit
    ) {
        checkForPrerequisites()
    }

    // Removes the entry of the connected Wi-Fi from the scan results,
    // so there isn't a duplicate showing.
    fun removeConnectedWifiEntry(connectedWifiSSID: String?) {
        _wifiList.update { list ->
            list.filterNot { it.ssid == connectedWifiSSID }
        }
    }
}