package app.vanshajpahwa.kotlinytmusicscraper.models.response

import app.vanshajpahwa.kotlinytmusicscraper.models.Continuation
import app.vanshajpahwa.kotlinytmusicscraper.models.MusicResponsiveListItemRenderer
import app.vanshajpahwa.kotlinytmusicscraper.models.MusicShelfRenderer
import app.vanshajpahwa.kotlinytmusicscraper.models.Tabs
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val contents: Contents?,
    val continuationContents: ContinuationContents?,
) {
    @Serializable
    data class Contents(
        val tabbedSearchResultsRenderer: Tabs?,
    )

    @Serializable
    data class ContinuationContents(
        val musicShelfContinuation: MusicShelfContinuation,
    ) {
        @Serializable
        data class MusicShelfContinuation(
            val contents: List<Content>,
            val continuations: List<Continuation>?,
        ) {
            @Serializable
            data class Content(
                val musicResponsiveListItemRenderer: MusicResponsiveListItemRenderer?,
                val musicMultiRowListItemRenderer: MusicShelfRenderer.Content.MusicMultiRowListItemRenderer?,
            )
        }
    }
}