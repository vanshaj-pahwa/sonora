package app.vanshajpahwa.domain.data.model.searchResult

import app.vanshajpahwa.domain.data.type.SearchResultType

data class SearchSuggestions(
    val queries: List<String>,
    val recommendedItems: List<SearchResultType>,
)