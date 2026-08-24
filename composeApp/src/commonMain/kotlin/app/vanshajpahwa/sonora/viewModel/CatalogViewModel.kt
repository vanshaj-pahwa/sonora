package app.vanshajpahwa.sonora.viewModel

import androidx.lifecycle.viewModelScope
import app.vanshajpahwa.domain.data.entities.AlbumEntity
import app.vanshajpahwa.domain.data.model.home.HomeItem
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.domain.repository.AlbumRepository
import app.vanshajpahwa.domain.repository.HomeRepository
import app.vanshajpahwa.sonora.ui.navigation.destination.list.CatalogDestination
import app.vanshajpahwa.sonora.viewModel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/** Verified against the live API: both return 200 with carousel shelves parseMixedContent handles. */
private const val PODCASTS_BROWSE_ID = "FEmusic_podcasts"
private const val NEW_RELEASES_BROWSE_ID = "FEmusic_new_releases"

/**
 * Backs the Albums and Podcasts catalog pages opened from Library.
 *
 * Both come back as ordinary [HomeItem] shelves, so the screen renders them with the same components
 * the feed uses and nothing new had to be parsed.
 *
 * Neither page has a separate "logged in" and "logged out" code path, deliberately. YouTube already
 * personalises these responses when a cookie is present and serves popular content when it is not,
 * so branching here would only duplicate a decision the server has already made, and would drift the
 * moment YouTube changed it.
 */
class CatalogViewModel(
    private val homeRepository: HomeRepository,
    private val dataStoreManager: DataStoreManager,
    private val albumRepository: AlbumRepository,
) : BaseViewModel() {
    // Local sections. The browse only ever returns one shelf for albums, so these are what give the
    // page more than a single row. Both are hidden entirely when empty, so a new user sees the
    // browse shelf alone rather than two empty headings.
    val savedAlbums: StateFlow<List<AlbumEntity>> =
        albumRepository
            .getLikedAlbums()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recentAlbums: StateFlow<List<AlbumEntity>> =
        albumRepository
            .getAllAlbums(20)
            .map { albums -> albums.sortedByDescending { it.inLibrary } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _shelves: MutableStateFlow<List<HomeItem>> = MutableStateFlow(emptyList())
    val shelves: StateFlow<List<HomeItem>> = _shelves

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var loadedType: String? = null

    fun load(
        type: String,
        viewString: String,
        songString: String,
        newReleaseString: String,
        musicVideoString: String,
    ) {
        if (loadedType == type) return
        loadedType = type
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val browseId =
                if (type == CatalogDestination.PODCASTS) PODCASTS_BROWSE_ID else NEW_RELEASES_BROWSE_ID
            val result = homeRepository.getBrowseShelves(browseId, viewString, songString).firstOrNull()
            val shelves =
                result
                    ?.data
                    .orEmpty()
                    .filter { it.contents.isNotEmpty() }
                    .let { all ->
                        // The new releases page carries a music videos shelf beside the albums one,
                        // and videos are not albums. Matched on the returned wording rather than the
                        // app's own string, because this is YouTube's title, not ours.
                        if (type == CatalogDestination.ALBUMS) {
                            all.filterNot { it.title.contains("video", ignoreCase = true) }
                        } else {
                            // Podcasts are shown whole: popular shows, popular episodes and every
                            // genre carousel YouTube returns. Nothing is dropped.
                            all
                        }
                    }
            if (shelves.isEmpty()) {
                _errorMessage.value = result?.message
                _shelves.value = emptyList()
            } else {
                _shelves.value = shelves
            }
            _isLoading.value = false
        }
    }
}
