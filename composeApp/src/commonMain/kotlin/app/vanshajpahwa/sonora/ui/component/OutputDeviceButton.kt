package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.vanshajpahwa.sonora.expect.OutputKind
import app.vanshajpahwa.sonora.expect.openOutputSwitcher
import app.vanshajpahwa.sonora.expect.rememberOutputDevice
import app.vanshajpahwa.sonora.ui.icon.Bluetooth
import app.vanshajpahwa.sonora.ui.icon.Cast
import app.vanshajpahwa.sonora.ui.icon.DirectionsCar
import app.vanshajpahwa.sonora.ui.icon.Headphones
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.icon.Speaker
import app.vanshajpahwa.sonora.ui.icon.VolumeUp

/**
 * Where the sound is going, next to the control for how you feel about it.
 *
 * Answers one question at a glance: is this playing where I meant it to. The failure worth catching
 * is specific and loud, music arriving from the phone speaker because a pair of earbuds dropped
 * without announcing it, and nothing in the app said so until now.
 *
 * Always accented. It is the one control in the row reporting a fact rather than offering an action,
 * and the accent is what separates the two at a glance.
 */
@Composable
fun OutputDeviceButton(
    modifier: Modifier = Modifier,
    size: Int = 22,
    // Matches the footprint of whatever sits beside it. Equal gaps come from equal boxes, not from
    // equal spacers: a narrower box with the same spacer either side reads as a wider gap.
    box: Int = 30,
) {
    val device = rememberOutputDevice()
    val icon =
        when (device.kind) {
            OutputKind.Phone -> SonoraIcons.VolumeUp
            OutputKind.Wired -> SonoraIcons.Headphones
            OutputKind.Bluetooth -> SonoraIcons.Bluetooth
            OutputKind.Speaker -> SonoraIcons.Speaker
            OutputKind.Cast -> SonoraIcons.Cast
            OutputKind.Car -> SonoraIcons.DirectionsCar
        }
    Box(
        modifier =
            modifier
                .size(box.dp)
                .clip(CircleShape)
                .clickable { openOutputSwitcher() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            // The device names itself, so the label is accurate even where the icon has to be
            // general: Android reports every Bluetooth route the same way whether it is earbuds, a
            // speaker or a car.
            contentDescription = device.name.takeIf { it.isNotBlank() },
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(size.dp),
        )
    }
}
