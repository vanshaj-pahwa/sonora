package app.vanshajpahwa.domain.data.model.home

import app.vanshajpahwa.domain.data.model.searchResult.songs.Album
import app.vanshajpahwa.domain.data.model.searchResult.songs.Artist
import app.vanshajpahwa.domain.data.model.searchResult.songs.Thumbnail
import app.vanshajpahwa.domain.data.type.HomeContentType
import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val album: Album?,
    val artists: List<Artist>?,
    val description: String?,
    val isExplicit: Boolean?,
    val playlistId: String?,
    val browseId: String?,
    val thumbnails: List<Thumbnail>,
    val title: String,
    val videoId: String?,
    val views: String?,
    val durationSeconds: Int? = null,
    val radio: String? = null,
) : HomeContentType