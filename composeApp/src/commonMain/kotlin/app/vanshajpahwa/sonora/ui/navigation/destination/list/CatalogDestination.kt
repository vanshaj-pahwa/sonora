package app.vanshajpahwa.sonora.ui.navigation.destination.list

import kotlinx.serialization.Serializable

/**
 * A browsable catalog page reached from Library.
 *
 * [type] is one of [ALBUMS] or [PODCASTS]. What each shows depends on whether the user is signed in:
 * their own recommendations when YouTube personalises the home feed, and new or popular content when
 * it does not.
 */
@Serializable
data class CatalogDestination(
    val type: String,
) {
    companion object {
        const val ALBUMS = "albums"
        const val PODCASTS = "podcasts"
    }
}
