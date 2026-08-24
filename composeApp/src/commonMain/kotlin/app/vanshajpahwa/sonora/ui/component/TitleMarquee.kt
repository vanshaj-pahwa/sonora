package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.MarqueeDefaults
import androidx.compose.foundation.basicMarquee
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/**
 * Whether scrolling titles are allowed, provided once at the app root from the user's setting.
 *
 * A composition local rather than a parameter threaded through every screen, because the ~100 places
 * that scroll a title are mostly leaf components several layers below anything that knows about
 * settings. Passing a boolean down all of those would change a lot of signatures to express one
 * global preference.
 *
 * Defaults to true so anything composed outside the provider keeps today's behaviour rather than
 * silently freezing.
 */
val LocalTitleScrollingEnabled = compositionLocalOf { true }

/**
 * Marquee that obeys [LocalTitleScrollingEnabled].
 *
 * Drop-in for `basicMarquee`, same parameters and same defaults, so call sites only change name.
 * When scrolling is off this returns the receiver untouched, which leaves the text as ordinary
 * clipped text: the first part stays readable and the rest is cut, which is what the request asked
 * for. Turning it off by other means, such as zero iterations, would still hand the text to the
 * marquee layout and can leave it parked mid scroll, which is the exact state that makes titles
 * unreadable in the first place.
 */
@Composable
fun Modifier.titleMarquee(
    iterations: Int = MarqueeDefaults.Iterations,
    animationMode: MarqueeAnimationMode = MarqueeAnimationMode.Immediately,
    repeatDelayMillis: Int = MarqueeDefaults.RepeatDelayMillis,
    initialDelayMillis: Int = if (animationMode == MarqueeAnimationMode.Immediately) repeatDelayMillis else 0,
    velocity: Dp = MarqueeDefaults.Velocity,
): Modifier =
    if (LocalTitleScrollingEnabled.current) {
        this.basicMarquee(
            iterations = iterations,
            animationMode = animationMode,
            repeatDelayMillis = repeatDelayMillis,
            initialDelayMillis = initialDelayMillis,
            velocity = velocity,
        )
    } else {
        this
    }
