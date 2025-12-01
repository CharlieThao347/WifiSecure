/*
This file contains code for the WireGuardManager Class.
This is the data layer for the vpn screen.
 */

package com.example.wifisecure.vpn

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
import com.wireguard.crypto.KeyPair
import kotlinx.coroutines.tasks.await
import kotlin.collections.mutableListOf

// Class that handles connecting to the VPN.
class WireGuardManager(private val context: Context) {
    // Allows coroutines to run on the main thread.
    private val scope = CoroutineScope(Job() + Dispatchers.Main.immediate)
    // Provides the WireGuard tunnel.
    private val backend: Backend = GoBackend(context)
    // Tunnel that is currently in use.
    private var currentTunnel: Tunnel? = null

    // Keypair from which the public and private key will be generated from.
    val keyPair = KeyPair()
    // Client's public key.
    val peerPublicKey = keyPair.publicKey.toBase64()
    // Client's private key.
    val peerPrivateKey = keyPair.privateKey.toBase64()

    // Variables to keep track of the VPN connection state.
    private val _vpnState = MutableStateFlow<VpnState>(VpnState.Disconnected)
    val vpnState: StateFlow<VpnState> = _vpnState

    // Connect function.
     fun connect(selectedServer: String, allowedIPs: String) {
         // Performs network I/O outside of the main thread.
         scope.launch(Dispatchers.IO) {
         try {
             _vpnState.value = VpnState.Connecting

             val readList = mutableListOf<String>()
             // Read server info from Firebase.
             Firebase.firestore
                 .collection("defaultServers")
                 .document("cWaKfjM4QSiSogZAASAd")
                 .get()
                 .addOnSuccessListener { doc ->
                     if (doc.exists()) {
                         readFromFirebase(doc, selectedServer, readList)
                     }
                 }
                 .addOnFailureListener { e ->
                     println("Error: ${e.message}")
                 }
                 .await()
             // Sends the public key to the server and receives the internal IP
             // that is assigned to the client.
             val ip = HttpApi.registerPeer(peerPublicKey)
             // Configuration data required to connect to VPN.
             val config = """
                 [Interface]
                 PrivateKey = $peerPrivateKey
                 Address = $ip
                 DNS = ${readList[0]}
                 
                 [Peer]
                 PublicKey = ${readList[1]}
                 Endpoint = ${readList[2]}
                 AllowedIPs = $allowedIPs
                 PersistentKeepalive = ${readList[3]}
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

    // Reads server info from Firebase and saves the data in a list.
    fun readFromFirebase(doc: DocumentSnapshot, selectedServer: String, readList: MutableList<String>) {
        val interFace = doc.get("interface") as Map<String, Map<String, String>>
        val serverInterface = interFace[selectedServer]!!
        val dns = serverInterface["dns"]

        val peer = doc.get("peer") as Map<String, Map<String, String>>
        val serverPeer = peer[selectedServer]!!
        val publicKey = serverPeer["publicKey"]
        val endpoint = serverPeer["endpoint"]
        val persistentKeepAlive = serverPeer["persistentKeepAlive"]

        if (dns != null)
            readList.add(dns)
        if (publicKey != null)
            readList.add(publicKey)
        if (endpoint != null)
            readList.add(endpoint)
        if (persistentKeepAlive != null)
            readList.add(persistentKeepAlive)
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