package app.vanshajpahwa.domain.data.model.home

import app.vanshajpahwa.domain.data.model.searchResult.songs.Thumbnail
import kotlinx.serialization.Serializable

@Serializable
data class HomeItem(
    val contents: List<Content?>,
    val title: String,
    val subtitle: String? = null,
    val thumbnail: List<Thumbnail>? = null,
    val channelId: String? = null,
)