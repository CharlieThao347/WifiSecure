/*
This file contains code for the WifiScanner Class.
This is the data layer for the wifi screen.
 */

package com.example.wifisecure.wifi

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// Class that handles Wi-Fi scanning
class WifiScanner(private val context: Context)
{
    // Class object for performing Wifi operations.
    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    // Function that performs the Wifi Scan.
    fun startScan(): Boolean = wifiManager.startScan()
        // Emits whenever scan results are available.
        val resultsFlow: Flow<List<ScanResult>> = callbackFlow {
            // Registers the broadcast receiver.
            val wifiScanReceiver = object : BroadcastReceiver() {
                // When the broadcast is received (the broadcast sent when the Wifi scan has completed),
                // checks if app still has access to the device's location. If it still does, sends
                // the results to the ViewModel.
                override fun onReceive(context: Context, intent: Intent) {
                    if (ContextCompat.checkSelfPermission(
                            context, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        trySend(wifiManager.scanResults)
                    }
                }
            }
            // Tells the broadcast receiver to specifically listen to the broadcast sent
            // when a WiFi scan has completed.
            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            context.registerReceiver(wifiScanReceiver, intentFilter)
            // Automatically unregisters the broadcast receiver.
            awaitClose { context.unregisterReceiver(wifiScanReceiver) }
        }
}