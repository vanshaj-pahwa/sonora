package app.vanshajpahwa.sonora.expect.ui

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun ObserveNetwork(
    onAvailable: () -> Unit,
    onLost: () -> Unit,
) {
    val context = LocalContext.current
    val currentOnAvailable by rememberUpdatedState(onAvailable)
    val currentOnLost by rememberUpdatedState(onLost)
    DisposableEffect(context) {
        val manager = context.getSystemService(ConnectivityManager::class.java)
        val callback =
            object : ConnectivityManager.NetworkCallback() {
                // Validated rather than merely connected: a captive portal or a dead hotspot is
                // "available" and would flash Back online for a feed that still cannot load.
                override fun onCapabilitiesChanged(
                    network: Network,
                    capabilities: NetworkCapabilities,
                ) {
                    if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        currentOnAvailable()
                    }
                }

                // The default network callback only reports a loss when nothing replaces it, so a
                // hand over from Wi-Fi to mobile data does not show the bar.
                override fun onLost(network: Network) {
                    currentOnLost()
                }
            }
        manager?.registerDefaultNetworkCallback(callback)
        onDispose { manager?.unregisterNetworkCallback(callback) }
    }
}
