/*
This file contains Wi-Fi utilities that clean up the
results from the Wi-Fi scan.
 */

package com.example.wifisecure.wifi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat

/* This function was generated using ChatGPT5.
   Converts the band number (Int) to the band type (String).
*/
fun bandConversionToString(r: Int): String = when (r) {
    in 2400..2499 -> "2.4GHz"
    in 4900..5899 -> "5GHz"
    in 5925..7125 -> "6GHz"
    else -> "${r}MHz"
}

// Retrieves the encryption type for the currently connected Wi-Fi.
fun getEncryptionType(context: Context): String? {
    if (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiDetails = wifiManager.connectionInfo ?: return null
        val currentBSSID = wifiDetails.bssid ?: return null
        val scanResults = wifiManager.scanResults
        val matchingAP = scanResults.find { it.BSSID == currentBSSID } ?: return null
        val encryptionString = matchingAP.capabilities
        return when {
            "WPA3" in encryptionString -> "WPA3"
            "WPA2" in encryptionString -> "WPA2"
            "WPA" in encryptionString -> "WPA"
            "WEP" in encryptionString -> "WEP"
            else -> "Open"
        }
    }
    return null
}

// Simplifies the encryption string (Ex: WPA2-PSK-CCMP -> WPA2).
fun simplifyEncryptionString(encryptionString: String): String = when {
    "WPA3" in encryptionString -> "WPA3"
    "WPA2" in encryptionString -> "WPA2"
    "WPA"  in encryptionString -> "WPA"
    "WEP"  in encryptionString -> "WEP"
    else -> "Open"
}

/* This function was generated using ChatGPT5.
   Comments were done by me.
   Collapses the scan results by SSID and cleans up the data.
 */
fun collapseBySsid(wifiList: List<ScanResult>): List<WifiList> {
    return wifiList
        // Ignores <hidden> networks.
        .filter { it.SSID.isNotBlank() }
        // Groups by SSID key (groups networks with the same SSID into just 1 network via
        // the one with the best signal).
        .groupBy { it.SSID.trim() }
        .map { (ssid, group) ->
            val best = group.maxBy { it.level }
            WifiList(
                ssid = ssid.trim().removeSurrounding("\""),
                bssid = best.BSSID,
                rssi = best.level,
                encryption = simplifyEncryptionString(best.capabilities),
                frequency = bandConversionToString(best.frequency)
            )
        }
        // Sorts by signal (SSID's with strongest signals appears first in the list).
        .sortedByDescending { it.rssi }
}