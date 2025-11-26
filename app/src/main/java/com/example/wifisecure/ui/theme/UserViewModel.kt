package com.example.wifisecure.ui.theme

/*
This file contains code for the view model regarding the User.
Holds and updates user logic and state.
 */

import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// User View Model.
class UserViewModel : ViewModel() {
    // Stores User's uid. Only accessible by the ViewModel.
    private val _uid = MutableStateFlow("")
    // Read-only version of _uid that is used by the UI layer.
    val uid: StateFlow<String> = _uid
    // Stores User's name. Only accessible by the ViewModel.
    private val _name = MutableStateFlow("")
    // Read-only version of _name that is used by the UI layer.
    val name: StateFlow<String> = _name
    // State for whether or not user info was already retrieved. Only accessible by the ViewModel.
    private val _retrieved = MutableStateFlow(false)
    // Read-only version of _retrieved that is used by the UI layer.
    val retrieved: StateFlow<Boolean> = _retrieved

    // Retrieve user info.
    fun retrieveUser() {
        // If user info has already been retrieved, then return.
        if(_retrieved.value) {
            return
        }
        // Otherwise, retrieve the info.
        _uid.value = Firebase.auth.currentUser!!.uid
        retrieveName()
        _retrieved.value = true
    }

    // Retrieve name when user logs in.
    fun retrieveName() {
        Firebase.firestore.collection("users")
            .document(_uid.value)
            .get()
            .addOnSuccessListener { doc ->
                if(doc.exists()) {
                    _name.value = doc.getString("name") ?: "Null"
                } else {
                    _name.value = "FailedToGetName"
                }
            }
            .addOnFailureListener { e ->
                _name.value = "FailedToGetName"
            }
    }

    // Clear user info.
    fun clearUser() {
        _uid.value = ""
        _name.value = ""
        _retrieved.value = false
    }
}