package app.vanshajpahwa.domain.data.model.mood.genre

import app.vanshajpahwa.domain.data.model.searchResult.songs.Thumbnail
import app.vanshajpahwa.domain.data.type.HomeContentType

data class Content(
    val playlistBrowseId: String,
    val thumbnail: List<Thumbnail>?,
    val title: Title,
) : HomeContentType