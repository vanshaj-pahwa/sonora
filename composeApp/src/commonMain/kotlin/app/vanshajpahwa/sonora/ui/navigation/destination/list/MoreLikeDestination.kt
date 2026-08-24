package app.vanshajpahwa.sonora.ui.navigation.destination.list

import kotlinx.serialization.Serializable

/**
 * The full list behind a Home "More like X" shelf.
 *
 * Carries the seed rather than the tracks. Navigation arguments end up in a saved state bundle, and
 * a whole recommendation list would be both far too large for that and stale by the time it is read;
 * the seed is two short strings and the screen re-runs the same query the shelf did.
 */
@Serializable
data class MoreLikeDestination(
    val seedVideoId: String,
    val seedName: String,
    /** Set for the artist shelf, so the full list re-runs that artist's radio rather than a song's. */
    val seedChannelId: String? = null,
)
