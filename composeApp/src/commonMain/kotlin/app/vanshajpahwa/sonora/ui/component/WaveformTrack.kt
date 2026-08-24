package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Seek track drawn as a row of bars instead of a line.
 *
 * The bar heights are **decorative, not measured amplitude**. Nothing in the app has decoded samples
 * for a track before it plays, so a real waveform for a whole song does not exist to draw. Heights
 * come from a deterministic sequence seeded by [seed], so they are stable for a given track and
 * differ between tracks, and they say nothing about how loud the music is.
 *
 * If a genuine waveform is ever wanted, the cheap route is `CrossfadeFilterAudioProcessor`, which
 * already receives every PCM frame in `queueInput`. That can only ever describe audio already
 * played, so the design would have to change with it.
 *
 * This is only a renderer. Seeking is still handled by the Material `Slider` it is plugged into.
 */
@Composable
fun WaveformTrack(
    progress: Float,
    seed: Long,
    modifier: Modifier = Modifier,
    playedColor: Color = Color.White,
    unplayedColor: Color = Color.White.copy(alpha = 0.26f),
    barCount: Int = 38,
    barWidth: Dp = 2.5.dp,
    minBarFraction: Float = 0.18f,
) {
    val heights =
        remember(seed, barCount) {
            // splitmix64. A plain LCG was tried first and looked wrong on device: taking a few bits
            // out of it clusters hard, so every bar came out near the middle of the range and the
            // result read as a comb rather than a waveform. This avalanches properly, and being
            // hand-rolled it stays identical across platforms.
            var state = if (seed == 0L) 0x9E3779B97F4A7C15uL.toLong() else seed
            fun nextUnit(): Float {
                state += -0x61c8864680b583ebL
                var z = state
                z = (z xor (z ushr 30)) * -0x40a7b892e31b1a47L
                z = (z xor (z ushr 27)) * -0x6b2fb644ecceee15L
                z = z xor (z ushr 31)
                return ((z ushr 40).toFloat() / (1 shl 24).toFloat()).coerceIn(0f, 1f)
            }
            FloatArray(barCount) { index ->
                // Two octaves: a slow swell so the shape has structure, plus per-bar jitter so
                // neighbours differ. Pure noise looks like static; pure swell looks like a hill.
                val swell = nextUnit() * 0.55f + nextUnit() * 0.45f
                val jitter = nextUnit()
                val unit = (swell * 0.6f + jitter * 0.4f).coerceIn(0f, 1f)
                // Taper the ends so the row does not butt hard against the timestamps.
                val edge = (index + 0.5f) / barCount
                val taper = if (edge < 0.07f || edge > 0.93f) 0.65f else 1f
                (minBarFraction + unit * (1f - minBarFraction)) * taper
            }
        }

    Canvas(modifier = modifier) {
        if (size.width <= 0f || barCount <= 0) return@Canvas
        val strokePx = barWidth.toPx()
        val step = size.width / barCount
        val centerY = size.height / 2f
        val playedBars = (progress.coerceIn(0f, 1f) * barCount).roundToInt()

        heights.forEachIndexed { index, fraction ->
            val x = step * index + step / 2f
            val half = max(strokePx / 2f, (size.height * fraction) / 2f)
            drawLine(
                color = if (index < playedBars) playedColor else unplayedColor,
                start = Offset(x, centerY - half),
                end = Offset(x, centerY + half),
                strokeWidth = strokePx,
                cap = StrokeCap.Round,
            )
        }
    }
}
