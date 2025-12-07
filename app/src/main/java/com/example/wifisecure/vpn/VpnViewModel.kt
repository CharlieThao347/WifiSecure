/*
This file contains the code for the Vpn View Model.
Holds and updates UI logic and state. This is the presentation
layer for the vpn screen.
 */

package com.example.wifisecure.vpn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Vpn View Model, which takes a WireGuardManager object as an argument.
class VpnViewModel(
    private val wireGuardManager: WireGuardManager
) : ViewModel() {

    // List containing information about the servers.
    private val _servers = MutableStateFlow(listOf(
        // Default server
        VpnDetails(
            "Default",
            "157.230.133.120",
            false,
            "0.0.0.0/0, ::/0",
            "Santa Clara",
            "United States",
            isSelected = false,
            isChecked = false,
            isDefault = true,
            isDeleteEnabled = true
        )

    ))
    // Read-only version of _servers that is used by the UI layer.
    val servers: StateFlow<List<VpnDetails>> = _servers

    // Number of default servers.
    private val _defaultServerCount = MutableStateFlow(1)
    // Read-only version of _defaultServerCount that is used by the UI layer.
    val defaultServerCount: StateFlow<Int> = _defaultServerCount

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

    /*
    REMINDER TO SELF:
    Everything related to fetching the user's servers and
    clearing it after the user logs out should actually go
    in the User View Model, not this View Model.
    Do this if later refactoring the code.
     */
    // State for whether or not user's servers have been fetched.
    private val _retrieved = MutableStateFlow(false)
    // Read-only version of _retrieved.
    val retrieved: StateFlow<Boolean> = _retrieved

    // Function that fetches user's server info from Firebase.
    fun retrieveServers() {
        // If user's servers has already been retrieved, then return.
        if(_retrieved.value) {
            return
        }
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val doc = Firebase.firestore
                    .collection("users")
                    .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                    .get()
                    .await()
                if (doc.exists()) {
                    val servers = doc.get("servers") as? Map<String, List<String>> ?: emptyMap()
                    for ((serverName, details) in servers) {
                        val readList = mutableListOf<String>()
                        for (value in details) {
                            readList.add(value)
                        }
                        _servers.update { list ->
                            list + VpnDetails(
                                readList[0],
                                readList[3],
                                false,
                                readList[9],
                                readList[1],
                                readList[2],
                                isSelected = false,
                                isChecked = false,
                                isDefault = false,
                                isDeleteEnabled = true
                            )
                        }
                    }
                }
            }
        }
        _retrieved.value = true
    }

    // Delete user's servers from server list when logging out.
    fun clearServers() {
        _servers.update { list ->
            list.take(1)
        }
        _retrieved.value = false
    }

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

    // Updates the state of the delete button.
    fun updateIsDeleteEnabled(selectedItemId: String, state: Boolean) {
        _servers.update { list ->
            list.map { item ->
                if (item.name == selectedItemId) {
                    item.copy(isDeleteEnabled = state)
                } else {
                    item
                }
            }
        }
    }

    // Add user's server to Firebase.
    fun addServerToFirebase(serverName:String, city:String, country:String, ip:String, result: MutableMap<String, String>) {
        val privateKey = result["PrivateKey"]!!
        val address = result["Address"]!!
        val dns = result["DNS"]!!
        val publicKey = result["PublicKey"]!!
        val endpoint = result["Endpoint"]!!
        val allowedIPs = result["AllowedIPs"]!!
        val persistentKeepAlive = result["PersistentKeepalive"]!!
        val serverDetails = listOf(serverName, city, country, ip, privateKey, address, dns, publicKey, endpoint, allowedIPs, persistentKeepAlive)
        val servers = mapOf(
            serverName to serverDetails,
        )
        val data = mapOf(
            "servers" to servers
        )
        viewModelScope.launch {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                .set(data, SetOptions.merge())
                .await()
        }
    }

    // Add user's server to server list.
    fun addToServers(name:String, city:String, country:String, ip:String, allowedIPs:String) {
        _servers.update { list ->
            list + VpnDetails(
                name, ip, false, allowedIPs, city, country, isSelected = false, isChecked = false, isDefault = false, isDeleteEnabled = true)
        }
    }

    // Delete user's server from Firebase.
    fun deleteServerFromFirebase(serverName:String) {
        viewModelScope.launch {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                .update(mapOf("servers.$serverName" to FieldValue.delete()))
                .await()
        }
    }

    // Delete user's server from server list.
    fun deleteFromServers(name:String) {
        _servers.update { list ->
            list.filter {
                it.name != name
            }
        }
    }

    // Connect to VPN function.
    fun connect(selectedServer: String) {
        val selected = _servers.value.find { it.name == selectedServer }
        if(selected != null) {
            viewModelScope.launch {
                wireGuardManager.connect(selectedServer, selected.allowedIPs, selected.isDefault)
            }
        }
        _connectedServer.value = selectedServer
        updateIsDeleteEnabled(_connectedServer.value, false)
    }

    // Disconnect from VPN function.
    fun disconnect() {
        viewModelScope.launch {
            wireGuardManager.disconnect()
        }
        updateIsDeleteEnabled(_connectedServer.value, true)
        _connectedServer.value = ""
    }

    // Checks for VPN permission when VPN button is clicked
    // and user is trying to connect.
    fun onClickToConnect(checkForPermission: () -> Unit
    ) {
        checkForPermission()
    }
}