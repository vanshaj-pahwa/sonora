package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.Canvas
import kotlinx.coroutines.isActive
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * The Now Playing artwork, in whichever shape the user picked.
 *
 * [useRing] true gives [CircularSeekArtwork]: circular art with the progress arc around it, and that
 * arc is also the seek control. False gives the earlier plain square, where the bar under the title
 * is the only way to seek.
 *
 * Both branches take the same square box and the same [content], so the artwork keeps its position
 * and the layout below it does not move when the setting changes. Only the frame differs.
 *
 * [isVideo] overrides both. A video is 16:9, and neither shape can hold one honestly: a circle crops
 * the sides off the picture, and a square frame letterboxes it into a band with dead space above and
 * below. Video therefore always gets a plain rectangle that the content sizes itself, whatever the
 * user picked for songs.
 */
@Composable
fun PlayerArtworkFrame(
    useRing: Boolean,
    progress: Float,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.White,
    enabled: Boolean = true,
    isVideo: Boolean = false,
    /** Spin the disc. Ignored for the square shape, which has no record to turn. */
    spinning: Boolean = false,
    onSeek: (Float) -> Unit = {},
    onSeekFinished: (Float) -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    if (isVideo) {
        // No aspectRatio here on purpose: the video surface inside already declares 16:9, and forcing
        // a ratio on the frame as well would box it a second time.
        Box(modifier = modifier, content = content)
    } else if (useRing) {
        CircularSeekArtwork(
            progress = progress,
            modifier = modifier,
            activeColor = activeColor,
            enabled = enabled,
            spinning = spinning,
            onSeek = onSeek,
            onSeekFinished = onSeekFinished,
            content = content,
        )
    } else {
        Box(
            modifier = modifier.aspectRatio(1f),
            content = content,
        )
    }
}

/**
 * How far the artwork sits inside its frame, in both shapes.
 *
 * Shared so the ring's disc and the square can never drift to different sizes again.
 */
internal val ARTWORK_SQUARE_INSET = 18.dp

/** Gap at the bottom of the ring, in degrees, so the arc reads as a dial rather than a full circle. */
private const val GAP_DEGREES = 80f
private const val SWEEP_DEGREES = 360f - GAP_DEGREES
private const val START_DEGREES = 90f + GAP_DEGREES / 2f

/**
 * Circular artwork with the seek position drawn as an arc around it.
 *
 * The arc is both the indicator and the control: dragging anywhere on the ring seeks, and a tap on
 * the ring jumps. [content] is the artwork, clipped to a circle and inset inside the ring.
 *
 * Two consequences of this shape, stated in code because they are real and were argued over:
 *  - a circle inscribed in the same width shows about 21% less artwork than a square, before the
 *    ring inset takes more;
 *  - angular dragging is less precise than linear dragging, so [onSeekFinished] only fires on
 *    release and the caller keeps the value it already has until then.
 */
