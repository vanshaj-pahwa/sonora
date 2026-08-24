package app.vanshajpahwa.kotlinytmusicscraper.pages

import app.vanshajpahwa.kotlinytmusicscraper.models.AlbumItem
import app.vanshajpahwa.kotlinytmusicscraper.models.VideoItem

data class ExplorePage(
    val released: List<AlbumItem>,
    val musicVideo: List<VideoItem>,
)