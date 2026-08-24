package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.vanshajpahwa.sonora.ui.icon.Pause
import app.vanshajpahwa.sonora.ui.icon.PlayArrow
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons

@Composable
fun RippleIconButton(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    fillMaxSize: Boolean = false,
    tint: Color = Color.White,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector,
            null,
            tint = tint,
            modifier = if (fillMaxSize) Modifier.fillMaxSize().padding(4.dp) else Modifier,
        )
    }
}

@Composable
fun PlayPauseButton(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    onClick: () -> Unit,
) {
    RippleIconButton(
        if (!isPlaying) {
            SonoraIcons.PlayArrow
        } else {
            SonoraIcons.Pause
        },
        modifier = modifier,
        tint = tint,
        onClick = onClick,
    )
}
