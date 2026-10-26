package app.vanshajpahwa.sonora.ui.component

import androidx.compose.animation.Crossfade
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
    // the header plain, which is right for a fixed heading like Speed dial.
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
            // Only the track that is playing carries a badge. Every other card in the feed shows
            // artwork alone, and a play triangle on all of them said nothing tapping did not.
            if (isPlaying) {
                NowPlayingIndicator(
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .size(48.dp),
                )
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

/**
 * Speed dial heading: the signed in account above the title, and a chevron into the full list.
 *
 * Without an account the avatar and name are left out and the title stands alone.
 */
@Composable
fun SpeedDialHeader(
    title: String,
    accountName: String?,
    accountThumbnail: String?,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onOpen)
                .padding(horizontal = HomeGutter, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val name = accountName?.takeIf { it.isNotBlank() }
        if (name != null) {
            AsyncImage(
                model = accountThumbnail,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = rememberHolderPainter(),
                error = rememberHolderPainter(),
                modifier = Modifier.size(32.dp).clip(CircleShape),
            )
            Spacer(Modifier.width(10.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            if (name != null) {
                Text(
                    text = name.uppercase(),
                    style = typo().bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = title,
                style = typo().headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
            )
        }
        Icon(
            imageVector = SonoraIcons.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(16.dp),
        )
    }
}

/**
 * Pages of a 3x3 artwork grid, swiped sideways, with page dots under it.
 *
 * The last cell of the first page is [dice], so it is on screen without swiping. With fewer than
 * eight items it follows the last one instead, and a short row keeps its tiles at full grid size
 * instead of stretching them.
 */
@Composable
fun <T : Any> SpeedDialGrid(
    items: List<T>,
    modifier: Modifier = Modifier,
    dice: @Composable () -> Unit,
    tile: @Composable (T) -> Unit,
) {
    if (items.isEmpty()) return
    // null marks the dice cell. Past the first page only whole pages are kept: the pager takes the
    // height of its tallest page, so a one row last page sat above two rows of empty space.
    val cells: List<T?> = items.take(8) + listOf(null) + items.drop(8)
    val pages = cells.chunked(9).let { all -> listOf(all.first()) + all.drop(1).filter { it.size == 9 } }
    val pagerState = rememberPagerState(pageCount = { pages.size })
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = HomeGutter),
            pageSpacing = 12.dp,
            verticalAlignment = Alignment.Top,
        ) { page ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                pages[page].chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        row.forEach { item ->
                            Box(modifier = Modifier.weight(1f)) {
                                if (item == null) dice() else tile(item)
                            }
                        }
                        repeat(3 - row.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        if (pages.size > 1) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
            ) {
                repeat(pages.size) { index ->
                    Box(
                        modifier =
                            Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == pagerState.currentPage) {
                                        MaterialTheme.colorScheme.onBackground
                                    } else {
                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                                    },
                                ),
                    )
                }
            }
        }
    }
}

/**
 * The dice cell of Speed dial: a die over a gradient, labelled like the tiles around it. Tapping it
 * plays at random.
 */
@Composable
fun SpeedDialDiceTile(
    label: String,
    onClick: () -> Unit,
) {
    val face = MaterialTheme.colorScheme.onPrimaryContainer
    val pip = MaterialTheme.colorScheme.primaryContainer
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(SonoraShapes.Media)
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.tertiaryContainer,
                        ),
                    ),
                ).clickable(onClick = onClick),
    ) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .offset(y = (-8).dp)
                    .fillMaxSize(0.42f)
                    .rotate(-14f)
                    .shadow(10.dp, RoundedCornerShape(22))
                    .background(face, RoundedCornerShape(22))
                    .drawBehind {
                        val radius = size.minDimension * 0.1f
                        listOf(0.27f, 0.5f, 0.73f).forEach { f ->
                            drawCircle(color = pip, radius = radius, center = Offset(size.width * f, size.height * f))
                        }
                    },
        )
        Text(
            text = label,
            style = typo().bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = Color.White,
            maxLines = 1,
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 8.dp, vertical = 7.dp),
        )
    }
}

@Composable
fun SpeedDialTile(
    title: String,
    thumbnail: String?,
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(SonoraShapes.Media)
                .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = thumbnail,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = rememberHolderPainter(),
            error = rememberHolderPainter(),
            modifier = Modifier.fillMaxSize(),
        )
        // Bottom half only, so the title stays legible on bright art without dimming the whole tile.
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            0.45f to Color.Transparent,
                            1f to Color.Black.copy(alpha = 0.75f),
                        ),
                    ),
        )
        if (isPlaying) {
            NowPlayingIndicator(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .size(40.dp),
            )
        }
        Text(
            text = title,
            style = typo().bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 8.dp, vertical = 7.dp),
        )
    }
}
