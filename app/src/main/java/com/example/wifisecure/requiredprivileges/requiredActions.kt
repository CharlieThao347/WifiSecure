package com.example.wifisecure.requiredprivileges

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.compose.runtime.Composable

// Check if location services are enabled
fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    return isNetworkEnabled || isGpsEnabled
}

// Redirect user to location settings
fun directToLocationServices(context: Context){
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(intent)
}

// Composable that asks for user to enable location services if it is not enabled.
@Composable
fun LocationServicesDialog(context: Context){
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
    builder
        .setMessage("This app requires 'Location' to be enabled for Wi-Fi scanning.")
        .setTitle("Use Location?")
        .setPositiveButton("Yes") { dialog, which ->
            directToLocationServices(context)
        }
        .setNegativeButton("No") { dialog, which ->
        }
    val dialog: AlertDialog = builder.create()
    dialog.show()
}