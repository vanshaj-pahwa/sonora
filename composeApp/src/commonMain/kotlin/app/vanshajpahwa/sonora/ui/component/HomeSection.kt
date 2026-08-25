package app.vanshajpahwa.sonora.ui.component

import androidx.compose.animation.Crossfade
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import app.vanshajpahwa.sonora.ui.icon.ArrowForwardIos
import app.vanshajpahwa.sonora.ui.icon.PlayArrow
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.theme.typo

/** Horizontal gutter every Home section shares, so section titles and cards line up down the page. */
val HomeGutter = 16.dp

/**
 * Section heading with an optional destination on the right.
 *
 * The title used to be dead text. Giving each section a "See all" turns the heading into the way
 * into the full list, which is where the extra items that do not fit in a row of three have been
 * living all along with no signpost.
 */
@Composable
fun HomeSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    // The part of the title that names something specific, drawn as if marked by hand. Null leaves
    // the header plain, which is right for a fixed heading like Your rotation.
    underline: String? = null,
) {
    val accent = MaterialTheme.colorScheme.primary
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = HomeGutter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = typo().titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { layout = it },
            modifier =
                Modifier
                    .weight(1f, fill = false)
                    .drawBehind {
                        val result = layout ?: return@drawBehind
                        val mark = underline?.takeIf { it.isNotBlank() } ?: return@drawBehind
                        val start = title.indexOf(mark).takeIf { it >= 0 } ?: return@drawBehind
                        val end = start + mark.length
                        if (end > result.layoutInput.text.length) return@drawBehind
                        val first = result.getBoundingBox(start)
                        val last = result.getBoundingBox(end - 1)
                        // Ellipsised or wrapped onto a second line: the box no longer describes what
                        // is on screen, and a stroke drawn from it lands across unrelated words.
                        if (first.top != last.top) return@drawBehind
                        if (last.right <= first.left) return@drawBehind

                        val left = first.left - 1.dp.toPx()
                        val right = last.right + 1.dp.toPx()
                        val span = right - left
                        val baseY = last.bottom - 1.dp.toPx()

                        // A tapered stroke, not a rule and not a squiggle: thin where the pen lands,
                        // thickest through the middle, thin again as it lifts, with a slight rise to
                        // the right so it sits like something drawn rather than typeset. Filled
                        // rather than stroked, because an even stroke width is exactly what makes a
                        // line look mechanical.
                        val thickness = 3.4.dp.toPx()
                        val rise = 1.6.dp.toPx()
                        val path = Path()
                        path.moveTo(left, baseY)
                        // Upper edge, left to right.
                        path.cubicTo(
                            left + span * 0.28f,
                            baseY - thickness * 0.28f,
                            left + span * 0.72f,
                            baseY - rise - thickness * 0.28f,
                            right,
                            baseY - rise,
                        )
                        // Lower edge, back again, bulging to give the middle its weight.
                        path.cubicTo(
                            left + span * 0.72f,
                            baseY - rise + thickness,
                            left + span * 0.28f,
                            baseY + thickness,
                            left,
                            baseY,
                        )
                        path.close()
                        drawPath(path = path, color = accent.copy(alpha = 0.92f))
                    },
        )
        if (actionLabel != null && onAction != null) {
            Row(
                modifier =
                    Modifier
                        .clip(SonoraShapes.Container)
                        .clickable(onClick = onAction)
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = actionLabel,
                    style = typo().bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                )
                Spacer(Modifier.width(3.dp))
                Icon(
                    imageVector = SonoraIcons.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(11.dp),
                )
            }
        }
    }
}

/**
 * Square artwork card with the play control on the art itself.
 *
 * Tapping the art plays immediately. Everywhere else in the app, tapping artwork opens a detail
 * screen and playing is a second step; on Home, where the whole point is to start something, that
 * second step is the thing worth removing.
 *
 * [onOpen] is kept separate so the text below can still lead to the detail screen, which means the
 * card offers both without either being hidden behind a long press.
 */
@Composable
fun HomeTrackCard(
    title: String,
    subtitle: String,
    thumbnail: String?,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    onPlay: () -> Unit,
    onOpen: (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(SonoraShapes.Media)
                    .clickable(onClick = onPlay),
        ) {
            AsyncImage(
                model = thumbnail,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = rememberHolderPainter(),
                error = rememberHolderPainter(),
                modifier = Modifier.fillMaxSize(),
            )
            // Scrim only under the badge rather than over the whole card: the art is the reason the
            // card works as a target, so darkening all of it to float one control costs more than it
            // buys. This keeps contrast where the glyph actually sits.
            //
            // The badge doubles as the now playing indicator. A play triangle on the track that is
            // already playing is a lie about what tapping does, and the bars are the same signal the
            // list rows elsewhere in the app already use, so it needs no explaining.
            Box(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center,
            ) {
                Crossfade(targetState = isPlaying, label = "cardPlayingState") { playing ->
                    if (playing) {
                        NowPlayingIndicator(modifier = Modifier.size(22.dp))
                    } else {
                        Icon(
                            imageVector = SonoraIcons.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(
            text = title,
            style = typo().titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier =
                if (onOpen != null) {
                    Modifier.clickable(onClick = onOpen)
                } else {
                    Modifier
                },
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

/**
 * A row of exactly three cards, sized to the width they are given.
 *
 * Deliberately a fixed row rather than a carousel. A carousel leaves a card sliced off at the screen
 * edge to advertise that it scrolls, which means one visible item is always unreadable and the row
 * never lines up with anything above it. Three cards land on the gutter at both ends, and anything
 * past the third is reached through "See all" in the header, which is a clearer promise than a
 * half-visible card.
 */
@Composable
fun <T> HomeTripletRow(
    items: List<T>,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    if (items.isEmpty()) return
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = HomeGutter),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items.take(3).forEach { item ->
            Box(modifier = Modifier.weight(1f)) { content(item) }
        }
        // Keeps a short row left aligned on the same grid instead of stretching two cards across the
        // full width, which would make one section's cards a different size from every other one.
        repeat(3 - items.take(3).size) {
            Spacer(Modifier.weight(1f))
        }
    }
}
