/*
This file contains Wi-Fi utilities that clean up the
results from the Wi-Fi scan.
 */

package com.example.wifisecure.main

import android.net.wifi.ScanResult

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

// Class that holds the list of cleaned up scan results.
data class WifiList(
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val encryption: String,
    val frequency: String
)

// This function was generated using ChatGPT5.
// Comments were done by me.
// Collapses the scan results by SSID and cleans up the data.
fun collapseBySsid(wifiList: List<ScanResult>): List<WifiList> {
    return wifiList
        // Ignores <hidden> networks
        .filter { it.SSID.isNotBlank() }
        // Groups by SSID key (groups networks with the same SSID into just 1 network via
        // the one with the best signal)
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
        // sorts by signal (SSID's with strongest signals appears first in the list)
        .sortedByDescending { it.rssi }
}