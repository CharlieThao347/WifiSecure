/*
This file contains the code for the main screen.
UI layer for the main screen.
 */

package com.example.wifisecure.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
Root level Composable that renders the main page.
Renders the app bar and calls other
composables that render everything below the app bar.
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    windowSizeClass: WindowSizeClass
) {
    // Used for UI screen adaptiveness.
    val sizing = rememberSizing(windowSizeClass)

    // Used to access app resources and information. Tied
    // to the app's entire lifecycle.
    val appContext = LocalContext.current.applicationContext
    // Used to access app resources and information. Tied
    // to the activity (MainActivity).
    val activityContext = LocalContext.current

    // Declaration of the ViewModel
    val viewModel: MainViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val scanner = WifiScannerClass(appContext)
                return MainViewModel(scanner) as T
            }
        }
    )

    // ViewModel variables for UI.
    val wifiList by viewModel.wifiList.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val isNetworkEnabled by viewModel.isNetworkEnabled.collectAsState()
    val isGpsEnabled by viewModel.isGpsEnabled.collectAsState()

    // Updates location setting variables.
    val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    viewModel.updateLocationSetting(
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER),
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    )
    // When user returns to the app from location settings,
    // update location setting variables.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.updateLocationSetting(
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER),
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                )
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Launcher for permission request.
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    )
    // If user granted permission, proceed with the Wi-Fi scan.
    { granted ->
        if (granted) viewModel.onPermissionGranted()
    }

    // Checks for the required prerequisites before performing Wi-Fi scanning.
    val checkForPrerequisites: () -> Unit = checkForPrerequisites@{
        // Check to see if Location is enabled in Android settings.
        if (!(isNetworkEnabled || isGpsEnabled)) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(activityContext)
            builder
                .setMessage("This app requires 'Location' to be enabled for Wi-Fi scanning.")
                .setTitle("Use Location?")
                .setPositiveButton("Yes") { dialog, which ->
                    activityContext.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("No") { dialog, which ->
                }
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return@checkForPrerequisites
        }
        // Check to see if app has access to device's location.
        val granted = ContextCompat.checkSelfPermission(
            appContext, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            // If granted, proceed with the Wi-Fi scan.
            viewModel.onPermissionGranted()
        } else {
            // Otherwise, launch the permission dialog.
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Function that handles the click of a scan button.
    // Defined this way so that it can be pass into composables
    // as an argument.
    val onScanClick: () -> Unit = {
        viewModel.onScanClicked(checkForPrerequisites)
    }

    // State for the drawer.
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Thread/coroutine for opening and closing drawer.
    val scope = rememberCoroutineScope()

    // Renders the drawer.
    ModalNavigationDrawer(
        drawerContent = {
            Drawer(sizing)
        },
        drawerState = drawerState
    ) {
        // Renders the app bar.
        Scaffold(
            topBar = {
                TopAppBar(
                    sizing,
                    scope,
                    drawerState,
                    onScanClick,
                    isScanning
                )
            }
        )
        // Renders everything below the app bar.
        { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                // Renders the "Found" text, which shows how
                // many networks were found.
                FoundText(
                    sizing,
                    wifiList
                )
                // Renders the list of found Wi-Fi.
                WifiList(
                    sizing,
                    wifiList
                )
                // Renders the VPN button.
                VPNButton(sizing)
            }
        }
    }
}

// Composable that renders the drawer.
@Composable
fun Drawer(sizing: Sizing) {
    ModalDrawerSheet (
        modifier = Modifier.width(sizing.drawerWidth)
    )
    {
        Column(
            modifier = Modifier
                .padding(horizontal = sizing.drawerPadding)
        ) {
            Spacer(Modifier.height(sizing.drawerSpacer))
            // Account text.
            Text(
                "Guest",
                modifier = Modifier.padding(sizing.accountTextPadding),
                style = MaterialTheme.typography.titleLarge,
                fontSize = sizing.accountTextSize
            )
            HorizontalDivider()
            NavigationDrawerItem(
                // Connect to VPN text.
                label = {
                    Text(
                        "Connect to VPN",
                        modifier = Modifier.padding(sizing.drawerVpnTextPadding),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = sizing.drawerVpnTextSize
                    )
                },
                selected = false,
                // VPN Icon.
                icon = {
                    Icon(
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = "VPN Icon"
                    )
                },
                onClick = { /* Handle click */ }
            )
            HorizontalDivider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(sizing.logoutButtonPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logout button.
                Button(
                    onClick = {},
                    modifier = Modifier.size(sizing.logoutButtonWidth, sizing.logoutButtonHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF27619b),
                        contentColor = Color.White
                    ),
                )
                { Text("Log Out") }
            }
        }
    }
}

