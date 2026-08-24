package app.vanshajpahwa.kotlinytmusicscraper.models.body

import app.vanshajpahwa.kotlinytmusicscraper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class LikeBody(
    val context: Context,
    val target: Target,
) {
    @Serializable
    data class Target(
        val videoId: String,
    )
}