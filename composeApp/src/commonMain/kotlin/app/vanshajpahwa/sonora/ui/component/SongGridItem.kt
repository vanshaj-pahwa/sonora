package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.clickable
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.padding
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.sonora.ui.theme.typo
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade

/**
 * A song as a grid tile: artwork, title, artist.
 *
 * The grid deliberately shows less than the list row does. A row has space for the download state,
 * the explicit badge and an overflow menu; a tile at half the screen width does not, and cramming
 * them in is what makes grid layouts feel worse than the lists they replace. The trade is
 * intentional: the grid is for recognising covers at a glance, the list is for reading detail and
 * reaching per-track actions.
 *
 * The now-playing cue still applies, because losing track of what is playing would be a
 * regression rather than a simplification.
 */
@Composable
fun SongGridItem(
    song: SongEntity,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(SonoraShapes.Media)
                .clickable { onClick() },
    ) {
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalPlatformContext.current)
                    .data(song.thumbnails)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .diskCacheKey(song.thumbnails)
                    .crossfade(true)
                    .build(),
            placeholder = rememberHolderPainter(),
            error = rememberHolderPainter(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(SonoraShapes.Media),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = song.title,
            style = typo().titleSmall,
            color =
                if (isPlaying) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = song.artistName?.joinToString(", ").orEmpty(),
            style = typo().bodySmall,
            // An explicit line height with room below the baseline. The style's default was tight
            // enough to clip descenders on a single line, so names like "Aujla" lost the tail of
            // the j. Bottom padding keeps that clear of the next row of tiles too.
            lineHeight = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 4.dp),
        )
    }
}
