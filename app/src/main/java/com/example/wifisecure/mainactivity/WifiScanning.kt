/*
This file contains variables related to Wi-Fi scanning
and also Wi-Fi helper functions.
 */

package com.example.wifisecure.mainactivity

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.net.wifi.ScanResult
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

// Class that holds the list of cleaned up scan results.
data class CleanedWifiList(
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val encryption: String,
    val frequency: String
)

// Contains list of WiFi after it has been cleaned up.
val wifiList = mutableStateListOf<CleanedWifiList>()

// State that changes if Wifi scanning is being performed or not.
var isScanning = mutableStateOf(false)

// Cleans up Wifi scan results and adds the results to wifiList.
fun wifiScanResults(results: List<ScanResult>) {
    wifiList.clear()
    val cleanedResults = collapseBySsid(results)
    wifiList.addAll(cleanedResults)
}

// This function was generated using ChatGPT5.
// Converts the band number (Int) to the band type (String).
fun bandConversionToString(r: Int): String = when (r) {
    in 2400..2499 -> "2.4GHz"
    in 4900..5899 -> "5GHz"
    in 5925..7125 -> "6GHz"
    else -> "${r}MHz"
}

// Simplifies the encryption string (Ex: WPA2-PSK-CCMP -> WPA2)
fun simplifyEncryptionString(encryptionString: String): String = when {
    "WPA3" in encryptionString -> "WPA3"
    "WPA2" in encryptionString -> "WPA2"
    "WPA"  in encryptionString -> "WPA"
    "WEP"  in encryptionString -> "WEP"
    else -> "Open"
}

// This function was generated using ChatGPT5.
// Comments were done by me.
// Collapses the scan results by SSID and cleans up the data.
fun collapseBySsid(wifiList: List<ScanResult>): List<CleanedWifiList> {
    return wifiList
        // Ignores <hidden> networks
        .filter { it.SSID.isNotBlank() }
        // Groups by SSID key (groups networks with the same SSID into just 1 network via
        // the one with the best signal)
        .groupBy { it.SSID.trim() }
        .map { (ssid, group) ->
            val best = group.maxBy { it.level }
            CleanedWifiList(
                ssid = ssid.trim().removeSurrounding("\""),
                bssid = best.BSSID,
                rssi = best.level,
                encryption = simplifyEncryptionString(best.capabilities),
                frequency = bandConversionToString(best.frequency)
            )
        }
        // sorts by signal (SSID's with strongest signals appears first in the list)
        .sortedByDescending { it.rssi }
}

// Check if location services are enabled
fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    return isNetworkEnabled || isGpsEnabled
}

// Redirect user to location settings
fun directToLocationServices(context: Context){
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(intent)
}