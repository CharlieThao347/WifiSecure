/*
This file contains the root level code for the main page.
 */

@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
package com.example.wifisecure.mainactivity

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.net.wifi.WifiManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.content.ContextCompat

/*
  Activity for rendering and managing the main page.
 */
class MainActivity : ComponentActivity() {

    // Class object for performing Wifi operations.
    lateinit var wifiManager: WifiManager

    // Receives the broadcast sent when a WiFi scan is completed.
    var wifiScanReceiver: BroadcastReceiver? = null

    // Launcher for permission request.
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}

    // Creates the Activity.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initializing wifiManager class object.
        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        enableEdgeToEdge()
        // Loads the main page UI onto the screen.
        setContent {
            // Used for UI screen adaptiveness.
            val windowSizeClass = calculateWindowSizeClass(this)
            // Renders the UI.
            MainScreen(
                windowSizeClass,
                wifiList,
                { wifiScan() },
                isScanning.value
            )
        }
    }

    // Frees up resources when Activity is destroyed.
    override fun onDestroy() {
        super.onDestroy()
        cleanupWifiScan(this)
    }

    // Function that performs the Wifi Scan.
    // Activates when user clicks on the "Scan" button.
    fun wifiScan() {
        // Check to see if Location is enabled in Android settings.
        if (!isLocationEnabled(this)) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder
                .setMessage("This app requires 'Location' to be enabled for Wi-Fi scanning.")
                .setTitle("Use Location?")
                .setPositiveButton("Yes") { dialog, which ->
                    directToLocationServices(this)
                }
                .setNegativeButton("No") { dialog, which ->
                }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return
        }
        // Check to see if app has access to device's location.
        if(ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        // Registers the broadcast receiver.
        wifiScanReceiver = object : BroadcastReceiver() {
            // When the broadcast is received (the broadcast sent when the Wifi scan has completed),
            // checks if app still has access to the device's location. If it still does, accesses the
            // results for cleanup and then unregisters the broadcast receiver.
            override fun onReceive(context: Context?, intent: Intent?) {
                if(ContextCompat.checkSelfPermission(
                        this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED){
                    val results = wifiManager.scanResults
                    wifiScanResults(results)
                    cleanupWifiScan(this@MainActivity)
                }
                else{
                    return
                }
            }
        }
        // Tells the broadcast receiver to specifically listen to the broadcast sent
        // when a WiFi scan has completed.
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)
        // Perform the Wifi scan.
        wifiManager.startScan()
        isScanning.value = true
    }

    // Unregisters broadcast receiver and reset states.
    fun cleanupWifiScan(context: Context) {
        runCatching {context.unregisterReceiver(wifiScanReceiver)}
        isScanning.value = false
    }

}