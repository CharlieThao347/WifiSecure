/*
This file contains code for the VpnDetails data class, which is for the default server.
 */

package com.example.wifisecure.vpn

data class VpnDetails(
                      val name: String,
                      val ip: String,
                      val splitTunnelStatus: Boolean,
                      val allowedIPs: String,
                      val city: String,
                      val country: String,
                      val isSelected: Boolean = false,
                      val isChecked: Boolean = false
)