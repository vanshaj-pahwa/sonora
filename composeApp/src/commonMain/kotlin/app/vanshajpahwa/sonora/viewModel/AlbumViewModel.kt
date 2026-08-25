package app.vanshajpahwa.sonora.viewModel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.domain.data.entities.DownloadState
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.model.browse.artist.ResultAlbum
import app.vanshajpahwa.domain.data.model.searchResult.songs.Artist
import app.vanshajpahwa.domain.extension.now
import app.vanshajpahwa.domain.mediaservice.handler.DownloadHandler
import app.vanshajpahwa.domain.mediaservice.handler.PlaylistType
import app.vanshajpahwa.domain.mediaservice.handler.QueueData
import app.vanshajpahwa.domain.repository.AlbumRepository
import app.vanshajpahwa.domain.repository.SongRepository
import app.vanshajpahwa.domain.utils.Resource
import app.vanshajpahwa.domain.utils.toAlbumEntity
import app.vanshajpahwa.domain.utils.toArrayListTrack
import app.vanshajpahwa.domain.utils.toSongEntity
import app.vanshajpahwa.logger.LogLevel
import app.vanshajpahwa.sonora.viewModel.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.album
import app.vanshajpahwa.sonora.resources.downloaded
import app.vanshajpahwa.sonora.resources.error
import app.vanshajpahwa.sonora.resources.playlist_is_empty
import app.vanshajpahwa.domain.repository.ArtistRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AlbumViewModel(
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
) : BaseViewModel() {
    private val downloadUtils: DownloadHandler by inject<DownloadHandler>()
    private val _uiState: MutableStateFlow<AlbumUIState> = MutableStateFlow(AlbumUIState.initial())
    val uiState: StateFlow<AlbumUIState> = _uiState

    /**
     * The album artist's picture, when the device already has one.
     *
     * Read from Room rather than fetched. An album page should not spend a request on decoration,
     * and any artist the user has played, followed or browsed is already stored with a thumbnail.
     * Null simply means no picture, and the row falls back to the name on its own.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val artistThumbnail: StateFlow<String?> =
        uiState
            .map { it.artist.id to it.artist.name }
            .distinctUntilChanged()
            .flatMapLatest { (id, name) ->
                if (id.isNullOrBlank() && name.isBlank()) {
                    flowOf(null)
                } else {
                    artistRepository.getArtistByIdOrName(id, name).map { it?.thumbnails }
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)


    private var job: Job? = null
    private var collectDownloadStateJob: Job? = null

    fun updateBrowseId(browseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(browseId = browseId) }
            albumRepository.getAlbumData(browseId).collectLatest { res ->
                when (res) {
                    is Resource.Success -> {
                        val data = res.data
                        if (data != null) {
                            _uiState.update {
                                it.copy(
                                    browseId = browseId,
                                    title = data.title,
                                    thumbnail = data.thumbnails?.lastOrNull()?.url,
                                    artist =
                                        data.artists.firstOrNull() ?: Artist(
                                            id = null,
                                            name = "",
                                        ),
                                    year = data.year ?: now().year.toString(),
                                    trackCount = data.trackCount,
                                    description = data.description,
                                    length = data.duration ?: "",
                                    listTrack = data.tracks,
                                    otherVersion = data.otherVersion,
                                    loadState = LocalPlaylistState.PlaylistLoadState.Success,
                                )
                            }
                            val localAlbum = albumRepository.getAlbum(browseId).lastOrNull()
                            if (localAlbum != null) {
                                _uiState.update {
                                    it.copy(
                                        downloadState = localAlbum.downloadState,
                                        liked = localAlbum.liked,
                                    )
                                }
                                albumRepository.updateAlbumInLibrary(now(), browseId)
                            } else {
                                albumRepository.insertAlbum(data.toAlbumEntity(browseId)).singleOrNull().let {
                                    log("Insert Album $it")
                                    data.tracks.forEach { track ->
                                        songRepository
                                            .insertSong(
                                                track.toSongEntity().copy(
                                                    inLibrary = Config.REMOVED_SONG_DATE_TIME,
                                                ),
                                            ).singleOrNull()
                                            ?.let {
                                                log("Insert Song $it")
                                            }
                                    }
                                }
                            }
                            getAlbumFlow(browseId)
                        } else {
                            makeToast(getString(Res.string.error) + ": Null data")
                            _uiState.update {
                                it.copy(
                                    loadState = LocalPlaylistState.PlaylistLoadState.Error,
                                )
                            }
                        }
                    }

                    is Resource.Error -> {
                        albumRepository.getAlbum(browseId).singleOrNull().let { albumEntity ->
                            if (albumEntity != null) {
                                _uiState.update {
                                    it.copy(
                                        browseId = browseId,
                                        title = albumEntity.title,
                                        thumbnail = albumEntity.thumbnails,
                                        artist =
                                            Artist(
                                                id = albumEntity.artistId?.firstOrNull(),
                                                name = albumEntity.artistName?.firstOrNull() ?: "",
                                            ),
                                        year = albumEntity.year ?: now().year.toString(),
                                        trackCount = albumEntity.trackCount,
                                        description = albumEntity.description,
                                        length = albumEntity.duration ?: "",
                                        listTrack =
                                            (
                                                songRepository
                                                    .getSongsByListVideoId(albumEntity.tracks ?: emptyList())
                                                    .singleOrNull() ?: emptyList()
                                            ).toArrayListTrack(),
                                        loadState = LocalPlaylistState.PlaylistLoadState.Success,
                                    )
                                }
                            } else {
                                log("Error: ${res.message}", LogLevel.ERROR)
                                makeToast(getString(Res.string.error) + ": ${res.message}")
                                _uiState.update {
                                    it.copy(
                                        loadState = LocalPlaylistState.PlaylistLoadState.Error,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setBrush(brush: List<Color>) {
        _uiState.update {
            it.copy(
                colors = brush,
            )
        }
    }

    fun setAlbumLike() {
        viewModelScope.launch {
            albumRepository.updateAlbumLiked(uiState.value.browseId, if (!uiState.value.liked) 1 else 0)
            _uiState.update {
                it.copy(
                    liked = !it.liked,
                )
            }
        }
    }

    private fun getAlbumFlow(browseId: String) {
        job?.cancel()
        collectDownloadStateJob?.cancel()
        job =
            viewModelScope.launch {
                albumRepository.getAlbumAsFlow(browseId).collectLatest { album ->
                    if (album != null) {
                        _uiState.update {
                            it.copy(
                                downloadState = album.downloadState,
                                liked = album.liked,
                            )
                        }
                    }
                }
            }
        collectDownloadStateJob =
            viewModelScope.launch {
                downloadUtils.downloadTask.collectLatest { downloadTask ->
                    var count = 0
                    uiState.value.listTrack.forEach { track ->
                        if (downloadTask[track.videoId] == DownloadState.STATE_DOWNLOADED) {
                            count++
                        }
                    }
                    if (count == uiState.value.listTrack.size) {
                        albumRepository.updateAlbumDownloadState(uiState.value.browseId, DownloadState.STATE_DOWNLOADED)
                        _uiState.update {
                            it.copy(
                                downloadState = DownloadState.STATE_DOWNLOADED,
                            )
                        }
                    }
                }
            }
    }

    fun playTrack(track: Track) {
        setQueueData(
            QueueData.Data(
                listTracks = uiState.value.listTrack.toCollection(ArrayList()),
                firstPlayedTrack = track,
                playlistId = uiState.value.browseId.replaceFirst("VL", ""),
                playlistName = "${getString(Res.string.album)} \"${uiState.value.title}\"",
                playlistType = PlaylistType.PLAYLIST,
                continuation = null,
            ),
        )
        val index = uiState.value.listTrack.indexOf(track)
        loadMediaItem(track, Config.ALBUM_CLICK, if (index == -1) 0 else index)
    }

    fun shuffle() {
        if (uiState.value.listTrack.isEmpty()) {
            makeToast(getString(Res.string.playlist_is_empty))
            return
        }
        val shuffleList = uiState.value.listTrack.shuffled()
        val randomIndex = shuffleList.indices.random()
        setQueueData(
            QueueData.Data(
                listTracks = shuffleList.toCollection(ArrayList()),
                firstPlayedTrack = shuffleList[randomIndex],
                playlistId = uiState.value.browseId.replaceFirst("VL", ""),
                playlistName = "${getString(Res.string.album)} \"${uiState.value.title}\"",
                playlistType = PlaylistType.PLAYLIST,
                continuation = null,
            ),
        )
        loadMediaItem(shuffleList[randomIndex], Config.ALBUM_CLICK, randomIndex)
    }

    fun downloadFullAlbum() {
        viewModelScope.launch {
            // Insert all song to database
            uiState.value.listTrack.forEach { track ->
                songRepository.insertSong(track.toSongEntity()).singleOrNull()?.let {
                    log("Insert Song $it")
                }
            }
            val fullListSong =
                songRepository
                    .getSongsByListVideoId(uiState.value.listTrack.map { it.videoId })
                    .singleOrNull() ?: emptyList()
            log("Full list song: $fullListSong")
            if (fullListSong.isEmpty()) {
                makeToast(getString(Res.string.playlist_is_empty))
                return@launch
            }
            val listJob = fullListSong.filter { it.downloadState != DownloadState.STATE_DOWNLOADED }
            log("List job: $listJob")
            if (listJob.isEmpty()) {
                makeToast(getString(Res.string.downloaded))
                return@launch
            }
            albumRepository.updateAlbumDownloadState(uiState.value.browseId, DownloadState.STATE_DOWNLOADING)
            listJob.forEach {
                log("Download: ${it.videoId} ${it.thumbnails}")
                downloadUtils.downloadTrack(
                    it.videoId,
                    it.title,
                    it.thumbnails ?: "",
                )
            }
        }
    }
}

data class AlbumUIState(
    val browseId: String = "",
    val title: String = "",
    val thumbnail: String? = null,
    val colors: List<Color> = listOf(Color.Black, Color.Black),
    val artist: Artist =
        Artist(
            id = null,
            name = "",
        ),
    val year: String = now().year.toString(),
    val downloadState: Int = DownloadState.STATE_NOT_DOWNLOADED,
    val liked: Boolean = false,
    val trackCount: Int = 0,
    val description: String? = null,
    val length: String = "",
    val listTrack: List<Track> = emptyList(),
    val otherVersion: List<ResultAlbum> = emptyList(),
    val loadState: LocalPlaylistState.PlaylistLoadState = LocalPlaylistState.PlaylistLoadState.Loading,
) {
    companion object {
        fun initial(): AlbumUIState = AlbumUIState()
    }
}