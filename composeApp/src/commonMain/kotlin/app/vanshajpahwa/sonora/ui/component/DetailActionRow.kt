package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.vanshajpahwa.sonora.ui.icon.Pause
import app.vanshajpahwa.sonora.ui.icon.PlayArrow
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.theme.typo

/**
 * The action cluster every detail page carries: a primary pill flanked by circular secondaries.
 *
 * Album and playlist each drew their own copy of this, 142 and 148 lines of the same Row with the
 * same spacing, the same 48dp circles and the same pill. They drifted, as duplicated UI does: when
 * the play pill moved to the brand colour, playlist and artist were updated and album was missed, so
 * one page kept a white primary action for weeks. There is now one place to change.
 *
 * The primary label and icon are parameters rather than assumed, because the artist page's primary
 * action is Shuffle, not Play. Callers that have no secondary action pass null and the slot closes
 * up rather than leaving a gap.
 */
@Composable
fun DetailActionRow(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier,
    playLabel: String? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading?.invoke()
        Box(
            modifier =
                Modifier
                    .height(48.dp)
                    .widthIn(min = 110.dp)
                    .clip(CircleShape)
                    // Sky Blue on the one control the page exists for. A white primary is the
                    // neutral default every player ships with; the brand colour here is what makes
                    // the detail pages read as the same product.
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onPlayPause() }
                    .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isPlaying) SonoraIcons.Pause else SonoraIcons.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp),
                )
                if (playLabel != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = playLabel,
                        color = Color.Black,
                        style = typo().labelLarge,
                    )
                }
            }
        }
        trailing?.invoke()
    }
}

/**
 * A 48dp circular secondary action, sized to match the primary pill's height.
 *
 * Exposed so a caller's own trailing control (download, sync, more) is the same object as the
 * shuffle button beside it, rather than a similar one built again at the call site.
 */
@Composable
fun DetailCircleButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    /** Overridable so a stateful control, such as Follow, can show its state without losing the shape. */
    containerColor: Color = Color.White.copy(alpha = 0.12f),
) {
    Box(
        modifier =
            modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(containerColor)
                .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
    }
}
