/*
This file contains the code for the vpn screen.
UI layer for the vpn screen.
 */

package com.example.wifisecure.vpn

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import com.example.wifisecure.main.VpnSizing
import com.example.wifisecure.main.rememberSizingVpn
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.wifisecure.main.AuthState
import com.example.wifisecure.main.AuthViewModel
import com.example.wifisecure.main.Routes
import com.example.wifisecure.main.UserViewModel
import com.example.wifisecure.wifi.WifiViewModel
import com.wireguard.android.backend.GoBackend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/*
Composable that renders the vpn page.
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VpnScreen(
    navController: NavController,
    windowSizeClass: WindowSizeClass,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    wifiViewModel: WifiViewModel,
    vpnViewModel: VpnViewModel
) {
    // ViewModel variable for authentication state.
    val authState = authViewModel.authState.collectAsState()
    // Runs when the authentication state changes.
    LaunchedEffect(authState.value) {
        when(authState.value) {
            // Navigates to login screen when user logs out.
            is AuthState.Unauthenticated -> {
                userViewModel.clearUser()
                vpnViewModel.clearServers()
                navController.navigate(Routes.loginScreen)
            }
            // Do nothing.
            else -> Unit
        }
    }

    // Retrieves user's servers if authenticated.
    if(authState.value == AuthState.Authenticated)
        vpnViewModel.retrieveServers()

    // User ViewModel variables for UI.
    val userName by userViewModel.name.collectAsState()

    // Used for UI screen adaptiveness.
    val sizing = rememberSizingVpn(windowSizeClass)

    // Used to access app resources and information. Tied
    // to the activity (MainActivity).
    val activityContext = LocalContext.current

    // Vpn ViewModel variables for UI.
    val servers by vpnViewModel.servers.collectAsState()
    val defaultServerCount by vpnViewModel.defaultServerCount.collectAsState()
    val vpnState by vpnViewModel.vpnState.collectAsState()
    val isEnabled by vpnViewModel.isEnabled.collectAsState()
    val selectedServer by vpnViewModel.selectedServer.collectAsState()
    val connectedServer by vpnViewModel.connectedServer.collectAsState()

    // Handling VPN Permission Request.
    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
                vpnViewModel.connect(selectedServer)
            }
        }

    // Check for VPN permission before attempting to connect to VPN.
    val checkForPermission: () -> Unit = {
        val intent = GoBackend.VpnService.prepare(activityContext)
        if (intent != null) {
            vpnPermissionLauncher.launch(intent)
        } else {
                vpnViewModel.connect(selectedServer)
            }
        }

    // Function to check for VPN permission when VPN button is clicked.
    val onClickToConnect: () -> Unit = {
        vpnViewModel.onClickToConnect(checkForPermission)
    }

    // Function to disconnect from VPN.
    val disconnect: () -> Unit = {
        vpnViewModel.disconnect()
    }

    // Function that handles logging out the user.
    val logout: () -> Unit = {
        authViewModel.logout()
    }

    // State for the drawer.
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Thread/coroutine for opening and closing drawer.
    val scope = rememberCoroutineScope()
    // Renders the drawer.
    ModalNavigationDrawer(
        drawerContent = {
            Drawer(navController, sizing, authState.value, userName, logout)
        },
        drawerState = drawerState
    ) {
        // Renders the app bar.
        Scaffold(
            topBar = {
                TopAppBar(
                    sizing,
                    scope,
                    drawerState
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
                // Renders the "VPN Servers" count text, which shows how
                // many VPN Servers exist.
                VpnCountText(sizing, servers.size, defaultServerCount, authState.value)
                // Renders the list of VPN servers.
                VpnList(sizing,
                    windowSizeClass,
                    servers,
                    selectItem = vpnViewModel::selectItem,
                    updateSplitTunnel = vpnViewModel::updateSplitTunnel,
                    updateIsChecked = vpnViewModel::updateIsChecked,
                    addToServers = vpnViewModel::addToServers,
                    addServerToFirebase = vpnViewModel::addServerToFirebase,
                    deleteFromServers = vpnViewModel::deleteFromServers,
                    deleteServerFromFirebase = vpnViewModel::deleteServerFromFirebase,
                    authState.value
                )
                // Renders the VPN button.
                VpnButton(sizing,
                    vpnState,
                    selectedServer,
                    connectedServer,
                    isEnabled,
                    onClickToConnect,
                    disconnect
                )
            }
        }
    }
}

// Composable that renders the drawer.
@Composable
fun Drawer(
    navController: NavController,
    sizing: VpnSizing,
    authState: AuthState,
    userName: String,
    logout: () -> Unit
)
{
    ModalDrawerSheet (
        modifier = Modifier.width(sizing.drawerWidth)
    )
    {
        Column(
            modifier = Modifier
                .padding(horizontal = sizing.drawerPadding)
        ) {
            Spacer(Modifier.height(sizing.drawerSpacer))
            // Display account name if user is not a guest.
            if(authState != AuthState.Guest) {
                Text(
                    userName,
                    modifier = Modifier.padding(sizing.accountTextPadding),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = sizing.accountTextSize
                )
            }
            // Otherwise, display "Guest".
            else if(authState == AuthState.Guest) {
                Text(
                    "Guest",
                    modifier = Modifier.padding(sizing.accountTextPadding),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = sizing.accountTextSize
                )
            }
            HorizontalDivider()
            NavigationDrawerItem(
                // Scan for Wi-Fi text.
                label = {
                    Text(
                        "Scan for Wi-Fi",
                        modifier = Modifier.padding(sizing.drawerTextPadding),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = sizing.drawerTextSize
                    )
                },
                selected = false,
                // Wi-Fi Icon.
                icon = {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Wi-Fi Icon"
                    )
                },
                onClick = {
                    navController.navigate(Routes.wifiScreen)
                }
            )
            NavigationDrawerItem(
                // Connect to VPN text.
                label = {
                    Text(
                        "Connect to VPN",
                        modifier = Modifier.padding(sizing.drawerTextPadding),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = sizing.drawerTextSize
                    )
                },
                selected = true,
                // VPN Icon.
                icon = {
                    Icon(
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = "VPN Icon"
                    )
                },
                onClick = {}
            )
            // Show logout button only if user is not a guest.
            if (authState != AuthState.Guest) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(sizing.logoutButtonPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logout button.
                    Button(
                        onClick = { logout() },
                        modifier = Modifier.size(
                            sizing.logoutButtonWidth,
                            sizing.logoutButtonHeight
                        ),
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
}

// Composable that renders the app bar.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    sizing: VpnSizing,
    scope: CoroutineScope,
    drawerState: DrawerState
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
            IconButton(
                onClick = {
                    scope.launch {
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                },
                modifier = Modifier.size(sizing.menuIcon)
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu Icon",
                    modifier = Modifier.size(sizing.menuIcon)
                        .padding(start = sizing.menuPaddingStart)
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

// Composable that renders the "VPN Servers" count text.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VpnCountText(
    sizing: VpnSizing,
    count: Int,
    defaultCount: Int,
    authState: AuthState
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (authState == AuthState.Guest) {
            Text(
                text = "VPN Servers($defaultCount)",
                modifier = Modifier.padding(
                    sizing.countPaddingWidth,
                    sizing.countPaddingHeight
                ),
                fontSize = sizing.countText,
                fontWeight = FontWeight.Bold
            )
        }
        else{
            Text(
                text = "VPN Servers($count)",
                modifier = Modifier.padding(
                    sizing.countPaddingWidth,
                    sizing.countPaddingHeight
                ),
                fontSize = sizing.countText,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Composable that renders the list of VPN servers.
@Composable
fun VpnList(
    sizing: VpnSizing,
    windowSizeClass: WindowSizeClass,
    servers: List<VpnDetails>,
    selectItem: (String) -> Unit,
    updateSplitTunnel: (String, String) -> Unit,
    updateIsChecked: (String, Boolean) -> Unit,
    addToServers: (String, String, String, String, String) -> Unit,
    addServerToFirebase: (String, String, String, String, MutableMap<String, String>) -> Unit,
    deleteFromServers: (String) -> Unit,
    deleteServerFromFirebase: (String) -> Unit,
    authState: AuthState
) {
    // Structures the list in a column.
    LazyColumn(
        modifier = Modifier
            .padding(top = sizing.vpnListPaddingTop)
            .fillMaxHeight(sizing.vpnListHeight),
        verticalArrangement = Arrangement.spacedBy(sizing.vpnListSpacer),
    ) {
        // "Default Server" Divider.
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left Divider.
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                )
                // Text in the middle.
                Text(
                    text = "Default Server",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    fontSize = sizing.countText,
                )
                // Right Divider.
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                )
            }
        }
        // Default servers list.
        items(servers.take(1), key = { it.name }) { detail ->
            SelectableCard(sizing,
                windowSizeClass,
                detail = detail,
                onClick = {
                    selectItem(detail.name)
                },
                updateSplitTunnel,
                updateIsChecked,
                deleteFromServers,
                deleteServerFromFirebase
            )
        }
        // Show user's VPN servers etc. if they are logged in.
        if(authState != AuthState.Guest) {
            // "My Servers" Divider.
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Left Divider.
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                    )
                    // Text in the middle.
                    Text(
                        text = "My VPN Servers",
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        fontSize = sizing.countText,
                    )
                    // Right Divider.
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            // User's servers list.
            items(servers.drop(1), key = { it.name }) { detail ->
                SelectableCard(sizing,
                    windowSizeClass,
                    detail = detail,
                    onClick = {
                        selectItem(detail.name)
                    },
                    updateSplitTunnel,
                    updateIsChecked,
                    deleteFromServers,
                    deleteServerFromFirebase
                )
            }
            // Button to add your VPN Server.
            item {
                // Variables to keep track of state.
                val context = LocalContext.current
                var showDialog by remember { mutableStateOf(false) }
                var showFileSelector by remember { mutableStateOf(false) }
                var name by remember { mutableStateOf("No text submitted yet.") }
                var city by remember { mutableStateOf("No text submitted yet.") }
                var country by remember { mutableStateOf("No text submitted yet.") }

                // Launches activity for selecting the file.
                val fileSelector = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument(),
                    onResult = { uri: Uri? ->
                        if (uri != null) {
                            /*
                            The following code was generated using ChatGPT5. I commented the code.
                            This code reads in the file that was selected, parses it, and
                            store it in a map.
                            */
                            val inputStream = context.contentResolver.openInputStream(uri)
                            inputStream?.use { stream ->
                                // Read the data from the stream.
                                val content = stream.bufferedReader().use { it.readText() }
                                val result = mutableMapOf<String, String>()
                                // Parses each line.
                                content.lines().forEach { line ->
                                    val trimmed = line.trim()
                                    // Ignores the section headers and blank lines
                                    if (trimmed.startsWith("[") || trimmed.isBlank()) return@forEach
                                    // Split the line into two parts, delimited by "=".
                                    val parts = trimmed.split("=", limit = 2)
                                    // Check if actually got two parts.
                                    if (parts.size == 2) {
                                        // The first part becomes the key.
                                        val key = parts[0].trim()
                                        // The second part becomes the value.
                                        val value = parts[1].trim()
                                        // Stores the result in a map.
                                        result[key] = value
                                    }
                                }
                                // Code generated using ChatGPT5 ends here.
                                showFileSelector = false
                                addServerToFirebase(name, city, country, result["Endpoint"]!!.substringBefore(":"), result)
                                addToServers(name, city, country, result["Endpoint"]!!.substringBefore(":"), result["AllowedIPs"]!!)
                            }
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Button.
                    FloatingActionButton(
                        onClick = {
                            showDialog = true
                        },
                        containerColor = Color(0xFF27619b),
                        contentColor = Color.White
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Your VPN Server"
                        )
                    }
                }
                // Add VPN Server dialog pop up.
                if (showDialog) {
                    AddServerDialog(
                        onDismiss = {
                            showDialog = false
                        },
                        onConfirm = { nameInput, cityInput, countryInput ->
                            name = nameInput
                            city = cityInput
                            country = countryInput
                            showDialog = false
                            showFileSelector = true
                        }
                    )
                }
                if (showFileSelector) {
                    fileSelector.launch(arrayOf("*/*"))
                }
            }
        }
    }
}

