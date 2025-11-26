/*
This file contains the code for the vpn screen.
UI layer for the vpn screen.
 */

package com.example.wifisecure.vpn

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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.wifisecure.main.AuthState
import com.example.wifisecure.main.AuthViewModel
import com.example.wifisecure.main.Routes
import com.example.wifisecure.main.UserViewModel
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
    userViewModel: UserViewModel
) {
    // ViewModel variable for authentication state.
    val authState = authViewModel.authState.collectAsState()
    // Runs when the authentication state changes.
    LaunchedEffect(authState.value) {
        when(authState.value) {
            // Navigates to login screen when user logs out.
            is AuthState.Unauthenticated -> {
                userViewModel.clearUser()
                navController.navigate(Routes.loginScreen)
            }
            // Do nothing.
            else -> Unit
        }
    }

    // User ViewModel variables for UI.
    val userName by userViewModel.name.collectAsState()

    // Used for UI screen adaptiveness.
    val sizing = rememberSizingVpn(windowSizeClass)

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
                VpnCountText(sizing)
                // Renders the list of VPN servers.
                VpnList(sizing, authState.value)
                // Renders the VPN button.
                VpnButton(sizing)
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
    sizing: VpnSizing
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "VPN Servers(0)",
            modifier = Modifier.padding(sizing.countPaddingWidth,
                sizing.countPaddingHeight),
            fontSize = sizing.countText,
            fontWeight = FontWeight.Bold
        )
    }
}

// Composable that renders the list of VPN servers.
@Composable
fun VpnList(
    sizing: VpnSizing,
    authState: AuthState
) {
    // Structures the list in a column.
    LazyColumn(
        modifier = Modifier
            .padding(top = sizing.vpnListPaddingTop)
            .fillMaxHeight(sizing.vpnListHeight),
        verticalArrangement = Arrangement.spacedBy(sizing.vpnListSpacer),
    ) {
        // "Default Servers" Divider.
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
                    text = "Default Servers",
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
        // Hardcoded default servers list.
        items(3) { result ->
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
            // Displays the placeholder text inside the card.
            {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF0000FF),
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("VPN Server")
                        }
                    },
                    modifier = Modifier
                        .padding(sizing.ssidPaddingHorizontal, sizing.ssidPaddingVertical),
                    fontSize = sizing.ssidText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        // Show user's VPN servers etc. if they are logged in.
        if(authState != AuthState.Guest) {
            // "Your Servers" Divider.
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Left Divider
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                    )
                    // Text in the middle.
                    Text(
                        text = "Your VPN Servers",
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
            // Hardcoded user's servers list.
            items(1) { result ->
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
                // Displays the placeholder text inside the card.
                {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFF0000FF),
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("VPN Server")
                            }
                        },
                        modifier = Modifier
                            .padding(sizing.ssidPaddingHorizontal, sizing.ssidPaddingVertical),
                        fontSize = sizing.ssidText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            // Button to add your VPN Server.
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    FloatingActionButton(
                        onClick = {},
                        containerColor = Color(0xFF27619b),
                        contentColor = Color.White
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Your VPN Server"
                        )
                    }
                }
            }
        }
    }
}

// Composable that renders the VPN toggle button.
@Composable
fun VpnButton(sizing: VpnSizing) {
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