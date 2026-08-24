package app.vanshajpahwa.sonora.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * The three corner radii Sonora uses, and nothing else.
 *
 * See docs/design-identity.md. Before these existed the app had twelve distinct radii in play, which
 * is the same as having none: a shape can only carry meaning if the set is small enough for the eye
 * to learn it. Each of these three says something different about what it wraps.
 *
 * If a new surface does not fit one of the three, the surface is wrong, not the set.
 */
object SonoraShapes {
    /**
     * Interactive and primary. Buttons, chips, the nav pill, play controls, selection states.
     *
     * This is the "capsule is the verb" signature. A fully rounded shape means the thing does
     * something, so nothing decorative may take it and nothing primary and interactive may take
     * anything else.
     */
    val Capsule = RoundedCornerShape(percent = 50)

    /** Cards, sheets, grouped surfaces. Anything that holds content rather than being content. */
    val Container = RoundedCornerShape(16.dp)

    /**
     * Artwork and thumbnails only.
     *
     * Deliberately tighter than [Container]. Album art is a photograph, and rounding a photograph as
     * heavily as the card it sits in makes the art look like part of the furniture instead of the
     * thing the furniture exists to show.
     */
    val Media = RoundedCornerShape(8.dp)

    /**
     * Bottom sheets, which round their top corners only.
     *
     * Same radius as [Container], because a sheet is a container. It gets its own value rather than
     * a copy of the number so that changing the container radius moves the sheets with it.
     */
    val SheetTop = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
}
