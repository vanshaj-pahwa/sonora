package app.vanshajpahwa.sonora.expect

import android.os.PowerManager
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService

@Composable
actual fun isMotionReduced(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        // ANIMATOR_DURATION_SCALE at 0 is how Android reports "remove animations", both from the
        // accessibility setting and from developer options. Battery saver is checked separately
        // because it suppresses motion without touching that scale.
        val animationsOff =
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f,
            ) == 0f
        val powerSaving = context.getSystemService<PowerManager>()?.isPowerSaveMode == true
        animationsOff || powerSaving
    }
}
