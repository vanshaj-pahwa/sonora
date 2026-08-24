package app.vanshajpahwa.sonora.expect

import androidx.compose.runtime.Composable

/**
 * Whether the system is asking us not to animate.
 *
 * Covers two separate signals that mean the same thing to us: the accessibility preference for
 * reduced motion, and battery saver. A continuous animation is exactly what both of those exist to
 * suppress, so an ornamental one has to yield to either without the user having to find our setting.
 */
@Composable
expect fun isMotionReduced(): Boolean
