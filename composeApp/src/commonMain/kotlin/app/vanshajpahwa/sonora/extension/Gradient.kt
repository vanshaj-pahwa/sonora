package app.vanshajpahwa.sonora.extension

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

// Angled gradient backgrounds and the offsets that describe their direction.
// Split out of the former UIExt.kt, which had grown into an unrelated grab bag.

fun Modifier.angledGradientBackground(
    colors: List<Color>,
    degrees: Float,
) = this.then(
    if (colors.size < 2) {
        Modifier
    } else {
        Modifier.drawBehind {
            /*
            Have to compute length of gradient vector so that it lies within
            the visible rectangle.
            --------------------------------------------
            | length of gradient ^  /                  |
            |             --->  /  /                   |
            |                  /  / <- rotation angle  |
            |                 /  o --------------------|  y
            |                /  /                      |
            |               /  /                       |
            |              v  /                        |
            --------------------------------------------
                                 x

                       diagonal angle = atan2(y, x)
                     (it's hard to draw the diagonal)

            Simply rotating the diagonal around the centre of the rectangle
            will lead to points outside the rectangle area. Further, just
            truncating the coordinate to be at the nearest edge of the
            rectangle to the rotated point will distort the angle.
            Let α be the desired gradient angle (in radians) and γ be the
            angle of the diagonal of the rectangle.
            The correct for the length of the gradient is given by:
            x/|cos(α)|  if -γ <= α <= γ,   or   π - γ <= α <= π + γ
            y/|sin(α)|  if  γ <= α <= π - γ, or π + γ <= α <= 2π - γ
            where γ ∈ (0, π/2) is the angle that the diagonal makes with
            the base of the rectangle.

             */

            val (x, y) = size
            val gamma = atan2(y, x)

            if (gamma == 0f || gamma == (PI / 2).toFloat()) {
                // degenerate rectangle
                return@drawBehind
            }

            val degreesNormalised = (degrees % 360).let { if (it < 0) it + 360 else it }

            val alpha = (degreesNormalised * PI / 180).toFloat()

            val gradientLength =
                when (alpha) {
                    // ray from centre cuts the right edge of the rectangle
                    in 0f..gamma, in (2 * PI - gamma)..2 * PI -> {
                        x / cos(alpha)
                    }

                    // ray from centre cuts the top edge of the rectangle
                    in gamma..(PI - gamma).toFloat() -> {
                        y / sin(alpha)
                    }

                    // ray from centre cuts the left edge of the rectangle
                    in (PI - gamma)..(PI + gamma) -> {
                        x / -cos(alpha)
                    }

                    // ray from centre cuts the bottom edge of the rectangle
                    in (PI + gamma)..(2 * PI - gamma) -> {
                        y / -sin(alpha)
                    }

                    // default case (which shouldn't really happen)
                    else -> {
                        hypot(x, y)
                    }
                }

            val centerOffsetX = cos(alpha) * gradientLength / 2
            val centerOffsetY = sin(alpha) * gradientLength / 2

            drawRect(
                brush =
                    Brush.linearGradient(
                        colors = colors,
                        // negative here so that 0 degrees is left -> right
                        start = Offset(center.x - centerOffsetX, center.y - centerOffsetY),
                        end = Offset(center.x + centerOffsetX, center.y + centerOffsetY),
                    ),
                size = size,
            )
        }
    },
)

// Angle Gradient Background without size
fun GradientOffset(angle: GradientAngle): GradientOffset =
    when (angle) {
        GradientAngle.CW45 -> {
            GradientOffset(
                start = Offset.Zero,
                end = Offset.Infinite,
            )
        }

        GradientAngle.CW90 -> {
            GradientOffset(
                start = Offset.Zero,
                end = Offset(0f, Float.POSITIVE_INFINITY),
            )
        }

        GradientAngle.CW135 -> {
            GradientOffset(
                start = Offset(Float.POSITIVE_INFINITY, 0f),
                end = Offset(0f, Float.POSITIVE_INFINITY),
            )
        }

        GradientAngle.CW180 -> {
            GradientOffset(
                start = Offset(Float.POSITIVE_INFINITY, 0f),
                end = Offset.Zero,
            )
        }

        GradientAngle.CW225 -> {
            GradientOffset(
                start = Offset.Infinite,
                end = Offset.Zero,
            )
        }

        GradientAngle.CW270 -> {
            GradientOffset(
                start = Offset(0f, Float.POSITIVE_INFINITY),
                end = Offset.Zero,
            )
        }

        GradientAngle.CW315 -> {
            GradientOffset(
                start = Offset(0f, Float.POSITIVE_INFINITY),
                end = Offset(Float.POSITIVE_INFINITY, 0f),
            )
        }

        else -> {
            GradientOffset(
                start = Offset.Zero,
                end = Offset(Float.POSITIVE_INFINITY, 0f),
            )
        }
    }

/**
 * Offset for [Brush.linearGradient] to rotate gradient depending on [start] and [end] offsets.
 */
data class GradientOffset(
    val start: Offset,
    val end: Offset,
)

enum class GradientAngle {
    CW0,
    CW45,
    CW90,
    CW135,
    CW180,
    CW225,
    CW270,
    CW315,
}
