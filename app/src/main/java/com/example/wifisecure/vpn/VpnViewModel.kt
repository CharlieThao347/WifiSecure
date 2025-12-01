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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Vpn View Model, which takes a WireGuardManager object as an argument.
class VpnViewModel(
    private val wireGuardManager: WireGuardManager
) : ViewModel() {

    // List containing information about the default servers.
    private val _servers = MutableStateFlow(listOf(
        VpnDetails(
            "Default", "157.230.133.120", false, "0.0.0.0/0, ::/0", "Santa Clara", "United States", isSelected = false, isChecked = false)

    ))
    // Read-only version of _servers that is used by the UI layer.
    val servers: StateFlow<List<VpnDetails>> = _servers

    // Read-only version of _vpnState that is used by the UI layer.
    val vpnState: StateFlow<VpnState> = wireGuardManager.vpnState

    // Stores the currently selected server.
    private val _selectedServer = MutableStateFlow("a VPN server")
    // Read-only version of _selectedServer that is used by the UI layer.
    val selectedServer: StateFlow<String> = _selectedServer

    // Stores the currently connected server.
    private val _connectedServer = MutableStateFlow("")
    // Read-only version of _connectedServer that is used by the UI layer.
    val connectedServer: StateFlow<String> = _connectedServer

    // State for whether or not VPN Button is enabled.
    private val _isEnabled = MutableStateFlow(false)
    // Read-only version of _isEnabled that is used by the UI layer.
    val isEnabled: StateFlow<Boolean> = _isEnabled

    // Keeps track and updates which VPN server is selected.
    fun selectItem(selectedItemId: String) {
        _servers.update { list ->
            list.map { item ->
                if (item.name == selectedItemId) {
                    item.copy(isSelected = true)
                } else {
                    item.copy(isSelected = false)
                }
            }
        }
        val selectedServer = _servers.value.find { it.name == selectedItemId }
        if (selectedServer != null) {
            _selectedServer.value = selectedServer.name
        }
        _isEnabled.value = true
    }

    // Updates split tunnel information of the currently selected server.
    fun updateSplitTunnel(selectedItemId: String, submittedText: String) {
        _servers.update { list ->
            list.map { item ->
                if (item.name == selectedItemId && !item.splitTunnelStatus) {
                    item.copy(allowedIPs = submittedText, splitTunnelStatus = true)
                }
                else if(item.name == selectedItemId) {
                    item.copy(allowedIPs = submittedText, splitTunnelStatus = false)
                }
                else {
                    item
                }
            }
        }
    }

    // Updates the state of the split tunneling switch button.
    fun updateIsChecked(selectedItemId: String, state: Boolean) {
        _servers.update { list ->
            list.map { item ->
                if (item.name == selectedItemId) {
                    item.copy(isChecked = state)
                } else {
                    item
                }
            }
        }
    }

    // Connect to VPN function.
    fun connect(selectedServer: String) {
        val selected = _servers.value.find { it.name == selectedServer }
        if(selected != null) {
            viewModelScope.launch {
                wireGuardManager.connect(selectedServer, selected.allowedIPs)
            }
        }
        _connectedServer.value = selectedServer
    }

    // Disconnect from VPN function.
    fun disconnect() {
        viewModelScope.launch {
            wireGuardManager.disconnect()
        }
    }

    // Checks for VPN permission when VPN button is clicked
    // and user is trying to connect.
    fun onClickToConnect(checkForPermission: () -> Unit
    ) {
        checkForPermission()
    }
}