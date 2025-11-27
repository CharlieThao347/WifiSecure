/*
This file contains the code for all the VPN states.
 */

package com.example.wifisecure.vpn

sealed class VpnState {
    object Connecting : VpnState()
    object Connected : VpnState()
    object Disconnecting : VpnState()
    object Disconnected : VpnState()
}