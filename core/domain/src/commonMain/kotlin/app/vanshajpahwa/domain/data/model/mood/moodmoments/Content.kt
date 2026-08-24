package app.vanshajpahwa.domain.data.model.mood.moodmoments

import app.vanshajpahwa.domain.data.model.searchResult.songs.Thumbnail
import app.vanshajpahwa.domain.data.type.HomeContentType

data class Content(
    val playlistBrowseId: String,
    val subtitle: String,
    val thumbnails: List<Thumbnail>?,
    val title: String,
    /**
     * Set only for entries of the "Songs" shelf, which YouTube sends as
     * `musicResponsiveListItemRenderer` instead of the `musicTwoRowItemRenderer` used by every
     * other shelf. Null means this is a playlist and [playlistBrowseId] is the one to open.
     */
    val videoId: String? = null,
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