@Composable
fun CircularSeekArtwork(
    progress: Float,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.White,
    trackColor: Color = Color.White.copy(alpha = 0.22f),
    ringWidth: Dp = 3.dp,
    knobRadius: Dp = 7.dp,
    ringInset: Dp = ARTWORK_SQUARE_INSET,
    /** How far either side of the ring still counts as grabbing it. */
    ringTouchWidth: Dp = 26.dp,
    enabled: Boolean = true,
    /** Spin the artwork like a record. Only honoured while something is actually playing. */
    spinning: Boolean = false,
    onSeek: (Float) -> Unit = {},
    onSeekFinished: (Float) -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    var dragFraction by remember { mutableStateOf<Float?>(null) }
    val shown = (dragFraction ?: progress).coerceIn(0f, 1f)

    /** Touch point -> fraction along the arc, or null when the touch sits inside the bottom gap. */
    fun fractionFor(
        point: Offset,
        size: Size,
    ): Float? {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val deg = (atan2(point.y - cy, point.x - cx) * 180f / PI.toFloat() + 360f) % 360f
        val fromStart = (deg - START_DEGREES + 360f) % 360f
        if (fromStart > SWEEP_DEGREES) return null
        return fromStart / SWEEP_DEGREES
    }

    /** True when the touch sits within [band] of the ring itself, not out in the artwork. */
    fun isOnRing(
        point: Offset,
        size: Size,
        band: Float,
    ): Boolean {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = size.width / 2f
        val dist = hypot(point.x - cx, point.y - cy)
        return dist >= radius - band && dist <= radius + band
    }

    Box(
        modifier = modifier.aspectRatio(1f),
    ) {
        Canvas(
            modifier =
                Modifier
                    .fillMaxSize()
                    .then(
                        if (!enabled) {
                            Modifier
                        } else {
                            Modifier.pointerInput(Unit) {
                                // Hand-rolled rather than detectDragGestures, which claims every
                                // drag that begins anywhere on this Canvas. The Canvas covers the
                                // whole artwork, so that swallowed vertical scrolls over the art and
                                // dragged the playhead instead of scrolling the screen.
                                //
                                // Here the first touch is only consumed when it lands on the ring
                                // band itself. Anywhere else, including the artwork in the middle,
                                // the event is left untouched and the scroll container gets it.
                                awaitEachGesture {
                                    val down = awaitFirstDown(requireUnconsumed = false)
                                    val canvasSize = size.toSize()
                                    val startFraction =
                                        fractionFor(down.position, canvasSize)
                                            ?.takeIf { isOnRing(down.position, canvasSize, ringTouchWidth.toPx()) }
                                            ?: return@awaitEachGesture

                                    down.consume()
                                    dragFraction = startFraction
                                    onSeek(startFraction)

                                    var last = startFraction
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                        if (!change.pressed) break
                                        // Once the ring owns the gesture, follow the finger by angle
                                        // even if it strays off the band, so a sloppy arc still seeks.
                                        fractionFor(change.position, canvasSize)?.let {
                                            last = it
                                            dragFraction = it
                                            onSeek(it)
                                        }
                                        change.consume()
                                    }
                                    onSeekFinished(last)
                                    dragFraction = null
                                }
                            }
                        },
                    ),
        ) {
            val stroke = ringWidth.toPx()
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(inset, inset)

            drawArc(
                color = trackColor,
                startAngle = START_DEGREES,
                sweepAngle = SWEEP_DEGREES,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = activeColor,
                startAngle = START_DEGREES,
                sweepAngle = SWEEP_DEGREES * shown,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )

            // Knob sits on the arc at the current position.
            val angle = (START_DEGREES + SWEEP_DEGREES * shown) * PI.toFloat() / 180f
            val radius = (size.width - stroke) / 2f
            drawCircle(
                color = activeColor,
                radius = knobRadius.toPx(),
                center =
                    Offset(
                        x = size.width / 2f + cos(angle) * radius,
                        y = size.height / 2f + sin(angle) * radius,
                    ),
            )
        }

        // Only the artwork turns. The ring around it is the seek bar and its knob marks position,
        // so rotating that would spin the control away from meaning anything. Keeping the arc fixed
        // is what lets the disc move without costing the gesture.
        //
        // The angle accumulates rather than resetting: pausing cancels the effect and leaves the
        // value where it stopped, and resuming animates on from there, so a pause reads as a record
        // slowing to a halt rather than the art snapping back to upright.
        val angle = remember { Animatable(0f) }
        val lifecycleOwner = LocalLifecycleOwner.current
        LaunchedEffect(spinning, lifecycleOwner) {
            if (!spinning) return@LaunchedEffect
            // RESUMED, not STARTED: with the screen off or the app behind another window this
            // suspends, so a phone in a pocket during a workout is not animating a layer it cannot
            // show. The angle survives, so it picks up where it left off when you look again.
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (isActive) {
                    angle.animateTo(
                        targetValue = angle.value + 360f,
                        animationSpec = tween(ROTATION_PERIOD_MS, easing = LinearEasing),
                    )
                }
            }
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(ringInset)
                    // Read inside the graphicsLayer lambda so the angle is sampled in the draw
                    // phase. Setting rotationZ from a recomposing value instead would rebuild this
                    // subtree every frame and re-request the artwork; this way one layer is
                    // transformed and nothing above it recomposes at all.
                    .graphicsLayer { rotationZ = angle.value % 360f }
                    .clip(CircleShape),
            content = content,
        )
    }
}

/** One full turn. Slow enough to read as a record rather than a spinner. */
private const val ROTATION_PERIOD_MS = 24_000
