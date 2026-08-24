package app.vanshajpahwa.sonora.viewModel

import androidx.lifecycle.viewModelScope
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.model.searchResult.songs.Artist
import app.vanshajpahwa.domain.data.model.searchResult.songs.Thumbnail
import app.vanshajpahwa.domain.mediaservice.handler.PlaylistType
import app.vanshajpahwa.domain.mediaservice.handler.QueueData
import app.vanshajpahwa.sonora.expect.ui.LocalAudioFile
import app.vanshajpahwa.sonora.expect.ui.hasLocalAudioPermission
import app.vanshajpahwa.sonora.expect.ui.scanLocalAudio
import app.vanshajpahwa.sonora.viewModel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Backs the Local files screen: the device's own audio, playable through the normal player.
 *
 * A local file is represented as a [Track] whose `videoId` is its `content://` URI. That reads oddly
 * but it is the reason nothing else in the playback stack needed changing: the Media3 layer builds
 * every item with `setUri(videoId)`, so a URI in that field produces a MediaItem pointing at a real
 * file, and the stream resolver recognises the scheme and hands it straight to the file reader
 * rather than trying to exchange it for a YouTube stream.
 */
class LocalFilesViewModel : BaseViewModel() {
    private val _files: MutableStateFlow<List<LocalAudioFile>> = MutableStateFlow(emptyList())
    val files: StateFlow<List<LocalAudioFile>> = _files

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _hasPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _hasPermission.value = hasLocalAudioPermission()
            if (!_hasPermission.value) {
                _files.value = emptyList()
                return@launch
            }
            _isLoading.value = true
            _files.value = scanLocalAudio()
            _isLoading.value = false
        }
    }

    fun play(uri: String) {
        val all = _files.value
        val target = all.find { it.uri == uri } ?: return
        val tracks = all.map { it.toTrack() }
        setQueueData(
            QueueData.Data(
                listTracks = ArrayList(tracks),
                firstPlayedTrack = target.toTrack(),
                playlistId = null,
                playlistName = "Local files",
                playlistType = PlaylistType.RADIO,
                continuation = null,
            ),
        )
        loadMediaItem(
            target.toTrack(),
            Config.PLAYLIST_CLICK,
            all.indexOf(target).coerceAtLeast(0),
        )
    }
}

/**
 * Maps a device file onto the app's track model.
 *
 * `isAvailable` is true and `likeStatus` null on purpose: these never round trip to YouTube, so
 * anything that would try to fetch related tracks, a like state or lyrics for them has nothing to
 * ask about. They play, and that is the whole contract.
 */
fun LocalAudioFile.toTrack(): Track =
    Track(
        album = null,
        artists = listOf(Artist(id = null, name = artist)),
        duration = durationMs.takeIf { it > 0 }?.let { formatMillis(it) },
        durationSeconds = (durationMs / 1000).toInt(),
        isAvailable = true,
        isExplicit = false,
        likeStatus = null,
        thumbnails = albumArtUri?.let { listOf(Thumbnail(width = 512, url = it, height = 512)) },
        title = title,
        videoId = uri,
        videoType = null,
        category = null,
        feedbackTokens = null,
        resultType = "song",
    )

private fun formatMillis(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
