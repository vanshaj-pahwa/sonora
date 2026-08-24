package app.vanshajpahwa.domain.data.model.mood.genre

import app.vanshajpahwa.domain.data.model.searchResult.songs.Artist

data class ItemsSong(
    val title: String,
    val artist: List<Artist>?,
    val videoId: String,
)