package app.vanshajpahwa.data.parser.search

import app.vanshajpahwa.domain.data.model.searchResult.playlists.PlaylistsResult
import app.vanshajpahwa.domain.data.model.searchResult.songs.Thumbnail
import app.vanshajpahwa.kotlinytmusicscraper.models.PlaylistItem
import app.vanshajpahwa.kotlinytmusicscraper.pages.SearchResult

internal fun parseSearchPlaylist(result: SearchResult): ArrayList<PlaylistsResult> {
    val playlistsResult: ArrayList<PlaylistsResult> = arrayListOf()
    result.items.forEach {
        val playlist = it as PlaylistItem
        playlistsResult.add(
            PlaylistsResult(
                author = playlist.author?.name ?: "",
                browseId = playlist.id,
                category = "playlist",
                itemCount = playlist.songCountText ?: "",
                resultType = "Playlist",
                thumbnails =
                    listOf(
                        Thumbnail(
                            544,
                            if (playlist.thumbnail.contains(Regex("([wh])120"))) {
                                Regex("([wh])120").replace(
                                    playlist.thumbnail,
                                    "$1544",
                                )
                            } else {
                                playlist.thumbnail
                            },
                            544,
                        ),
                    ),
                title = playlist.title,
            ),
        )
    }
    return playlistsResult
}