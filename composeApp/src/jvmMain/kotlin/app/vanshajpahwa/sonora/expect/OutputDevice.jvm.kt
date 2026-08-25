package app.vanshajpahwa.sonora.expect

import androidx.compose.runtime.Composable

/**
 * Desktop has no route to report, and the jvm target is not shipped, so this states the only thing
 * that is true rather than inventing a device.
 */
@Composable
actual fun rememberOutputDevice(): OutputDevice = OutputDevice(OutputKind.Speaker)
