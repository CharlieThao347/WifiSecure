/*
This file contains the code for the Vpn View Model.
Holds and updates UI logic and state. This is the presentation
layer for the vpn screen.
 */

package com.example.wifisecure.vpn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Vpn View Model, which takes a WireGuardManager object as an argument.
class VpnViewModel(
    private val wireGuardManager: WireGuardManager
) : ViewModel() {

    // Read-only version of _vpnState that is used by the UI layer.
    val vpnState: StateFlow<VpnState> = wireGuardManager.vpnState
    // State for whether or not VPN Button is enabled. Only accessible by the ViewModel.
    private val _isEnabled = MutableStateFlow(true)
    // Read-only version of _isEnabled that is used by the UI layer.
    val isEnabled: StateFlow<Boolean> = _isEnabled

    // Connect function.
    fun connect() {
        viewModelScope.launch {
            wireGuardManager.connect()
        }
    }

    // Disconnect function.
    fun disconnect() {
        viewModelScope.launch {
            wireGuardManager.disconnect()
        }
    }

    // Toggles the VPN button depending on the VPN connection state.
    fun toggleButton() {
        if(vpnState.value == VpnState.Connecting || vpnState.value == VpnState.Disconnecting)
            _isEnabled.value = false
        else if(vpnState.value == VpnState.Connected || vpnState.value == VpnState.Disconnected)
            _isEnabled.value = true
    }

    // Checks for VPN permission when VPN button is clicked
    // and user is trying to connect.
    fun onClickToConnect(checkForPermission: () -> Unit
    ) {
        checkForPermission()
    }
}