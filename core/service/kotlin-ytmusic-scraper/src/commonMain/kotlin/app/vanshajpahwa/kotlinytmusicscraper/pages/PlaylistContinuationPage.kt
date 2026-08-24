package app.vanshajpahwa.kotlinytmusicscraper.pages

import app.vanshajpahwa.kotlinytmusicscraper.models.SongItem

data class PlaylistContinuationPage(
    val songs: List<SongItem>,
    val continuation: String?,
)