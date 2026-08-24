package app.vanshajpahwa.kotlinytmusicscraper.pages

import app.vanshajpahwa.kotlinytmusicscraper.models.Album
import app.vanshajpahwa.kotlinytmusicscraper.models.Artist
import app.vanshajpahwa.kotlinytmusicscraper.models.MusicResponsiveListItemRenderer
import app.vanshajpahwa.kotlinytmusicscraper.models.SongItem
import app.vanshajpahwa.kotlinytmusicscraper.models.YTItem
import app.vanshajpahwa.kotlinytmusicscraper.models.oddElements
import app.vanshajpahwa.kotlinytmusicscraper.models.splitBySeparator
import app.vanshajpahwa.kotlinytmusicscraper.utils.parseTime

data class ArtistItemsContinuationPage(
    val items: List<YTItem>,
    val continuation: String?,
) {
    companion object {
        fun fromMusicResponsiveListItemRenderer(renderer: MusicResponsiveListItemRenderer): SongItem? {
            return SongItem(
                id = renderer.videoId ?: return null,
                title =
                    renderer.flexColumns
                        .firstOrNull()
                        ?.musicResponsiveListItemFlexColumnRenderer
                        ?.text
                        ?.runs
                        ?.firstOrNull()
                        ?.text ?: return null,
                artists =
                    renderer.flexColumns
                        .getOrNull(1)
                        ?.musicResponsiveListItemFlexColumnRenderer
                        ?.text
                        ?.runs
                        // The column reads "Artist • Album • 13M plays"; only the first group is
                        // artists, so everything after the first " • " is dropped.
                        ?.splitBySeparator()
                        ?.firstOrNull()
                        ?.oddElements()
                        ?.map {
                            Artist(
                                name = it.text,
                                id = it.navigationEndpoint?.browseEndpoint?.browseId,
                            )
                        } ?: return null,
                album =
                    renderer.flexColumns.getOrNull(2)?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.firstOrNull()?.let {
                        Album(
                            name = it.text,
                            id = it.navigationEndpoint?.browseEndpoint?.browseId ?: return null,
                        )
                    },
                duration =
                    renderer.fixedColumns
                        ?.firstOrNull()
                        ?.musicResponsiveListItemFlexColumnRenderer
                        ?.text
                        ?.runs
                        ?.firstOrNull()
                        ?.text
                        ?.parseTime() ?: return null,
                thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: return null,
                explicit =
                    renderer.badges?.find {
                        it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                    } != null,
                endpoint =
                    renderer.overlay
                        ?.musicItemThumbnailOverlayRenderer
                        ?.content
                        ?.musicPlayButtonRenderer
                        ?.playNavigationEndpoint
                        ?.watchEndpoint,
            )
        }
    }
}