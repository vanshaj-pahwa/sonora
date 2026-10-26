package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.background
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import app.vanshajpahwa.sonora.expect.ui.PlatformBackdrop
import app.vanshajpahwa.sonora.ui.icon.Folder
import app.vanshajpahwa.sonora.ui.icon.PlayArrow
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.theme.typo

/**
 * The card at the top of Home that picks playback back up.
 *
 * This is the one thing the old Home could not do at all. Reopening the app and continuing what you
 * were listening to meant going through Library or hitting the mini player, and if playback had
 * stopped there was no mini player to hit. It sits in the slot a promotional banner would occupy in
 * most music apps, on the grounds that a button that starts your music is worth more than an advert.
 *
 * The play target is 56.dp rather than the 48.dp minimum. This is the first control under the thumb
 * on the screen the app opens to, and it gets used while walking or in the gym.
 */
@Composable
fun HomeResumeCard(
    title: String,
    subtitle: String,
    thumbnail: String?,
    modifier: Modifier = Modifier,
    backdrop: PlatformBackdrop? = null,
    /** e.g. "1:12 left", or null when the remaining time is unknown. */
    remainingLabel: String? = null,
    onPlay: () -> Unit,
) {
    val shape = SonoraShapes.Media
    // Glass here and not on the sections further down, because this card is the one surface on Home
    // that sits over the artwork gradient. Glass over the flat background below it would cost a
    // shader pass to look identical to a translucent rectangle.
    val surface =
        if (backdrop != null) {
            Modifier.liquidGlass(backdrop, shape, interactive = false)
        } else {
            Modifier
                .clip(shape)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        ),
                    ),
                )
        }
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = HomeGutter)
                .then(surface)
                .clip(shape)
                .clickable(onClick = onPlay)
                .padding(12.dp),
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
                    .size(60.dp)
                    .clip(SonoraShapes.Media),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "PICK UP WHERE YOU LEFT OFF",
                style = typo().bodySmall.copy(fontWeight = FontWeight.Bold),
                
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = title,
                style = typo().titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                // Artist only. The remaining time used to be appended after a separator, which put a
                // stray dot and a countdown on a card whose whole job is to be tapped without
                // reading. How far in you are is not a decision you make from the home screen.
                text = subtitle,
                style = typo().bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(10.dp))
        Box(
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = SonoraIcons.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

/**
 * The three shortcuts under the rotation grid: Liked, Offline, Recent.
 *
 * All three screens already existed and were already routable; nothing on Home pointed at them, so
 * reaching your own liked songs meant a trip through Library. Each tile carries its count, which is
 * also the honest way to show an empty library: a tile reading zero is more useful than a tile that
 * silently leads nowhere.
 */
@Composable
fun HomeShortcutTile(
    label: String,
    count: Int,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    backdrop: PlatformBackdrop? = null,
    onClick: () -> Unit,
) {
    val shape = SonoraShapes.Container
    val surface =
        if (backdrop != null) {
            Modifier.liquidGlass(backdrop, shape, interactive = false)
        } else {
            Modifier.clip(shape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
        }
    Column(
        modifier =
            modifier
                .then(surface)
                .clip(shape)
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            style = typo().bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
        )
        Text(
            text = count.toString(),
            style = typo().bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

/** Artwork tile used by the rotation grid, sharing the resume card's corner language. */
@Composable
fun HomeGridArtwork(
    thumbnail: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(SonoraShapes.Media),
    ) {
        AsyncImage(
            model = thumbnail,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = rememberHolderPainter(),
            error = rememberHolderPainter(),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/**
 * Wide folder tile, used for the Local files entry point in the offline section.
 *
 * Wider than the square shortcut tiles beside it and carrying a subtitle, because it is the one
 * destination on that row that needs explaining: "Downloaded" is self evident, "Local files" is not
 * obviously "the music already on your phone" until it says so.
 */
@Composable
fun HomeFolderTile(
    label: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    backdrop: PlatformBackdrop? = null,
    onClick: () -> Unit,
) {
    val shape = SonoraShapes.Container
    val surface =
        if (backdrop != null) {
            Modifier.liquidGlass(backdrop, shape, interactive = false)
        } else {
            Modifier.clip(shape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
        }
    Row(
        modifier =
            modifier
                .then(surface)
                .clip(shape)
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp, horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = SonoraIcons.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = typo().bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
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

/**
 * Circular artist portrait for the Home artists row.
 *
 * Circular on purpose: it is the convention that separates a person from a record everywhere else in
 * music software, so the shape alone says "artist" before the name is read. Fixed 72.dp so the row
 * keeps a steady rhythm however long the names are, with the label allowed two lines beneath.
 */
@Composable
fun HomeArtistBubble(
    name: String,
    thumbnail: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier.width(72.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = thumbnail,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = rememberHolderPainter(),
            error = rememberHolderPainter(),
            modifier = Modifier.size(72.dp).clip(CircleShape),
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = name,
            style = typo().bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}
