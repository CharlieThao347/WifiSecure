/*
This file contains code for the WireGuardTunnel Class.
 */

package com.example.wifisecure.vpn

import com.wireguard.android.backend.Tunnel

// WireGuard Tunnel.
class WireGuardTunnel : Tunnel {
    override fun getName(): String {
        return "Tunnel"
    }
    override fun onStateChange(newState: Tunnel.State) {
    }
}