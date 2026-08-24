package app.vanshajpahwa.sonora.viewModel

import androidx.lifecycle.viewModelScope
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.mediaservice.handler.PlaylistType
import app.vanshajpahwa.domain.mediaservice.handler.QueueData
import app.vanshajpahwa.domain.repository.SongRepository
import app.vanshajpahwa.domain.repository.ArtistRepository
import app.vanshajpahwa.sonora.viewModel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * The full recommendation list behind a Home "More like X" shelf.
 *
 * Runs the same `getRelatedData` query the shelf ran, rather than receiving its three visible tracks,
 * so this screen shows everything YouTube returned instead of only what fitted in a row.
 */
class MoreLikeViewModel(
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository,
) : BaseViewModel() {
    private val _tracks: MutableStateFlow<List<Track>> = MutableStateFlow(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var loaded: String? = null

    fun load(
        seedVideoId: String,
        seedChannelId: String?,
    ) {
        // Guarded because the screen calls this from a LaunchedEffect that also re-runs on
        // configuration change, and refetching would reshuffle the list under the user.
        val key = seedChannelId ?: seedVideoId
        if (loaded == key) return
        loaded = key
        viewModelScope.launch {
            _isLoading.value = true
            // An artist shelf came from that artist's radio, so the full list has to come from the
            // same place. Falling back to a song's related list here would show something different
            // from what the user tapped through.
            if (seedChannelId != null) {
                runCatching {
                    val browse = artistRepository.getArtistData(seedChannelId).first()
                    val radio = browse.data?.radioId
                    if (radio != null) {
                        _tracks.value = songRepository.getRadioFromEndpoint(radio).first().data?.first.orEmpty()
                    }
                }
                _isLoading.value = false
                return@launch
            }
            songRepository.getRelatedData(seedVideoId).collectLatest { res ->
                _tracks.value = res.data?.first.orEmpty()
                _isLoading.value = false
            }
        }
    }

    fun play(
        videoId: String,
        seedName: String,
    ) {
        val list = _tracks.value
        val track = list.find { it.videoId == videoId } ?: return
        setQueueData(
            QueueData.Data(
                listTracks = ArrayList(list),
                firstPlayedTrack = track,
                playlistId = null,
                playlistName = "More like $seedName",
                playlistType = PlaylistType.RADIO,
                continuation = null,
            ),
        )
        loadMediaItem(
            track,
            Config.PLAYLIST_CLICK,
            list.indexOf(track).coerceAtLeast(0),
        )
    }
}
