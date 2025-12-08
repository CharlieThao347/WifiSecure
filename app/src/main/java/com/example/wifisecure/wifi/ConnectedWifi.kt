/*
This file contains code for the ConnectedWifi Class.
This is the data layer for the connected wifi portion of the wifi screen.
 */

package com.example.wifisecure.wifi

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// Class that handles obtaining info about the currently connected wifi.
class ConnectedWifi(private val context: Context) {

    // Class object for performing wifi operations.
    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    // Class object for monitoring network status.
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Requesting info from only wifi connections.
    private val networkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()

    // Function that gets connected wifi info whenever its state changes.
    fun getConnectedWifiInfo() {
        val resultsFlow: Flow<WifiInfo?> = callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                // Update when first detecting a connected wifi.
                override fun onAvailable(network: Network) {
                    trySend(wifiManager.connectionInfo)
                }
                // Update when the capabilities (signal strength etc.) of the wifi changes.
                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    trySend(wifiManager.connectionInfo)
                }
                // Update when the connected wifi disconnected.
                override fun onLost(network: Network) {
                    trySend(null)
                }
            }
            connectivityManager.registerNetworkCallback(networkRequest, callback)
            wifiManager.connectionInfo
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }
    }
}