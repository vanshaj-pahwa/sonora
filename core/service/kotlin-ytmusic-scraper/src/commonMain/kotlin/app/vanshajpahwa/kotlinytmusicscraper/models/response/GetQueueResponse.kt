package app.vanshajpahwa.kotlinytmusicscraper.models.response

import app.vanshajpahwa.kotlinytmusicscraper.models.PlaylistPanelRenderer
import kotlinx.serialization.Serializable

@Serializable
data class GetQueueResponse(
    val queueDatas: List<QueueData>,
) {
    @Serializable
    data class QueueData(
        val content: PlaylistPanelRenderer.Content,
    )
}