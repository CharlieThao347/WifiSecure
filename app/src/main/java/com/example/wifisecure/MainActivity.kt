@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.example.wifisecure

import android.Manifest
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.wifisecure.screensizesupport.*
import com.example.wifisecure.requiredprivileges.*
import com.example.wifisecure.ui.theme.WifiSecureTheme


/*
  Entry point for when the app starts.
  Calls the composable that renders the main page.
 */
class MainActivity : ComponentActivity() {

    // Handles user response of permission request.
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue.
                val placeholder = 1
            } else {
                // Explain that feature won't work because
                // permission denied.
                val placeholder = 2
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WifiSecureTheme {

                // Used for UI screen adaptiveness.
                val windowSizeClass = calculateWindowSizeClass(this)
                // Renders the UI.
                MainScreen(windowSizeClass)

                // Requests permission for user's location.
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED -> {
                        requestPermissionLauncher.launch(
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }

                // Check if "Location" is enabled in android settings.
                if(!isLocationEnabled(this))
                    LocationServicesDialog(this)
            }
        }
    }
}

/*
  Composable that renders the main page.
  Renders the top app bar and
  calls other composables that render everything below the app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(windowSizeClass: WindowSizeClass) {
    val sizing = rememberSizing(windowSizeClass)
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxHeight(sizing.appBarWidth),
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center, text = "WifiSecure",
                            fontSize = sizing.appBarText
                        )
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
            FoundAndFilter(sizing)
            WifiList(sizing)
            VPNButton(sizing)
        }
    }
}

// Composable that renders "Found" text and filter icon.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundAndFilter(sizing: Sizing) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically){
        // Hardcoded to 6. Will replace later.
        Text(text = "Found(6)",
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
fun WifiList(sizing: Sizing) {
            LazyColumn(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .fillMaxHeight(0.8f),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                // Hardcoded to 10. Will replace later.
                items(10) {
                    ElevatedCard(elevation = CardDefaults.cardElevation(
                        defaultElevation = 5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(sizing.cardHeight)
                            .padding(horizontal = 20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE0E0E0)
                        )) {
                        Text(text = "Wifi Name",
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                            fontSize = sizing.ssidText,
                            fontWeight = FontWeight.Bold)
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

// Renders the app in Preview Mode with compact layout.
@Preview(
    name = "Compact",
    device = "spec:width=360dp, height=800dp, orientation=portrait",
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun CompactLayoutPreview() {
    WifiSecureTheme {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 800.dp))
        MainScreen(windowSizeClass)
    }
}

// Renders the app in Preview Mode with medium layout.
@Preview(
    name = "Medium",
    device = "spec:width=800dp, height=1000dp, orientation=portrait",
    showSystemUi = true,
    showBackground = true,
)
@Composable
fun MediumLayoutPreview() {
    WifiSecureTheme {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(800.dp, 1000.dp))
        MainScreen(windowSizeClass)
    }
}

// Renders the app in Preview Mode with expanded layout.
@Preview(
    name = "Expanded",
    device = "spec:width=800dp, height=1000dp, orientation=landscape",
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun ExpandedLayoutPreview() {
    WifiSecureTheme {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1000.dp, 800.dp))
        MainScreen(windowSizeClass)
    }
}