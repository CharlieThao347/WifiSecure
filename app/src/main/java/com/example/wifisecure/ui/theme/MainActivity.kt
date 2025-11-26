/*
This file contains code for the entry point of the app and navigation routes.
 */

@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
package com.example.wifisecure.ui.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wifisecure.forgotpassword.ForgotPasswordScreen
import com.example.wifisecure.login.LoginScreen
import com.example.wifisecure.main.MainScreen
import com.example.wifisecure.signup.SignUpScreen

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
            // Used for UI screen adaptiveness.
            val windowSizeClass = calculateWindowSizeClass(this)
            // Used for navigation.
            val navController = rememberNavController()
            // Defining the routes.
            NavHost(navController = navController, startDestination = Routes.loginScreen, builder = {
                composable(Routes.loginScreen){
                    LoginScreen(navController, authViewModel)
                }
                composable(Routes.signUpScreen){
                    SignUpScreen(navController, authViewModel)
                }
                composable(Routes.forgotPasswordScreen){
                    ForgotPasswordScreen()
                }
                composable(Routes.mainScreen){
                    MainScreen(navController, windowSizeClass, authViewModel, userViewModel)
                }
            })
        }
    }
}