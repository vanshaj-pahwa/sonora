package app.vanshajpahwa.sonora.viewModel

import app.vanshajpahwa.domain.repository.HomeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Cover art for browse categories, resolved once and shared by Search and Home.
 *
 * The category list carries no artwork, so each cover costs its own browse. Both screens show the
 * same categories, so holding this per screen would buy the same picture twice and let one screen
 * sit blank while the other had it. A single store means whichever screen is opened first warms the
 * other, and [requested] guarantees one request per category for the life of the process.
 */
class MoodArtworkStore(
    private val homeRepository: HomeRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val requested = mutableSetOf<String>()

    private val _artwork: MutableStateFlow<Map<String, String>> = MutableStateFlow(emptyMap())
    val artwork: StateFlow<Map<String, String>> get() = _artwork.asStateFlow()

    fun load(params: String) {
        synchronized(requested) { if (!requested.add(params)) return }
        scope.launch {
            homeRepository.getMoodCategoryArtwork(params).collect { url ->
                if (url != null) _artwork.update { it + (params to url) }
            }
        }
    }
}
