package app.vanshajpahwa.sonora.extension

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kmpalette.palette.graphics.Palette
import kotlin.math.abs
import kotlin.math.min

// Colour derived from artwork: palette picking, scrims and conversions.
// Split out of the former UIExt.kt, which had grown into an unrelated grab bag.

fun Palette?.getColorFromPalette(): Color {
    val p = this ?: return Color.Black
    val defaultColor = 0x000000
    var startColor = p.getDarkVibrantColor(defaultColor)
    if (startColor == defaultColor) {
        startColor = p.getDarkMutedColor(defaultColor)
        if (startColor == defaultColor) {
            startColor = p.getVibrantColor(defaultColor)
            if (startColor == defaultColor) {
                startColor =
                    p.getMutedColor(defaultColor)
                if (startColor == defaultColor) {
                    startColor =
                        p.getLightVibrantColor(
                            defaultColor,
                        )
                    if (startColor == defaultColor) {
                        startColor =
                            p.getLightMutedColor(
                                defaultColor,
                            )
                    }
                }
            }
        }
    }
    return Color(startColor)
}

/**
 * Apple-Music-style immersive page background derived from the artwork.
 * Uses the DOMINANT swatch (overall tone of the image, by pixel area) instead of a vivid Muted
 * swatch — so a light image yields a light-ish tone rather than a small saturated patch (e.g. a
 * bright portrait no longer turns brick-red from the hair/skin). Then darkens adaptively: the
 * lighter the source, the harder it is pulled toward black, so white text stays readable on any
 * artwork.
 */
fun Palette?.toImmersiveBackground(): Color {
    val p = this ?: return Color.Black
    val rgb =
        p.getDominantColor(0).takeIf { it != 0 }
            ?: p.getMutedColor(0).takeIf { it != 0 }
            ?: p.getVibrantColor(0).takeIf { it != 0 }
            ?: return Color.Black
    val base = Color(rgb)
    // Perceived luminance (0 dark .. 1 light) of the source swatch.
    val luminance = 0.299f * base.red + 0.587f * base.green + 0.114f * base.blue
    // Darken more for lighter artwork so the page stays dark enough for white text.
    val darkenFactor = 0.35f + 0.45f * luminance
    return androidx.compose.ui.graphics.lerp(base, Color.Black, darkenFactor)
}

/**
 * Vertical scrim from [from] to [to] that fades without showing an edge.
 *
 * Two details are what make this read as smooth where a plain
 * `verticalGradient(Transparent, bg)` does not:
 *  - **smoothstep easing**, so the curve is flat at BOTH ends. A linear ramp has a corner where it
 *    leaves 0, and the eye tracks the derivative of brightness — that corner IS the visible edge.
 *  - **the colours are interpolated here, not by Skia**, so a caller can pass
 *    `color.copy(alpha = 0f)` instead of [Color.Transparent]. Transparent is *black* with alpha 0,
 *    and Skia interpolates stops un-premultiplied, so RGB gets dragged toward black alongside the
 *    alpha and the middle of the scrim turns into a dirty grey band.
 *
 * [steps] stops (rather than the 2 the curve needs) keep the piecewise-linear approximation of the
 * S-curve below the point where 8-bit banding becomes visible on a dark background.
 *
 * [startFraction]/[endFraction] confine the ramp to part of the box (the rest is held at [from] /
 * [to] by [TileMode.Clamp]); [startY]/[endY] do the same in pixels when the caller knows them.
 */
fun smoothScrimBrush(
    from: Color,
    to: Color,
    startFraction: Float = 0f,
    endFraction: Float = 1f,
    startY: Float = 0f,
    endY: Float = Float.POSITIVE_INFINITY,
    steps: Int = 24,
): Brush =
    Brush.verticalGradient(
        colorStops =
            Array(steps + 1) { i ->
                val t = i / steps.toFloat()
                val position = startFraction + (endFraction - startFraction) * t
                position to androidx.compose.ui.graphics.lerp(from, to, t * t * (3f - 2f * t))
            },
        startY = startY,
        endY = endY,
    )

/**
 * The common case of [smoothScrimBrush]: a bottom scrim that melts artwork into the page
 * background ([toImmersiveBackground]) by ramping [color] from invisible to opaque.
 */
fun artworkScrimBrush(
    color: Color,
    steps: Int = 24,
): Brush = smoothScrimBrush(from = color.copy(alpha = 0f), to = color, steps = steps)

fun Color.rgbFactor(factor: Float): Color {
    val r = min(red * factor, 255f)
    val g = min(green * factor, 255f)
    val b = min(blue * factor, 255f)
    return Color(r, g, b, alpha)
}

/**
 * Parse a hex color string ("ad1e5d", "#ad1e5d", or 8-digit "aarrggbb") into a Compose [Color].
 * Returns null on malformed input.
 */
fun String.hexToColorOrNull(): Color? =
    runCatching {
        val clean = removePrefix("#")
        val argb =
            when (clean.length) {
                6 -> 0xFF000000L or clean.toLong(16)
                8 -> clean.toLong(16)
                else -> return null
            }
        Color(argb)
    }.getOrNull()

/** Converts HSV (hue 0-360, saturation 0-1, value 0-1) to Compose Color. */
fun hsvToColor(
    hue: Float,
    saturation: Float,
    value: Float,
): Color {
    val c = value * saturation
    val x = c * (1 - abs((hue / 60f) % 2f - 1f))
    val m = value - c
    val (r, g, b) =
        when {
            hue < 60f -> Triple(c, x, 0f)
            hue < 120f -> Triple(x, c, 0f)
            hue < 180f -> Triple(0f, c, x)
            hue < 240f -> Triple(0f, x, c)
            hue < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
    return Color(
        red = (r + m).coerceIn(0f, 1f),
        green = (g + m).coerceIn(0f, 1f),
        blue = (b + m).coerceIn(0f, 1f),
        alpha = 1f,
    )
}
