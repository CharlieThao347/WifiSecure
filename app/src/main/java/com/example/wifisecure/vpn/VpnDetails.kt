/*
This file contains code for the VpnDetails data class.
 */

package com.example.wifisecure.vpn

// Stores info about the VPN.
data class VpnDetails(
                      val name: String,
                      val ip: String,
                      val splitTunnelStatus: Boolean,
                      val allowedIPs: String,
                      val city: String,
                      val country: String,
                      val isSelected: Boolean,
                      val isChecked: Boolean,
                      val isDefault: Boolean,
)
