package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.background
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.sonora.expect.ui.PlatformBackdrop
import app.vanshajpahwa.sonora.ui.icon.Album
import app.vanshajpahwa.sonora.ui.icon.Downloading
import app.vanshajpahwa.sonora.ui.icon.Favorite
import app.vanshajpahwa.sonora.ui.icon.Insights
import app.vanshajpahwa.sonora.ui.icon.Podcasts
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.icon.TrendingUp
import app.vanshajpahwa.sonora.ui.theme.typo
import org.jetbrains.compose.resources.stringResource
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.albums
import app.vanshajpahwa.sonora.resources.downloaded
import app.vanshajpahwa.sonora.resources.favorite
import app.vanshajpahwa.sonora.resources.followed
import app.vanshajpahwa.sonora.resources.most_played
import app.vanshajpahwa.sonora.resources.podcasts

/**
 * Category accents, rotated off the theme's own primary rather than hardcoded.
 *
 * The brand hue is reserved for state: the active tab, the mini player, playback controls. If the
 * categories borrowed it too, they would stop being distinguishable from each other AND the brand
 * would stop reading as "active", because it would be on everything.
 *
 * Offsets are fixed, so changing the seed or the custom theme colour rotates the whole set together
 * and keeps both the spacing between categories and the gap around the brand.
 */
private const val ACCENT_SATURATION = 0.50f
private const val ACCENT_LIGHTNESS = 0.68f

private fun hueOf(color: Color): Float {
    val r = color.red
    val g = color.green
    val b = color.blue
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val d = max - min
    if (d == 0f) return 0f
    val h =
        when (max) {
            r -> ((g - b) / d) % 6f
            g -> (b - r) / d + 2f
            else -> (r - g) / d + 4f
        }
    return (h * 60f + 360f) % 360f
}

private fun accent(
    brand: Color,
    offset: Float,
): Color = Color.hsl((hueOf(brand) + offset) % 360f, ACCENT_SATURATION, ACCENT_LIGHTNESS)

/**
 * The offsets are spread evenly rather than picked by eye, and that is not a detail.
 *
 * Hand-picking hues that each looked reasonable on its own produced two pairs only 35 and 47 degrees
 * apart, which at this size is close enough to hesitate over. Placing them evenly instead, across
 * the arc that remains once the 90 degrees around the brand hue is reserved, puts every pair at
 * least 54 degrees apart. That is the widest minimum separation six categories can have while still
 * leaving the brand its own band, so it is the best available rather than merely better.
 *
 * The icon is still the primary distinguisher. The accent reinforces it, it does not carry it alone.
 * Distinguishing six categories by hue alone would be asking colour to do a job it is bad at, in
 * sunlight and for anyone with a colour vision deficiency both.
 */
private const val OFFSET_DOWNLOADED = 315f
private const val OFFSET_MOST_PLAYED = 207f
private const val OFFSET_FAVORITE = 153f
private const val OFFSET_FOLLOWED = 261f
private const val OFFSET_ALBUMS = 45f
private const val OFFSET_PODCASTS = 99f

// Shape and fill come from SonoraCard, shared with the Home mood and genre tiles.
private val CardShape = SonoraCardShape
private const val STACK_COVERS = 3

/**
 * The shortcut block at the top of Library.
 *
 * Downloaded and Most Played lead because Library is their only entry point in the app. Favorite is
 * deliberately not among them: it already has a permanent home in the Home header, so a second
 * oversized entrance here would spend space without adding a way in.
 */
@Composable
fun LibraryShortcuts(
    downloaded: List<SongEntity>,
    mostPlayed: List<SongEntity>,
    favoriteCount: Int,
    followedCount: Int,
    onDownloaded: () -> Unit,
    onMostPlayed: () -> Unit,
    onFavorite: () -> Unit,
    onFollowed: () -> Unit,
    onAlbums: () -> Unit,
    onPodcasts: () -> Unit,
    backdrop: PlatformBackdrop? = null,
) {
    val brand = MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LibraryPrimaryCard(
            label = stringResource(Res.string.downloaded),
            icon = SonoraIcons.Downloading,
            accent = accent(brand, OFFSET_DOWNLOADED),
            songs = downloaded,
            backdrop = backdrop,
            showCount = true,
            onClick = onDownloaded,
        )
        LibraryPrimaryCard(
            label = stringResource(Res.string.most_played),
            icon = SonoraIcons.TrendingUp,
            accent = accent(brand, OFFSET_MOST_PLAYED),
            songs = mostPlayed,
            backdrop = backdrop,
            showCount = false,
            onClick = onMostPlayed,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LibraryCompactCard(
                label = stringResource(Res.string.favorite),
                icon = SonoraIcons.Favorite,
                accent = accent(brand, OFFSET_FAVORITE),
                trailing = favoriteCount.takeIf { it > 0 }?.toString(),
                modifier = Modifier.weight(1f),
                backdrop = backdrop,
                onClick = onFavorite,
            )
            LibraryCompactCard(
                label = stringResource(Res.string.followed),
                icon = SonoraIcons.Insights,
                accent = accent(brand, OFFSET_FOLLOWED),
                trailing = followedCount.takeIf { it > 0 }?.toString(),
                modifier = Modifier.weight(1f),
                backdrop = backdrop,
                onClick = onFollowed,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LibraryCompactCard(
                label = stringResource(Res.string.albums),
                icon = SonoraIcons.Album,
                accent = accent(brand, OFFSET_ALBUMS),
                trailing = null,
                modifier = Modifier.weight(1f),
                backdrop = backdrop,
                onClick = onAlbums,
            )
            LibraryCompactCard(
                label = stringResource(Res.string.podcasts),
                icon = SonoraIcons.Podcasts,
                accent = accent(brand, OFFSET_PODCASTS),
                trailing = null,
                modifier = Modifier.weight(1f),
                backdrop = backdrop,
                onClick = onPodcasts,
            )
        }
    }
}

