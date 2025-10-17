@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.example.wifisecure

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Wifi1Bar
import androidx.compose.material.icons.filled.Wifi2Bar
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.wifisecure.screensizesupport.*
import com.example.wifisecure.requiredprivileges.*
import com.example.wifisecure.ui.theme.WifiSecureTheme

/*
  Entry point for when the app starts. Responsible for everything related to the main page.
 */
class MainActivity : ComponentActivity() {

    // Class object for performing Wifi operations.
    lateinit var wifiManager: WifiManager
    // Contains list of WiFi after it has been cleaned up.
    val wifiList = mutableStateListOf<CleanedWifiList>()
    // Receives the broadcast sent when a WiFi scan is completed.
    var wifiScanReceiver: BroadcastReceiver? = null
    // State that changes if Wifi scanning is being performed or not.
    var isScanning = mutableStateOf(false)


    // Launcher for permission request.
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}

    // Launcher for location settings.
    val locationSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {}

    // Creates the Activity that renders the main page.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initializing wifiManager class object.
        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        enableEdgeToEdge()
        // Loads the main page UI onto the screen.
        setContent {
            WifiSecureTheme {
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
    }

    // Frees up resources when Activity is destroyed.
    override fun onDestroy() {
        super.onDestroy()
        cleanupWifiScan()
    }

    // Function that performs the Wifi Scan.
    // Activates when user clicks on the "Scan" button.
    private fun wifiScan() {
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
                    cleanupWifiScan()
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

    // Cleans up Wifi scan results and adds the results to wifiList.
    private fun wifiScanResults(results: List<ScanResult>) {
        wifiList.clear()
        val cleanedResults = collapseBySsid(results)
        wifiList.addAll(cleanedResults)
    }

    // Unregisters broadcast receiver and reset states.
    private fun cleanupWifiScan() {
        unregisterReceiver(wifiScanReceiver)
        isScanning.value = false
    }
}

// Class that holds the list of cleaned up scan results.
data class CleanedWifiList(
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val encryption: String,
    val frequency: String
)

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

/*
  Composable that renders the main page.
  Renders the top app bar and
  calls other composables that render everything below the app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass,
    wifiList: List<CleanedWifiList>,
    onScan: () -> Unit,
    isScanning: Boolean)
{
    val sizing = rememberSizing(windowSizeClass)
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.fillMaxHeight(sizing.appBarWidth),
                title = {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            textAlign = TextAlign.Center, text = "WifiSecure",
                            fontSize = sizing.appBarText
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ){
                        Button(
                            onClick = onScan,
                            enabled = !isScanning,
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(150.dp, 60.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00A300),
                                contentColor = Color.White
                            )
                        ) {
                            Text(if (isScanning) {
                                "Scanning…"
                            }
                            else {
                                "Scan"
                            },
                                fontSize = 20.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF27619b),
                    titleContentColor = Color.White
                )
            )
        }
    )
    { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ){
            FoundAndFilter(
                sizing,
                wifiList)
            WifiList(
                sizing,
                wifiList
            )
            VPNButton(sizing)
        }
    }
}

// Composable that renders "Found" text and filter icon.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundAndFilter(
    sizing: Sizing,
    wifiList: List<CleanedWifiList>) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically){
        Text(text = "Found(${wifiList.size})",
            modifier = Modifier.padding(horizontal = 8.dp),
            fontSize = sizing.foundText,
            fontWeight = FontWeight.Bold)
        // Will implement button logic later.
        IconButton(
            onClick = {}
        ) {
            Icon(
                modifier = Modifier.size(sizing.filterIcon),
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter icon"
            )
        }
    }
}

// Composable that renders the list of WiFi cards.
@Composable
fun WifiList(
    sizing: Sizing,
    wifiList: List<CleanedWifiList>)
{
    // States for each dialog pop up
    var showSSIDDialog by remember { mutableStateOf(false) }
    var showBSSIDDialog by remember { mutableStateOf(false) }
    var showRSSIDialog by remember { mutableStateOf(false) }
    var showEncryptionDialog by remember { mutableStateOf(false) }
    var showFrequencyBandDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .padding(top = 2.dp)
            .fillMaxHeight(0.8f),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Iterates through the cleaned scan results and displays a card for each result.
        items(wifiList, key = { it.ssid }) { x ->
            ElevatedCard(elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sizing.cardHeight)
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE0E0E0)
                )) {
                // Displays info about the access point within the card.
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color(0xFF0000FF), textDecoration = TextDecoration.Underline)) {
                            append("SSID")
                        }
                        append(": ${x.ssid}")
                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .clickable{ showSSIDDialog = true },
                    fontSize = sizing.ssidText,
                    fontWeight = FontWeight.Bold
                )
                if (showSSIDDialog) {
                    AlertDialog(
                        title = {
                            Text(text = "What is the SSID?")
                        },
                        text = {
                            Text(text = "The SSID (Service Set Identifier) is the name of the Wi-Fi network.",
                                fontSize = 18.sp,)
                        },
                        onDismissRequest = {
                            showSSIDDialog = false
                        },
                        confirmButton = {},
                        dismissButton = {}
                    )
                }
                Row {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color(0xFF0000FF), textDecoration = TextDecoration.Underline)) {
                                append("BSSID")
                            }
                            append(": ${x.bssid}")
                        },
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 20.dp)
                            .clickable{ showBSSIDDialog = true },
                        fontSize = sizing.subfieldText,
                    )
                    if (showBSSIDDialog) {
                        AlertDialog(
                            title = {
                                Text(text = "What is the BSSID?")
                            },
                            text = {
                                Text(text = "The BSSID (Basic Service Set Identifier), also known as the MAC address, is a unique identifier for a specific Wi-Fi access point. This identifier ensures devices connect to the correct access point, especially when multiple access points share the same SSID.",
                                    fontSize = 18.sp,)
                            },
                            onDismissRequest = {
                                showBSSIDDialog = false
                            },
                            confirmButton = {},
                            dismissButton = {}
                        )
                    }
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color(0xFF0000FF), textDecoration = TextDecoration.Underline)) {
                                append("RSSI")
                            }
                            append(": ${x.rssi} dBm")
                        },
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 40.dp)
                            .clickable{ showRSSIDialog = true },
                        fontSize = sizing.subfieldText,
                    )
                    if (showRSSIDialog) {
                        AlertDialog(
                            title = {
                                Text(text = "What is the RSSI?")
                            },
                            text = {
                                Text(text = "The RSSI (Received Signal Strength Indicator) is a measurement of how well your device can hear a signal from an access point. It is measured in a negative number of dBm, with values closer to 0 being stronger and better, and values closer to -100 being weaker. A higher RSSI value indicates a stronger, more reliable connection, while a lower value indicates a weaker connection.",
                                    fontSize = 18.sp,)
                            },
                            onDismissRequest = {
                                showRSSIDialog = false
                            },
                            confirmButton = {},
                            dismissButton = {}
                        )
                    }
                    val icon = when (x.rssi)
                    {   in -999..-86 -> Icons.Filled.WifiOff
                        in -85..-71 -> Icons.Filled.Wifi1Bar
                        in -70..-60 -> Icons.Filled.Wifi2Bar
                        else -> Icons.Filled.Wifi
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Wi-Fi Signal Strength Icon"
                    )
                }
                Row {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color(0xFF0000FF), textDecoration = TextDecoration.Underline)) {
                                append("Encryption")
                            }
                            append(": ${x.encryption}")
                        },
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 20.dp)
                            .clickable{ showEncryptionDialog = true },
                        fontSize = sizing.subfieldText,
                    )
                    if (showEncryptionDialog) {
                        AlertDialog(
                            title = {
                                Text(text = "Main types of Wi-Fi encryption")
                            },
                            text = {
                                Text(text = "Open:\n" + "This indicates that no encryption is used. Connection to open networks do not require a password. Stay safe on these networks by using a VPN. If visiting a website, ensure the website is using HTTPS.\n\n\n" + "WEP (Wired Equivalent Privacy):\n" +
                                        "This is the oldest and weakest protocol. It only uses basic encryption and is highly vulnerable to attacks.\n\n\n" +
                                        "WPA (Wi-Fi Protected Access):\n" + "An improvement over WEP that uses stronger encryption. However, it is still considered insecure by modern standards.\n\n\n" + "WPA2 (Wi-Fi Protected Access 2):\n" + "The most common Wi-Fi encryption type that uses the robust Advanced Encryption Standard (AES). It is considered secure.\n\n\n"
                                    + "WPA3 (Wi-Fi Protected Access 3):\n" + "The latest and most secure standard, offering stronger encryption and better protection than the previous protocols.",
                                    fontSize = 18.sp,)
                            },
                            onDismissRequest = {
                                showEncryptionDialog = false
                            },
                            confirmButton = {},
                            dismissButton = {}
                        )
                    }
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color(0xFF0000FF), textDecoration = TextDecoration.Underline)) {
                                append("Frequency Band")
                            }
                            append(": ${x.frequency}")
                        },
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 108.dp)
                            .clickable{ showFrequencyBandDialog = true },
                        fontSize = sizing.subfieldText,
                    )
                    if (showFrequencyBandDialog) {
                        AlertDialog(
                            title = {
                                Text(text = "What is the Frequency Band?")
                            },
                            text = {
                                Text(text = "The Frequency Band refers to the range of radio waves that the wireless network uses to transmit data between devices. There are two main frequency bands, 2.4GHz and 5Ghz.\n\n" + "Compared to 5Ghz, 2.4GHz has longer range and is better at penetrating solid objects like walls. However, it is generally slower than 5GHz. Compared to 2.4GHz, 5GHz has less range, but it is generally faster.",
                                    fontSize = 18.sp,)
                            },
                            onDismissRequest = {
                                showFrequencyBandDialog = false
                            },
                            confirmButton = {},
                            dismissButton = {}
                        )
                    }
                }
            }
        }
    }
}

// Composable that renders the VPN toggle button.
@Composable
fun VPNButton(sizing: Sizing) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(sizing.vpnButtonPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {},
            shape = CircleShape,
            modifier = Modifier.size(sizing.vpnButton),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF27619b),
                contentColor = Color.White
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.PowerSettingsNew,
                contentDescription = "Power Button Icon",
                modifier = Modifier.size(sizing.powerIcon)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = "Connect to VPN",
            fontSize = sizing.vpnText,
            fontWeight = FontWeight.Bold)
    }
}