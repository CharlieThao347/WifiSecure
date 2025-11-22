/*
This file contains code for the view model regarding authentication.
Holds and updates authentication logic and state.
 */

package com.example.wifisecure.ui.theme

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Authentication View Model.
class AuthViewModel : ViewModel() {
    // Firebase Authentication service instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    // Stores authentication states. Only accessible by the ViewModel.
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    // Read only version of _authState.
    val authState: StateFlow<AuthState> = _authState

    // Login function. Handled by Firebase. Updates authentication state.
    fun login(email : String, password : String){
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ loginTask->
                if(loginTask.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }
                else{
                    _authState.value = AuthState.Error(loginTask.exception?.message?:
                    "An error occurred")
                }
            }
    }

    // Signup function. Handled by Firebase. Updates authentication state.
    fun signUp(email : String, password : String){
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ signUpTask->
                if(signUpTask.isSuccessful){
                    _authState.value = AuthState.SignUpSuccess(
                    "Account successfully created")
                }
                else{
                    _authState.value = AuthState.Error(signUpTask.exception?.message?:
                    "An error occurred")
                }
            }
    }

    // Updates state after successful account creation to "Unauthenticated".
    fun updateFromSignUpState(){
        _authState.value = AuthState.Unauthenticated
    }

    // Updates state to "Guest" after using application as a guest.
    fun continueAsGuest(){
        _authState.value = AuthState.Guest
    }

    // Logs user out and updates state to "Unauthenticated".
    fun logout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

}

// Holds all the possible authentication states.
sealed class AuthState{
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
    object Guest: AuthState()
    object Loading: AuthState()
    data class SignUpSuccess(val message :String) : AuthState()
    data class Error(val message :String) : AuthState()
}