// Composable that displays the dialog to add a server.
@Composable
fun AddServerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var cityInput by remember { mutableStateOf("") }
    var countryInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Add Your VPN Server",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Enter a name.") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cityInput,
                    onValueChange = { cityInput = it },
                    label = { Text("Enter the city.") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = countryInput,
                    onValueChange = { countryInput = it },
                    label = { Text("Enter the country.") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Button(
                        onClick = {
                            onConfirm(nameInput, cityInput, countryInput)
                        },
                        enabled = nameInput.isNotBlank() && cityInput.isNotBlank() && countryInput.isNotBlank()
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

// Composable that renders the VPN toggle button.
@Composable
fun VpnButton(sizing: VpnSizing,
              vpnState: VpnState,
              selectedServer: String,
              connectedServer: String,
              isEnabled: Boolean,
              onClickToConnect: () -> Unit,
              disconnect: () -> Unit,
)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(sizing.vpnButtonPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Shows blue connect button if not connected to VPN or
        // in the process of connecting.
        if (vpnState == VpnState.Disconnected || vpnState == VpnState.Connecting) {
            Button(
                onClick = { onClickToConnect() },
                enabled = isEnabled,
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
            if (vpnState == VpnState.Disconnected) {
                Text(
                    text = "Connect to $selectedServer",
                    fontSize = sizing.vpnText,
                    fontWeight = FontWeight.Bold

                )
            }
            else{
                Text(
                    text = "Connecting to $selectedServer...",
                    fontSize = sizing.vpnText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        // Shows red disconnect button if connected to VPN or
        // in the process of disconnecting.
        else if (vpnState == VpnState.Connected || vpnState == VpnState.Disconnecting) {
            Button(
                onClick = { disconnect() },
                enabled = isEnabled,
                shape = CircleShape,
                modifier = Modifier.size(sizing.vpnButton),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA62700),
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
            if(vpnState == VpnState.Connected) {
                Text(
                    text = "Disconnect from $connectedServer",
                    fontSize = sizing.vpnText,
                    fontWeight = FontWeight.Bold
                )
            }
            else{
                Text(
                    text = "Disconnecting from $connectedServer...",
                    fontSize = sizing.vpnText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}