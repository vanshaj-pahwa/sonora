package app.vanshajpahwa.domain.data.model.mood.genre

import app.vanshajpahwa.domain.data.model.searchResult.songs.Thumbnail
import app.vanshajpahwa.domain.data.type.HomeContentType

data class Content(
    val playlistBrowseId: String,
    val thumbnail: List<Thumbnail>?,
    val title: Title,
    /**
     * YouTube's own page type for this card, for example MUSIC_PAGE_TYPE_ALBUM.
     *
     * Genre shelves mix playlists, albums and artists, and every one of them arrives in
     * [playlistBrowseId] regardless of what it is. Guessing from the id's prefix works until it
     * does not; this is the answer the response already carries. Null when YouTube omitted it, in
     * which case the caller falls back to the prefix.
     */
    val pageType: String? = null,
) : HomeContentType