// Composable that renders the app bar.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    sizing: Sizing,
    scope: CoroutineScope,
    drawerState: DrawerState,
    onScanClick: () -> Unit,
    isScanning: Boolean
) {
        CenterAlignedTopAppBar(
            modifier = Modifier.fillMaxHeight(sizing.appBarWidth),
            // Renders the "WifiSecure" text that appears in the center of the app bar.
            title = {
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        textAlign = TextAlign.Center, text = "WifiSecure",
                        fontSize = sizing.appBarText
                    )
                }
            },
            // Renders the menu icon that appears in the left-side of the app bar.
            navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu Icon",
                        modifier = Modifier.size(sizing.menuIcon)
                            .padding(start = sizing.menuPaddingStart)
                    )
                }
            },
            // Renders the "Scan" buttons that appears in the right-side of the app bar.
            actions = {
                Row(
                    modifier = Modifier.fillMaxHeight().width(sizing.actionsBox),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    ScanButtons(
                        sizing,
                        onScanClick,
                        isScanning
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF27619b),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )
    }

// Composable that renders the two scan buttons.
@Composable
fun ScanButtons(
    sizing: Sizing,
    onScan: () -> Unit,
    isScanning: Boolean)
{
    // State for showing/hiding the manual scan button.
    var showManualScanButton by rememberSaveable { mutableStateOf(true) }
    // Used for launching the coroutine that performs the Wi-Fi scan every 5 seconds.
    val scope = rememberCoroutineScope()

    // Displays the automatic scan button.
    Button(
        enabled = !isScanning,
        onClick = {
            // Hides the manual scan button and toggles on automatic Wi-Fi scanning.
            if (showManualScanButton) {
                showManualScanButton = false
                scope.launch{
                    while (!showManualScanButton && !isScanning) {
                        // Invokes the Wi-Fi scanning function.
                        onScan()
                        // 5 second delay.
                        delay(5000L)
                    }
                }
            }
            // Unhides the manual scan button and toggles off automatic Wi-Fi scanning.
            else {
                showManualScanButton = true
            }
        },
        modifier = Modifier
            .padding(end = sizing.scanButtonPaddingEnd)
            .size(sizing.scanButtonWidth, sizing.scanButtonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6082B6),
            contentColor = Color.White
        )
    ) {
        Text(
            if (isScanning && !showManualScanButton) {
                "Scan…"
            } else {
                "Auto"
            },
            fontSize = sizing.scanButtonText
        )
    }

    // Displays the manual scan button if true.
    if (showManualScanButton) {
        Button(
            onClick = onScan,
            enabled = !isScanning,
            modifier = Modifier
                .padding(end = sizing.scanButtonPaddingEnd)
                .size(sizing.scanButtonWidth, sizing.scanButtonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00A300),
                contentColor = Color.White
            )
        ) {
            Text(
                if (isScanning) {
                    "Scan…"
                } else {
                    "Scan"
                },
                fontSize = sizing.scanButtonText,
            )
        }
    }
}

// Composable that renders "Found" text.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundText(
    sizing: Sizing,
    wifiList: List<WifiList>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Found(${wifiList.size})",
            modifier = Modifier.padding(sizing.foundPaddingWidth,
                sizing.foundPaddingHeight),
            fontSize = sizing.foundText,
            fontWeight = FontWeight.Bold
        )
    }
}

// Composable that renders the list of WiFi cards.
@Composable
fun WifiList(
    sizing: Sizing,
    wifiList: List<WifiList>
) {
    // Structures the list in a column.
    LazyColumn(
        modifier = Modifier
            .padding(top = sizing.wifiListPaddingTop)
            .fillMaxHeight(sizing.wifiListHeight),
        verticalArrangement = Arrangement.spacedBy(sizing.wifiListSpacer),
    ) {
        // Iterates through the cleaned scan results and displays a card for each result.
        items(wifiList, key = { it.ssid }) { result ->
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = sizing.cardElevation
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sizing.cardHeight)
                    .padding(horizontal = sizing.cardPaddingWidth),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE0E0E0)
                )
            )
            // Displays the Wi-Fi metrics (SSID, BSSID, RSSI, Encryption, and Frequency)
            // inside the card.
            {
                DisplaySSID(sizing, result)
                Row {
                    DisplayBSSID(sizing, result)
                    DisplayRSSI(sizing, result)
                }
                Row {
                    DisplayEncryption(sizing, result)
                    DisplayFrequency(sizing, result)
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
        Spacer(modifier = Modifier.height(sizing.vpnSpacerHeight))
        Text(
            text = "Connect to VPN",
            fontSize = sizing.vpnText,
            fontWeight = FontWeight.Bold
        )
    }
}