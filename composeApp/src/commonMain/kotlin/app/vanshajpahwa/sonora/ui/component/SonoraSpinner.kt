package app.vanshajpahwa.sonora.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The app's loading spinner.
 *
 * A sweep-gradient arc rather than Material's flat one: the stroke runs from full accent at its
 * leading edge to transparent at its tail, so the ring reads as a comet rather than a rotating
 * dash. That is the difference between "an Android app is loading" and something that looks like
 * it belongs to this app, and it costs one Canvas.
 *
 * Below [GRADIENT_MIN_SIZE] it falls back to a plain arc. A sweep needs enough arc length for the
 * fade to be legible; at 18dp — the size the mini player uses — the gradient compresses into what
 * looks like a rendering artifact, so the small case gets the flat accent stroke instead. Same
 * component, same colour, one honest degradation.
 */
@Composable
fun SonoraSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    strokeWidth: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    val transition = rememberInfiniteTransition(label = "spinner")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 1100, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "spinnerAngle",
    )

    val useGradient = size >= GRADIENT_MIN_SIZE
    // A faint full ring behind the arc. Without it the arc appears to travel through empty space
    // and the spinner reads as smaller than it is.
    val track = color.copy(alpha = 0.12f)

    Canvas(modifier = modifier.size(size)) {
        val stroke = strokeWidth.toPx()
        val inset = stroke / 2f
        val diameter = kotlin.math.min(this.size.width, this.size.height) - stroke
        val topLeft = androidx.compose.ui.geometry.Offset(inset, inset)
        val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)

        drawArc(
            color = track,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )

        if (useGradient) {
            // The sweep starts and ends at the same point, so the transparent tail meets the
            // opaque head. Rotating the whole brush with the arc keeps that seam under the head
            // where it cannot be seen.
            val brush =
                Brush.sweepGradient(
                    0f to color.copy(alpha = 0f),
                    0.75f to color,
                    1f to color,
                    center = center,
                )
            rotate(degrees = angle) {
                drawArc(
                    brush = brush,
                    startAngle = 0f,
                    sweepAngle = 300f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            }
        } else {
            drawArc(
                color = color,
                startAngle = angle,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
    }
}

/** Below this the sweep has too little arc to read as a gradient rather than a glitch. */
private val GRADIENT_MIN_SIZE = 24.dp
