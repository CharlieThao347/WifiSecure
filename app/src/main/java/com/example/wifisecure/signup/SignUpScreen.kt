/*
This file contains code for the signup screen.
 */

package com.example.wifisecure.signup

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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.wifisecure.ui.theme.AuthState
import com.example.wifisecure.ui.theme.AuthViewModel
import com.example.wifisecure.ui.theme.Routes

// Composable that renders the login page.
@Composable
fun SignUpScreen(navController: NavController, authViewModel: AuthViewModel) {
    // Used to access app resources and information. Tied
    // to the activity (MainActivity).
    val activityContext = LocalContext.current
    // ViewModel variable for authentication state.
    val authState = authViewModel.authState.collectAsState()
    // Runs when the authentication state changes.
    LaunchedEffect(authState.value) {
        when(authState.value) {
            // Displays success message, update auth state,
            // and navigate to login screen if
            // account was successfully created.
            is AuthState.SignUpSuccess -> {
                Toast.makeText(
                    activityContext,
                    (authState.value as AuthState.SignUpSuccess).message,
                    Toast.LENGTH_SHORT
                ).show()
                authViewModel.updateFromSignUpState()
                navController.navigate(Routes.loginScreen)
            }
            // Display error message if error occurred.
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

    // State for name.
    var name by remember { mutableStateOf("") }
    // State for email.
    var email by remember { mutableStateOf("") }
    // State for password.
    var password by remember { mutableStateOf("") }
    // State for confirm password.
    var confirmPassword by remember { mutableStateOf("") }
    // State for toggling visibility of password.
    var showPassword by remember { mutableStateOf(false) }
    // State for toggling visibility of confirm password.
    var showConfirmPassword by remember { mutableStateOf(false) }
    // State for name error message.
    var nameError by remember { mutableStateOf("") }
    // State for email error message.
    var emailError by remember { mutableStateOf("") }
    // State for password error message.
    var passwordError by remember { mutableStateOf("") }
    // State for confirm password error message.
    var confirmPasswordError by remember { mutableStateOf("") }

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
                    .size(300.dp)
                    .align(Alignment.CenterHorizontally),
            )
            /*
        Code taken from GeeksforGeeks ends here.
        */

            // "Create Account" text.
            Text(text = "Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(20.dp))

            // Name text field.
            TextField(
                value = name,
                onValueChange = { name = it },
                // Placeholder text in the field is "Name" with default color.
                // If user tries to login with empty field, display name error text in red.
                label = {
                    Text(
                        nameError.ifEmpty { "Name" },
                        color = if (nameError.isNotEmpty()) Red else Unspecified
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Person Icon"
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

            Spacer(modifier = Modifier.height(8.dp))

            // confirmPassword text field.
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                // Placeholder text in the field is "Confirm Password" with default color.
                // If user tries to login with empty field, display confirmPassword error text in red.
                label = {
                    Text(
                        confirmPasswordError.ifEmpty { "Confirm Password" },
                        color = if (confirmPasswordError.isNotEmpty()) Red else Unspecified
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
                        modifier = Modifier.clickable { showConfirmPassword = !showConfirmPassword },
                        imageVector = if (showConfirmPassword)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff,
                        contentDescription = "Visibility Icon"
                    )
                },
                // If showConfirmPassword is true, then show the confirmPassword. Otherwise, obscure the confirmPassword.
                visualTransformation = if (showConfirmPassword) VisualTransformation.None
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

            // Register button.
            Button(
                onClick = {
                    // Set error messages if user tries to click "Register" button with an empty text field.
                    nameError = if (name.isBlank()) "Name is required" else ""
                    emailError = if (email.isBlank())
                                    "Email is required"
                                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                                    "Please enter a valid email"
                                else
                                    ""
                    passwordError = if (password.isBlank()) "Password is required" else ""
                    confirmPasswordError = if (confirmPassword.isBlank())
                                                "Password is required"
                                            else if (confirmPassword != password)
                                                "Passwords do not match"
                                            else
                                                ""
                    // Signup.
                    if (nameError.isEmpty() && emailError.isEmpty() && passwordError.isEmpty() && confirmPasswordError.isEmpty()) {
                        authViewModel.signUp(name, email, password)
                    }
                },
                enabled = authState.value != AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 50.dp)
                    .height(47.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF27619b),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Register")
            }

            Spacer(modifier = Modifier.height(18.dp))

            // "Already have an account" text.
            Row {
                Text(text = "Already have an account? ")
                Text(
                    text = "Login",
                    color = Color(0xFF27619b),
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.loginScreen)
                    })
            }
        }
    }
}