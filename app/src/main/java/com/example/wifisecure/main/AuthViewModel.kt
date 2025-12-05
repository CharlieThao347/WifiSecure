/*
This file contains code for the view model regarding authentication.
Holds and updates authentication logic and state.
 */

package com.example.wifisecure.main

import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Authentication View Model.
class AuthViewModel : ViewModel() {
    // Firebase Authentication service instance.
    private val auth = Firebase.auth
    // Stores authentication states. Only accessible by the ViewModel.
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    // Read only version of _authState.
    val authState: StateFlow<AuthState> = _authState

    // Login function. Handled by Firebase. Updates authentication state.
    fun login(email : String, password : String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { loginTask ->
                _authState.value = AuthState.Authenticated
            }
            .addOnFailureListener { loginTask ->
                _authState.value = AuthState.Error("Error: ${loginTask.message}")
            }
    }

    // Signup function. Handled by Firebase. Updates authentication state.
    fun signUp(name: String, email : String, password : String){
        val db = Firebase.firestore
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener{ signUpTask->
                // Retrieves the user ID.
                val uid = signUpTask.user!!.uid
                // Save the user's name in Firestore in the correct place.
                val data = mapOf("name" to name)
                db.collection("users")
                    .document(uid)
                    .set(data)
                _authState.value = AuthState.SignUpSuccess(
                    "Account successfully created")
            }
            .addOnFailureListener { signUpTask->
                _authState.value = AuthState.Error("Error: ${signUpTask.message}")
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