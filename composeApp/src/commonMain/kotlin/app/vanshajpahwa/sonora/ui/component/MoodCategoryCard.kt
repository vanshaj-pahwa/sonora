package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.clickable
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import app.vanshajpahwa.sonora.extension.angledGradientBackground
import app.vanshajpahwa.sonora.ui.theme.seed
import app.vanshajpahwa.sonora.ui.theme.typo
import org.jetbrains.compose.resources.painterResource
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.monochrome

/**
 * A "Moods & Genres" browse category tile: the [playlistTitleGradient] and Sonora badge of an
 * artwork-less playlist tile, plus the tilted cover Spotify puts on its browse cards.
 *
 * Shared verbatim by Search and Home so the two cannot drift. [colorIndex] is the tile's position
 * in its grid, which is what keeps neighbouring categories visually apart, see [categoryGradient].
 *
 * [artworkUrl] is null until resolved — the category list carries no artwork at all, so the cover
 * costs a separate browse per category and arrives late. The tile is designed to look finished
 * without it.
 */
private const val BADGE_HEIGHT_FRACTION = 0.20f
private const val BADGE_INSET_FRACTION = 0.06f

@Composable
fun MoodCategoryCard(
    title: String,
    artworkUrl: String?,
    colorIndex: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val badge = painterResource(Res.drawable.monochrome)
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(2f)
                .clip(SonoraShapes.Media)
                .angledGradientBackground(
                    colors = categoryGradient(colorIndex),
                    degrees = 45f,
                ).drawBehind {
                    // PlaylistThumbnailPainter sizes the badge off the width because its tile is
                    // square. This one is 2:1, so the same fractions would double it — anchor on
                    // the height instead to keep the badge the size the eye expects.
                    //
                    // Size and inset are stated directly rather than derived from one shared
                    // radius. They were coupled through that value, so shrinking the mark also slid
                    // it into the corner, and the two have nothing to do with each other.
                    //
                    // 0.20 replaces an effective 0.27 of card height. At the size these tiles are
                    // drawn now the mark was competing with the title beside it, and it is a
                    // provenance stamp rather than something to read.
                    val badgeExtent = size.height * BADGE_HEIGHT_FRACTION
                    val inset = size.height * BADGE_INSET_FRACTION
                    // No white disc behind the mark: it sits straight on the gradient. The drawable
                    // is authored black for the themed launcher icon, so it is tinted to the brand
                    // seed here rather than shipping a second copy of the same paths.
                    val badgeSize = size.copy(width = badgeExtent, height = badgeExtent)
                    translate(
                        left = size.width - badgeExtent - inset,
                        top = inset,
                    ) {
                        with(badge) {
                            draw(
                                badgeSize,
                                colorFilter = ColorFilter.tint(seed),
                            )
                        }
                    }
                }.clickable(onClick = onClick),
    ) {
        if (artworkUrl != null) {
            AsyncImage(
                model = artworkUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        // Offset past the corner first, THEN rotate: the tile clips its children,
                        // so letting the cover run off the edge is what produces the cut-off
                        // diagonal instead of a square pasted inside the card.
                        .offset(x = 8.dp, y = 12.dp)
                        .size(64.dp)
                        .rotate(25f)
                        .clip(SonoraShapes.Container),
            )
        }
        Text(
            text = title,
            style = typo().titleSmall,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    // Safe width ends at the LEFT edge of the tilted cover, not just clear of the
                    // badge. The cover is 64.dp rotated 25°, so its bounding box grows to
                    // 64*(cos25+sin25) ≈ 85.dp — about 10.dp wider on each side — and it is offset
                    // 8.dp past the right edge. That leaves ~66.dp of it inside the tile.
                    .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 72.dp),
        )
    }
}
