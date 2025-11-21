/*
This file contains the code for the forgot password page.
 */

package com.example.wifisecure.forgotpassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.wifisecure.ui.theme.Routes
import kotlin.text.ifEmpty

// Composable that renders the forgot password page.
@Composable
fun ForgotPasswordScreen(navController: NavController)
{
    // State for email.
    var email by remember { mutableStateOf("") }
    // State for email error message.
    var emailError by remember { mutableStateOf("") }

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
        var speed by remember { mutableStateOf(.8f) }
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
                    .size(300.dp)
                    .align(Alignment.CenterHorizontally),
            )
            /*
        Code taken from GeeksforGeeks ends here.
        */

            // "Forgot password" text.
            Text(text = "Forgot Password", fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(20.dp))

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

            Spacer(modifier = Modifier.height(25.dp))

            // Enter button.
            Button(
                onClick = {
                    // Set error messages if user tries to click "Enter" button with an empty text field.
                    emailError = if (email.isBlank()) "Email is required" else ""
                    if (emailError.isEmpty()) {
                        //handle forgot password logic
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 50.dp)
                    .height(47.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF27619b),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Enter")
            }
        }
    }
}