package app.vanshajpahwa.domain.data.model.browse.artist

import app.vanshajpahwa.domain.data.model.searchResult.songs.Thumbnail
import app.vanshajpahwa.domain.data.type.HomeContentType

data class ResultSingle(
    val browseId: String,
    val thumbnails: List<Thumbnail>,
    val title: String,
    val year: String,
) : HomeContentType