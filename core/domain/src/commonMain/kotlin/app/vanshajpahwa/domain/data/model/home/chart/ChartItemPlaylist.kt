package app.vanshajpahwa.domain.data.model.home.chart

import app.vanshajpahwa.domain.data.model.browse.artist.ResultPlaylist

data class ChartItemPlaylist(
    val title: String,
    val playlists: List<ResultPlaylist>,
)