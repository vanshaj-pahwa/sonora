package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.ui.theme.seed
import org.jetbrains.compose.resources.painterResource
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.monochrome
import kotlin.math.abs

@Composable
fun PlaylistThumbnail(
    title: String,
    modifier: Modifier = Modifier,
) {
    val gradientColors = generateGradientFromTitle(title)
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors
                )
            )
    ) {

        // Title in bottom right
        Text(
            text = title,
            color = Color.White,
            
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}

// Generate deterministic gradient colors from title
private fun generateGradientFromTitle(title: String): List<Color> {
    val hash = title.hashCode()

    // Extract RGB components from hash
    val hue1 = ((hash and 0xFF) / 255f) * 360f
    val hue2 = (((hash shr 8) and 0xFF) / 255f) * 360f

    // Value was 0.9 / 0.85, which put white labels between 1.73:1 and 3.69:1 against these grounds,
    // all failing WCAG AA. Worst case was a title hashing into the yellow-green band: sampled
    // #73DD44 at 1.73:1.
    //
    // 0.48 is the largest value at which white clears 4.5:1 on EVERY hue, worst case 4.53:1, so it
    // fixes cards that do not exist yet rather than just the ones measured. 0.60 would only reach
    // 3.0:1, and that AA-large allowance needs 18sp+ text while these labels are titleSmall at 13sp.
    val color1 = hsvToColor(hue1, 0.7f, 0.48f)
    val color2 = hsvToColor(hue2, 0.7f, 0.45f)

    return listOf(color1, color2)
}

/**
 * Deterministic placeholder gradient colors for a playlist title — matches the
 * [PlaylistThumbnail] / [painterPlaylistThumbnail] placeholder, so a screen can tint
 * its background to match a local playlist that has no real artwork.
 */
fun playlistTitleGradient(title: String): List<Color> = generateGradientFromTitle(title)

/**
 * The gradient for a browse category tile, chosen by the tile's position rather than its title.
 *
 * Hashing the title, which is what [playlistTitleGradient] does, cannot keep categories apart. Two
 * unrelated titles land wherever their hashes fall: measured across the real Moods and Genres
 * sections, the closest pair sat 1 degree apart and six categories crowded into a 23 degree band,
 * all reading as the same purple. Quantising those hashes into buckets only converts near misses
 * into exact collisions, 7 of 18 in the same measurement.
 *
 * Stepping by the golden angle instead spreads any number of tiles about as evenly as a sequence
 * can: consecutive cards land ~137 degrees apart, and the sequence revisits a hue only after a full
 * turn. Adjacent tiles, the ones actually compared at a glance, are always maximally different.
 *
 * The trade-off, stated plainly: colour now follows position, so a category moves hue if YouTube
 * reorders a section. That is the right way round for a browse grid, where the job is telling this
 * tile from the one beside it, not recognising a colour learned last week.
 *
 * Saturation and value are the ones [generateGradientFromTitle] settled on for WCAG AA against
 * white labels, and only the hue is chosen here, so contrast is unchanged.
 */
private const val GOLDEN_ANGLE_DEGREES = 137.508f
private const val ANALOGOUS_OFFSET_DEGREES = 38f

fun categoryGradient(index: Int): List<Color> {
    val hue1 = ((index * GOLDEN_ANGLE_DEGREES) % 360f + 360f) % 360f
    // The second stop stays in the same family. Two unrelated hues blended across a tile is what
    // muddied the originals toward purple regardless of where either one started.
    val hue2 = (hue1 + ANALOGOUS_OFFSET_DEGREES) % 360f
    return listOf(hsvToColor(hue1, 0.7f, 0.48f), hsvToColor(hue2, 0.7f, 0.45f))
}

// HSV to RGB conversion
private fun hsvToColor(hue: Float, saturation: Float, value: Float): Color {
    val h = hue / 60f
    val c = value * saturation
    val x = c * (1 - abs((h % 2) - 1))
    val m = value - c

    val (r, g, b) = when (h.toInt()) {
        0 -> Triple(c, x, 0f)
        1 -> Triple(x, c, 0f)
        2 -> Triple(0f, c, x)
        3 -> Triple(0f, x, c)
        4 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color(
        red = (r + m),
        green = (g + m),
        blue = (b + m),
        alpha = 1f
    )
}

private class PlaylistThumbnailPainter(
    private val size: Size,
    private val title: String,
    private val textLayoutResult: TextLayoutResult,
    private val iconPainter: Painter
): Painter() {
    override val intrinsicSize: Size
        get() = size

    override fun DrawScope.onDraw() {
        val colors = generateGradientFromTitle(title)
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = colors
            ),
            size = size,
            cornerRadius = CornerRadius(16f, 16f),
        )
        drawText(textLayoutResult, topLeft = calculateTopLeft(
            alignment = Alignment.BottomStart,
            textSize = IntSize(
                width = textLayoutResult.size.width,
                height = textLayoutResult.size.height,
            ),
            containerSize = IntSize(
                size.width.toInt(),
                size.height.toInt(),
            ),
        ))
        Logger.d("PlaylistThumbnailPainter", "Drawing icon $title")
        Logger.d("PlaylistThumbnailPainter", "Colors: ${colors.map { it.toArgb() }}")
        val centerX = size.width * 0.9f
        val centerY = size.height * 0.1f
        // No white disc behind the mark: it sits straight on the tile as a watermark.
        translate(
            left = centerX - size.width * 0.15f / 2,
            top = centerY - size.height * 0.15f / 2,
        ) {
            with(iconPainter) {
                draw(
                    size * 0.15f,
                    colorFilter = ColorFilter.tint(seed),
                )
            }
        }
    }
}

private fun calculateTopLeft(
    alignment: Alignment,
    textSize: IntSize,
    containerSize: IntSize,
): Offset {
    val alignmentOffset =
        alignment.align(
            size = textSize.let { (width, height) ->
                IntSize(
                    width = width,
                    height = height,
                )
            },
            space = containerSize,
            layoutDirection = LayoutDirection.Ltr,
        )

    return Offset(
        x = alignmentOffset.x.toFloat() + 16f,
        y = alignmentOffset.y.toFloat() - 16f,
    )
}

@Composable
fun painterPlaylistThumbnail(
    title: String,
    style: TextStyle,
    sizeDp: Pair<Dp, Dp>,
): Painter {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult =
        remember(title, style, sizeDp) {
            textMeasurer.measure(
                title,
                style.copy(
                    color = Color.White,
                    textAlign = TextAlign.Start
                ),
                maxLines = 2,
                softWrap = true,
                overflow = TextOverflow.Ellipsis,
                layoutDirection = LayoutDirection.Ltr,
                constraints =
                    Constraints(
                        maxWidth = with(density) { (sizeDp.first - 32.dp).toPx().toInt() },
                        maxHeight = with(density) { (sizeDp.second - 32.dp).toPx().toInt() },
                    ),
            )
        }
    val painterRes = painterResource(Res.drawable.monochrome)
    return PlaylistThumbnailPainter(
        size = Size(
            width = with(density) { sizeDp.first.toPx() },
            height = with(density) { sizeDp.second.toPx() },
        ),
        title = title,
        textLayoutResult = textLayoutResult,
        iconPainter = painterRes
    )
}