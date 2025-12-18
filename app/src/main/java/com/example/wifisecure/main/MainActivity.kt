/*
This file contains code for the entry point of the app and navigation routes.
 */

@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
package com.example.wifisecure.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.wifisecure.forgotpassword.ForgotPasswordScreen
import com.example.wifisecure.login.LoginScreen
import com.example.wifisecure.signup.SignUpScreen
import com.example.wifisecure.vpn.VpnScreen
import com.example.wifisecure.vpn.VpnViewModel
import com.example.wifisecure.vpn.WireGuardManager
import com.example.wifisecure.wifi.ConnectedWifi
import com.example.wifisecure.wifi.ConnectedWifiViewModel
import com.example.wifisecure.wifi.WifiScanner
import com.example.wifisecure.wifi.WifiScreen
import com.example.wifisecure.wifi.WifiViewModel

/*
Entry point of the app.
 */
class MainActivity : ComponentActivity() {
    // Creates the Activity.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel : AuthViewModel by viewModels()
        val userViewModel : UserViewModel by viewModels()
        setContent {
            // Used to access app resources and information. Tied
            // to the app's entire lifecycle.
            val appContext = LocalContext.current.applicationContext
            // Declaration of the Vpn ViewModel.
            val vpnViewModel: VpnViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        val wgManager = WireGuardManager(appContext)
                        return VpnViewModel(wgManager) as T
                    }
                }
            )
            // Used for UI screen adaptiveness.
            val windowSizeClass = calculateWindowSizeClass(this)
            // Used for navigation.
            val navController = rememberNavController()
            // Defining the routes.
            NavHost(
                navController = navController,
                startDestination = Routes.loginScreen,
                builder = {
                    composable(Routes.loginScreen) {
                        LoginScreen(navController, windowSizeClass, authViewModel)
                    }
                    composable(Routes.signUpScreen) {
                        SignUpScreen(navController, windowSizeClass, authViewModel)
                    }
                    composable(Routes.forgotPasswordScreen) {
                        ForgotPasswordScreen(windowSizeClass)
                    }

                    /*
                    Groups the wifi screen and vpn screen in such way that the
                    Wifi View Model and Connected Wifi View Model will only persist when the user is on either
                    of these two screens. This ensures that the Wi-Fi scan results and connected Wi-Fi details do
                    not persist when exiting back to the login page etc. I used ChatGPT5
                    to generate the template of doing this (block of code below) and then I filled it in with the
                    appropriate data.
                    */
                    navigation(
                        startDestination = Routes.wifiScreen,
                        route = "app_flow"
                    ) {
                        composable(Routes.wifiScreen) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("app_flow")
                            }
                            // Declaration of the Wifi ViewModel.
                            val wifiViewModel: WifiViewModel = viewModel(
                                viewModelStoreOwner = parentEntry,
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        val scanner = WifiScanner(appContext)
                                        return WifiViewModel(scanner) as T
                                    }
                                }
                            )
                            // Declaration of the Connected Wifi ViewModel.
                            val connectedWifiViewModel: ConnectedWifiViewModel = viewModel(
                                viewModelStoreOwner = parentEntry,
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        val connectedWifi = ConnectedWifi(appContext)
                                        return ConnectedWifiViewModel(connectedWifi) as T
                                    }
                                }
                            )
                            WifiScreen(navController,
                                windowSizeClass,
                                authViewModel,
                                userViewModel,
                                wifiViewModel,
                                connectedWifiViewModel,
                                vpnViewModel
                            )
                        }
                        composable(Routes.vpnScreen) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry("app_flow")
                            }
                            // Declaration of the Wifi ViewModel.
                            val wifiViewModel: WifiViewModel = viewModel(
                                viewModelStoreOwner = parentEntry,
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        val scanner = WifiScanner(appContext)
                                        return WifiViewModel(scanner) as T
                                    }
                                }
                            )
                            // Declaration of the Connected Wifi ViewModel.
                            val connectedWifiViewModel: ConnectedWifiViewModel = viewModel(
                                viewModelStoreOwner = parentEntry,
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        val connectedWifi = ConnectedWifi(appContext)
                                        return ConnectedWifiViewModel(connectedWifi) as T
                                    }
                                }
                            )
                            VpnScreen(navController,
                                windowSizeClass,
                                authViewModel,
                                userViewModel,
                                wifiViewModel,
                                connectedWifiViewModel,
                                vpnViewModel
                            )
                        }
                    }
                }
            )
        }
    }
}