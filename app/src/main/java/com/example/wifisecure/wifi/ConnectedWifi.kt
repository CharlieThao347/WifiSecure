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
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

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

    // Gets connected wifi info whenever its state changes.
    val resultsFlow: Flow<WifiInfo?> = callbackFlow {
        // Job responsible for polling.
        var poll: Job? = null

        // Polls to get continuous RSSI updates.
        // Set to poll every half a second.
        fun startPolling() {
            if (poll != null)
                return
            poll = launch {
                while (isActive) {
                    trySend(wifiManager.connectionInfo)
                    delay(250L)
                }
            }
        }

        // Stops polling.
        fun stopPolling() {
            poll?.cancel()
            poll = null
            trySend(null)
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            // When first detecting a connected wifi.
            override fun onAvailable(network: Network) {
                startPolling()
            }
            // When the capabilities of the wifi changes.
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                // Only poll if this network is actually a Wi-Fi network.
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    startPolling()
                } else {
                    stopPolling()
                }
            }
            // When the connected wifi disconnected.
            override fun onLost(network: Network) {
                stopPolling()
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, callback)

        // Handles the case where Wi-Fi was already connected before everything got registered.
        val activeCapabilities = connectivityManager
            .getNetworkCapabilities(connectivityManager.activeNetwork)
        if (activeCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
            startPolling()
        }

        // Free resources when Flow no longer collects.
        awaitClose {
            stopPolling()
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
}