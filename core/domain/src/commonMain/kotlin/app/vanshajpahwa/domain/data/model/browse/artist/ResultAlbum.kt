package app.vanshajpahwa.domain.data.model.browse.artist

import app.vanshajpahwa.domain.data.model.searchResult.songs.Thumbnail
import app.vanshajpahwa.domain.data.type.HomeContentType

data class ResultAlbum(
    val browseId: String,
    val isExplicit: Boolean,
    val thumbnails: List<Thumbnail>,
    val title: String,
    val year: String,
) : HomeContentType