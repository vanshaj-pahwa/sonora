package app.vanshajpahwa.domain.data.model.browse.artist

import app.vanshajpahwa.domain.data.model.searchResult.songs.Thumbnail
import app.vanshajpahwa.domain.data.type.HomeContentType

data class ResultPlaylist(
    val id: String,
    val author: String,
    val thumbnails: List<Thumbnail>,
    val title: String,
) : HomeContentType