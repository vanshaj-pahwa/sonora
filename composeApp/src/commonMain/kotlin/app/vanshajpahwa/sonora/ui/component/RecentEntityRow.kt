package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.background
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import app.vanshajpahwa.sonora.ui.theme.typo

/**
 * One previously opened item: artist, song, album, playlist or podcast.
 *
 * The shape of the thumbnail carries the type as much as the label does, which is the convention
 * every music app already trains people on: artists are circles, everything else is a rounded
 * square. That is why this takes [isCircular] rather than deriving a single look for all of them.
 *
 * [typeLabel] is shown rather than left implicit because two of these can look identical otherwise:
 * an album and a playlist with the same cover art are indistinguishable without it.
 */
@Composable
fun RecentEntityRow(
    title: String,
    subtitle: String,
    typeLabel: String,
    thumbnail: String?,
    isCircular: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(SonoraShapes.Media)
                .clickable(onClick = onClick)
                .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = thumbnail,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = rememberHolderPainter(),
            error = rememberHolderPainter(),
            modifier =
                Modifier
                    .size(48.dp)
                    .clip(if (isCircular) CircleShape else SonoraShapes.Media),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = typo().titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .clip(SonoraShapes.Container)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = typeLabel,
                        style = typo().bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
                if (subtitle.isNotBlank()) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = subtitle,
                        style = typo().bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