/**
 * A leading shortcut, showing the covers of what is inside as a stack on the trailing edge.
 *
 * This started as artwork bled behind the label under a gradient scrim, which is the shape the
 * design called for and the shape it could not survive. At this card size the result reads as a
 * rendering fault rather than a decision: a hard vertical seam where the ramp gives out, and covers
 * desaturated into grey by the very scrim protecting the text.
 *
 * Discrete covers fix the cause instead of tuning the symptom. There is no scrim, so nothing can go
 * muddy and no text ever sits on artwork. The covers keep their real colour, the stack reads as
 * deliberate, and the card still answers "what is in here" before it is opened.
 */
@Composable
private fun LibraryPrimaryCard(
    label: String,
    icon: ImageVector,
    accent: Color,
    songs: List<SongEntity>,
    backdrop: PlatformBackdrop?,
    showCount: Boolean,
    onClick: () -> Unit,
) {
    val fill = sonoraCardFill()
    val covers =
        remember(songs) {
            songs.mapNotNull { it.thumbnails?.takeIf { url -> url.isNotBlank() } }.take(STACK_COVERS)
        }
    val names = remember(songs) { songs.take(3).joinToString(", ") { it.title } }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(68.dp)
                .then(
                    if (backdrop != null) {
                        Modifier.liquidGlass(backdrop, CardShape, interactive = false)
                    } else {
                        Modifier.clip(CardShape).background(fill)
                    },
                ).clip(CardShape)
                .clickable(onClick = onClick)
                .padding(start = 14.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AccentIcon(icon = icon, accent = accent, size = 36.dp, glyph = 19.dp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            // The count sits beside the label as a badge, not in front of the track names.
            //
            // Run together as one string it read as "1 . 52 Bars", where the separator floats and
            // the leading digit looks like it belongs to the title that follows it. They answer two
            // different questions, how many and what is inside, so they are two different objects.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    style = typo().bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (showCount && songs.isNotEmpty()) {
                    Spacer(Modifier.width(7.dp))
                    CountBadge(count = songs.size, accent = accent)
                }
            }
            if (names.isNotBlank()) {
                Text(
                    text = names,
                    style = typo().bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (covers.isNotEmpty()) {
            Spacer(Modifier.width(10.dp))
            CoverStack(covers = covers, borderColor = fill)
        }
    }
}

/**
 * Covers drawn as an overlapping stack, most recent on top.
 *
 * The border is painted in the card's own fill rather than a translucent white, so each cover is
 * separated by a clean edge that belongs to the surface instead of a grey halo sitting on top of it.
 */
@Composable
private fun CoverStack(
    covers: List<String>,
    borderColor: Color,
) {
    val shape = SonoraShapes.Media
    Row(horizontalArrangement = Arrangement.spacedBy((-10).dp)) {
        // Reversed so the first cover, the one the destination actually leads with, draws last and
        // therefore sits on top of the stack.
        covers.reversed().forEach { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = rememberHolderPainter(),
                error = rememberHolderPainter(),
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(shape)
                        .border(width = 2.dp, color = borderColor, shape = shape),
            )
        }
    }
}

/**
 * The count, as a quiet badge tinted with the category's own accent.
 *
 * Tinting rather than filling keeps it readable as a secondary detail: at full accent it would
 * outweigh the label it sits beside, which is the wrong way round for a number nobody navigates by.
 */
@Composable
private fun CountBadge(
    count: Int,
    accent: Color,
) {
    Box(
        modifier =
            Modifier
                .clip(SonoraShapes.Container)
                .background(accent.copy(alpha = 0.16f))
                .padding(horizontal = 6.dp, vertical = 1.dp),
    ) {
        Text(
            text = count.toString(),
            style = typo().bodySmall.copy(fontWeight = FontWeight.Medium),
            color = accent,
            maxLines = 1,
        )
    }
}

@Composable
private fun LibraryCompactCard(
    label: String,
    icon: ImageVector,
    accent: Color,
    trailing: String?,
    modifier: Modifier = Modifier,
    backdrop: PlatformBackdrop?,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .height(54.dp)
                .then(
                    if (backdrop != null) {
                        Modifier.liquidGlass(backdrop, CardShape, interactive = false)
                    } else {
                        Modifier.clip(CardShape).background(sonoraCardFill())
                    },
                ).clip(CardShape)
                .clickable(onClick = onClick)
                .padding(start = 14.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AccentIcon(icon = icon, accent = accent, size = 28.dp, glyph = 15.dp)
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            style = typo().bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (trailing != null) {
            Spacer(Modifier.width(6.dp))
            Text(
                text = trailing,
                style = typo().bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AccentIcon(
    icon: ImageVector,
    accent: Color,
    size: Dp,
    glyph: Dp,
) {
    Box(
        modifier = Modifier.size(size).clip(CircleShape).background(accent.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(glyph),
        )
    }
}
