package app.vanshajpahwa.sonora.expect.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.vanshajpahwa.cast.CastIconButton

@Composable
actual fun PlatformCastButton(
    modifier: Modifier,
    tint: Color,
) {
    CastIconButton(modifier = modifier, tint = tint)
}
