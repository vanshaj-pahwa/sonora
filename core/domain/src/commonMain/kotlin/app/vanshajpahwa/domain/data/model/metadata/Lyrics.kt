package app.vanshajpahwa.domain.data.model.metadata

import kotlinx.serialization.Serializable

@Serializable
data class Lyrics(
    val error: Boolean = false,
    val lines: List<Line>?,
    val syncType: String?,
    val sonoraLyrics: SonoraLyrics? = null,
)

@Serializable
data class SonoraLyrics(
    val id: String,
    val vote: Int,
)