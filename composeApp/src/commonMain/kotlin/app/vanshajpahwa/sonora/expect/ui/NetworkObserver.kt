package app.vanshajpahwa.sonora.expect.ui

import androidx.compose.runtime.Composable

/** Calls back when the system's default network is lost, and when a validated one is available. */
@Composable
expect fun ObserveNetwork(
    onAvailable: () -> Unit,
    onLost: () -> Unit,
)
