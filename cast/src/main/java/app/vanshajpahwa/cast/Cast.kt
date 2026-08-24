package app.vanshajpahwa.cast

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.Player
import app.vanshajpahwa.logger.Logger

// Google Cast is not shipped in Sonora. Every function here is a safe no-op, which keeps the
// cast plumbing in media3, domain and the UI compiling and permanently inert: initCast reports
// unavailable, wrapWithCastPlayer hands back the local player untouched, and castState never
// leaves NOT_CASTING. Removing that plumbing instead would mean editing the crossfade engine.

fun initCast(context: Context): Boolean {
    Logger.d("Cast", "NON-CAST build: Google Cast is not available")
    return false
}

fun isCastAvailable(): Boolean = false

fun wrapWithCastPlayer(
    context: Context,
    localPlayer: Player,
): Player = localPlayer

fun currentCastDeviceName(): String? = null

@Composable
fun CastIconButton(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
) {
    // No-op: Google Cast is not shipped.
}
