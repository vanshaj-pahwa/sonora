package app.vanshajpahwa.kotlinytmusicscraper.models.body

import app.vanshajpahwa.kotlinytmusicscraper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetQueueBody(
    val context: Context,
    val videoIds: List<String>?,
    val playlistId: String?,
)