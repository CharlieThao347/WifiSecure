/*
This file contains code for the WireGuardManager Class.
This is the data layer for the vpn screen.
 */

package com.example.wifisecure.vpn

import android.content.Context
import android.util.Log
import com.wireguard.android.backend.Backend
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

// Class that handles connecting to the VPN.
class WireGuardManager(private val context: Context) {
    // Allows coroutines to run on the main thread.
    private val scope = CoroutineScope(Job() + Dispatchers.Main.immediate)
    // Provides the WireGuard tunnel.
    private val backend: Backend = GoBackend(context)
    // Tunnel that is currently in use.
    private var currentTunnel: Tunnel? = null

    // Variables to keep track of the VPN connection state.
    private val _vpnState = MutableStateFlow<VpnState>(VpnState.Disconnected)
    val vpnState: StateFlow<VpnState> = _vpnState

    // Connect function.
     fun connect() {
         // Performs network I/O outside of the main thread.
         scope.launch(Dispatchers.IO) {
         try {
             _vpnState.value = VpnState.Connecting
             // Configuration data required to connect to VPN.
             // I deleted the sensitive data, so I can safely push to Github.
             val config = """
            [Interface]
            PrivateKey = 
            Address = 
            DNS = 
            
            [Peer]
            PublicKey = 
            Endpoint = 
            AllowedIPs = 
            PersistentKeepalive = 
        """.trimIndent()
             // Parsing the data.
             val inputStream = ByteArrayInputStream(config.toByteArray())
             val wireguardConfig = Config.parse(inputStream)
             // Creates tunnel.
             val tunnel: Tunnel = WireGuardTunnel()
             // Start tunnel using the configuration data.
             // If success, then connection to VPN has been established.
             backend.setState(tunnel, Tunnel.State.UP, wireguardConfig)
             currentTunnel = tunnel
             _vpnState.value = VpnState.Connected
         } catch (e: Exception) {
             Log.e("VPN", "Connect failed", e)
             _vpnState.value = VpnState.Disconnected
         }
     }
     }

    // Disconnect function.
    fun disconnect() {
        // Performs network I/O outside of the main thread.
        scope.launch(Dispatchers.IO) {
            try {
                _vpnState.value = VpnState.Disconnecting
                // Close tunnel. If successful, then no longer connected to VPN.
                currentTunnel?.let { tunnel ->
                    backend.setState(tunnel, Tunnel.State.DOWN, null)
                }
                currentTunnel = null
                _vpnState.value = VpnState.Disconnected
            } catch (e: Exception) {
                Log.e("VPN", "Disconnect failed", e)
                _vpnState.value = VpnState.Disconnected
            }
        }
    }
}