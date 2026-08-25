package app.vanshajpahwa.sonora.expect

import android.media.AudioAttributes
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService

@Composable
actual fun rememberOutputDevice(): OutputDevice {
    val context = LocalContext.current
    val audioManager = remember(context) { context.getSystemService<AudioManager>() }
    var device by remember { mutableStateOf(audioManager.currentOutput()) }

    // Registered rather than polled. Routes change when a plug is pulled or a connection drops,
    // which is exactly the moment this needs to be right, and a poll would either be late or run
    // forever for something that changes a handful of times a session.
    DisposableEffect(audioManager) {
        val manager = audioManager ?: return@DisposableEffect onDispose { }
        val callback =
            object : AudioDeviceCallback() {
                override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
                    device = manager.currentOutput()
                }

                override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
                    device = manager.currentOutput()
                }
            }
        manager.registerAudioDeviceCallback(callback, null)
        onDispose { manager.unregisterAudioDeviceCallback(callback) }
    }
    return device
}

/**
 * Reads the route audio would take right now.
 *
 * Asks the platform which device it would use for music rather than listing what is connected and
 * choosing: a phone can have three outputs attached at once, and only one of them is playing.
 */
private fun AudioManager?.currentOutput(): OutputDevice {
    val manager = this ?: return OutputDevice(OutputKind.Phone)
    val routed =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val attributes =
                AudioAttributes
                    .Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            manager.getAudioDevicesForAttributes(attributes).firstOrNull()
        } else {
            // Before the routing query existed, the best available answer is the connected outputs,
            // preferring anything that is not the built in speaker: if something is plugged in or
            // paired, that is where music goes.
            manager
                .getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                .sortedBy { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
                .firstOrNull()
        } ?: return OutputDevice(OutputKind.Phone)

    val kind =
        when (routed.type) {
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
            AudioDeviceInfo.TYPE_WIRED_HEADSET,
            AudioDeviceInfo.TYPE_USB_HEADSET,
            -> OutputKind.Wired

            AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
            AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
            -> OutputKind.Bluetooth

            AudioDeviceInfo.TYPE_USB_DEVICE,
            AudioDeviceInfo.TYPE_USB_ACCESSORY,
            AudioDeviceInfo.TYPE_DOCK,
            AudioDeviceInfo.TYPE_AUX_LINE,
            AudioDeviceInfo.TYPE_LINE_ANALOG,
            AudioDeviceInfo.TYPE_LINE_DIGITAL,
            -> OutputKind.Speaker

            AudioDeviceInfo.TYPE_HDMI,
            AudioDeviceInfo.TYPE_HDMI_ARC,
            -> OutputKind.Cast

            else -> OutputKind.Phone
        }
    // The built in speaker needs no name, and naming it would only add a second thing to read.
    val name = if (kind == OutputKind.Phone) "" else routed.productName?.toString().orEmpty()
    return OutputDevice(kind = kind, name = name)
}
