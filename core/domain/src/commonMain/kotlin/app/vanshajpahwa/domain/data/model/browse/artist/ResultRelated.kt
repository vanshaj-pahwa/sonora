package app.vanshajpahwa.domain.data.model.browse.artist

import app.vanshajpahwa.domain.data.model.searchResult.songs.Thumbnail

data class ResultRelated(
    val browseId: String,
    val subscribers: String,
    val thumbnails: List<Thumbnail>,
    val title: String,
)