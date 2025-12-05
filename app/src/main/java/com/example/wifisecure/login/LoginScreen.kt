/*
This file contains the code for the login page.
 */

package com.example.wifisecure.login

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.wifisecure.R
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.wifisecure.main.AuthState
import com.example.wifisecure.main.AuthViewModel
import com.example.wifisecure.main.Routes
import com.example.wifisecure.main.rememberSizingLanding
import com.example.wifisecure.main.rememberSizingWifi

// Composable that renders the login page.
@Composable
fun LoginScreen(navController: NavController, windowSizeClass: WindowSizeClass, authViewModel: AuthViewModel)
{
    // Used to access app resources and information. Tied
    // to the activity (MainActivity).
    val activityContext = LocalContext.current
    // ViewModel variable for authentication state.
    val authState = authViewModel.authState.collectAsState()
    // Runs when the authentication state changes.
    LaunchedEffect(authState.value) {
        when(authState.value) {
            // Navigate to main screen if authenticated.
            is AuthState.Authenticated ->
                navController.navigate (Routes.wifiScreen)
            // Display error message if error occurred..
            is AuthState.Error -> {
                Toast.makeText(
                    activityContext,
                    (authState.value as AuthState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            // Otherwise, do nothing.
            else -> Unit
        }
    }

    // Used for UI screen adaptiveness.
    val sizing = rememberSizingLanding(windowSizeClass)

    // State for email.
    var email by remember { mutableStateOf("") }
    // State for password.
    var password by remember { mutableStateOf("") }
    // State for toggling visibility of password.
    var showPassword by remember { mutableStateOf(false) }
    // State for email error message.
    var emailError by remember { mutableStateOf("") }
    // State for password error message.
    var passwordError by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        /*
    The following code was taken from GeeksforGeeks.
    Link: https://www.geeksforgeeks.org/kotlin/lottie-animation-in-android-jetpack-compose/
    The code was commented by me.
    The animation (landing_page_animation.JSON) was created by Hamza Khalid.
    Link: https://lottiefiles.com/thevisualdesigner
    */
        // State to keep track if animation is playing.
        var isPlaying by remember { mutableStateOf(true) }
        // State to control the speed of the animation.
        var speed by remember { mutableFloatStateOf(.8f) }
        // State to hold the lottie composition, which accepts the lottie composition result.
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.landing_page_animation))
        // Controls the animation.
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            isPlaying = isPlaying,
            speed = speed
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pass the composition and the progress state.
            LottieAnimation(
                composition,
                progress,
                modifier = Modifier
                    .size(sizing.lottieSize)
                    .align(Alignment.CenterHorizontally),
            )
            /*
        Code taken from GeeksforGeeks ends here.
        */

            // "Login" text.
            Text(text = "Login", fontSize = sizing.titleSize, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(sizing.spacerHeight))

            // Email text field.
            TextField(
                value = email,
                onValueChange = { email = it },
                // Placeholder text in the field is "Email" with default color.
                // If user tries to login with empty field, display email error text in red.
                label = {
                    Text(
                        emailError.ifEmpty { "Email" },
                        color = if (emailError.isNotEmpty()) Red else Unspecified
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Account Icon"
                    )
                },
                shape = RoundedCornerShape(7.dp),
                modifier = Modifier.fillMaxWidth()
                    .padding(20.dp, 5.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Transparent,
                    unfocusedIndicatorColor = Transparent
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password text field.
            TextField(
                value = password,
                onValueChange = { password = it },
                // Placeholder text in the field is "Password" with default color.
                // If user tries to login with empty field, display password error text in red.
                label = {
                    Text(
                        passwordError.ifEmpty { "Password" },
                        color = if (passwordError.isNotEmpty()) Red else Unspecified
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Password Icon"
                    )
                },
                trailingIcon = {
                    Icon(
                        modifier = Modifier.clickable { showPassword = !showPassword },
                        imageVector = if (showPassword)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff,
                        contentDescription = "Visibility Icon"
                    )
                },
                // If showPassword is true, then show the password. Otherwise, obscure the password.
                visualTransformation = if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
                shape = RoundedCornerShape(7.dp),
                modifier = Modifier.fillMaxWidth()
                    .padding(20.dp, 5.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Transparent,
                    unfocusedIndicatorColor = Transparent
                )
            )

            Spacer(modifier = Modifier.height(25.dp))

            // Login button.
            Button(
                onClick = {
                    // Set error messages if user tries to click "Login" button with an empty text field.
                    emailError = if (email.isBlank()) "Email is required" else ""
                    passwordError = if (password.isBlank()) "Password is required" else ""
                    // Login.
                    if (emailError.isEmpty() && passwordError.isEmpty()) {
                        authViewModel.login(email, password)
                    }
                },
                enabled = authState.value != AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 50.dp)
                    .height(sizing.buttonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF27619b),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Login")
            }

            Spacer(modifier = Modifier.height(15.dp))

            // "Forgot Password?" text.
            Text(
                text = "Forgot Password?",
                color = Color(0xFF27619b),
                modifier = Modifier.clickable {
                    navController.navigate(Routes.forgotPasswordScreen)
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            // "Don't have an account? and "Sign up now!" text.
            Row {
                Text(text = "Don't have an account? ")
                Text(
                    text = "Sign up now!",
                    color = Color(0xFF27619b),
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.signUpScreen)
                    })
            }

            Spacer(modifier = Modifier.height(15.dp))

            // "Continue as guest" text.
            Row {
                Text(text = "Or ")
                Text(
                    text = "Continue as guest.",
                    color = Color(0xFF27619b),
                    modifier = Modifier.clickable {
                        authViewModel.continueAsGuest()
                        navController.navigate(Routes.wifiScreen)
                    })
            }
        }
    }
}