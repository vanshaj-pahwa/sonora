package app.vanshajpahwa.sonora.viewModel

import androidx.lifecycle.viewModelScope
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.common.LibraryChipType
import app.vanshajpahwa.domain.data.entities.AlbumEntity
import app.vanshajpahwa.domain.data.entities.LocalPlaylistEntity
import app.vanshajpahwa.domain.data.entities.PlaylistEntity
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.domain.data.model.searchResult.playlists.PlaylistsResult
import app.vanshajpahwa.domain.data.type.PlaylistType
import app.vanshajpahwa.domain.data.type.RecentlyType
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.domain.repository.AlbumRepository
import app.vanshajpahwa.domain.repository.ArtistRepository
import app.vanshajpahwa.domain.repository.CommonRepository
import app.vanshajpahwa.domain.repository.LocalPlaylistRepository
import app.vanshajpahwa.domain.repository.PlaylistRepository
import app.vanshajpahwa.domain.repository.PodcastRepository
import app.vanshajpahwa.domain.repository.SongRepository
import app.vanshajpahwa.domain.utils.LocalResource
import app.vanshajpahwa.domain.utils.Resource
import app.vanshajpahwa.domain.utils.isRadioPlaylistId
import app.vanshajpahwa.sonora.viewModel.base.BaseViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.added_local_playlist
import app.vanshajpahwa.sonora.resources.youtube_liked_music

class LibraryViewModel(
    private val dataStoreManager: DataStoreManager,
    private val songRepository: SongRepository,
    private val commonRepository: CommonRepository,
    private val playlistRepository: PlaylistRepository,
    private val localPlaylistRepository: LocalPlaylistRepository,
    private val albumRepository: AlbumRepository,
    private val podcastRepository: PodcastRepository,
    private val artistRepository: ArtistRepository,
) : BaseViewModel() {
    // Backing data for the shortcut cards at the top of the screen.
    //
    // Downloaded and Most Played carry the track list itself, not just a count, because their cards
    // show the artwork of what is inside. The other two only ever render a number, so they are
    // collected as counts and the song rows are never held in memory.
    private val _downloadedShortcut: MutableStateFlow<List<SongEntity>> = MutableStateFlow(emptyList())
    val downloadedShortcut: StateFlow<List<SongEntity>> get() = _downloadedShortcut.asStateFlow()

    private val _mostPlayedShortcut: MutableStateFlow<List<SongEntity>> = MutableStateFlow(emptyList())
    val mostPlayedShortcut: StateFlow<List<SongEntity>> get() = _mostPlayedShortcut.asStateFlow()

    private val _favoriteCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val favoriteCount: StateFlow<Int> get() = _favoriteCount.asStateFlow()

    private val _followedCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val followedCount: StateFlow<Int> get() = _followedCount.asStateFlow()

    init {
        viewModelScope.launch {
            songRepository.getDownloadedSongs().collectLatest { songs ->
                _downloadedShortcut.value = songs.orEmpty()
            }
        }
        viewModelScope.launch {
            songRepository.getMostPlayedSongs().collectLatest { songs ->
                _mostPlayedShortcut.value = songs.sortedByDescending { it.totalPlayTime }
            }
        }
        viewModelScope.launch {
            songRepository.getLikedSongs().collectLatest { songs ->
                _favoriteCount.value = songs.size
            }
        }
        viewModelScope.launch {
            artistRepository.getFollowedArtists().collectLatest { artists ->
                _followedCount.value = artists.size
            }
        }
    }

    private val _currentScreen: MutableStateFlow<LibraryChipType> = MutableStateFlow(LibraryChipType.YOUR_LIBRARY)
    val currentScreen: StateFlow<LibraryChipType> get() = _currentScreen.asStateFlow()
    private val _recentlyAdded: MutableStateFlow<LocalResource<List<RecentlyType>>> =
        MutableStateFlow(LocalResource.Loading())
    val recentlyAdded: StateFlow<LocalResource<List<RecentlyType>>> get() = _recentlyAdded.asStateFlow()

    private val _yourLocalPlaylist: MutableStateFlow<LocalResource<List<LocalPlaylistEntity>>> =
        MutableStateFlow(LocalResource.Loading())
    val yourLocalPlaylist: StateFlow<LocalResource<List<LocalPlaylistEntity>>> get() = _yourLocalPlaylist.asStateFlow()

    private val _youTubePlaylist: MutableStateFlow<LocalResource<List<PlaylistsResult>>> =
        MutableStateFlow(LocalResource.Loading())
    val youTubePlaylist: StateFlow<LocalResource<List<PlaylistsResult>>> get() = _youTubePlaylist.asStateFlow()

    private val _youTubeMixForYou: MutableStateFlow<LocalResource<List<PlaylistsResult>>> =
        MutableStateFlow(LocalResource.Loading())
    val youTubeMixForYou: StateFlow<LocalResource<List<PlaylistsResult>>> get() = _youTubeMixForYou.asStateFlow()

    private val _favoritePlaylist: MutableStateFlow<LocalResource<List<PlaylistType>>> =
        MutableStateFlow(LocalResource.Loading())
    val favoritePlaylist: StateFlow<LocalResource<List<PlaylistType>>> get() = _favoritePlaylist.asStateFlow()

    private val _favoritePodcasts: MutableStateFlow<LocalResource<List<PlaylistType>>> =
        MutableStateFlow(LocalResource.Loading())
    val favoritePodcasts: StateFlow<LocalResource<List<PlaylistType>>> get() = _favoritePodcasts.asStateFlow()

    private val _downloadedPlaylist: MutableStateFlow<LocalResource<List<PlaylistType>>> =
        MutableStateFlow(LocalResource.Loading())
    val downloadedPlaylist: StateFlow<LocalResource<List<PlaylistType>>> get() = _downloadedPlaylist.asStateFlow()

    private val _listCanvasSong: MutableStateFlow<LocalResource<List<SongEntity>>> =
        MutableStateFlow(LocalResource.Loading())
    val listCanvasSong: StateFlow<LocalResource<List<SongEntity>>> get() = _listCanvasSong.asStateFlow()

    private val _accountThumbnail: MutableStateFlow<String?> = MutableStateFlow(null)
    val accountThumbnail: StateFlow<String?> get() = _accountThumbnail.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val youtubeLoggedIn = dataStoreManager.loggedIn.mapLatest { it == DataStoreManager.TRUE }

    /** Gates the analytics entry point: with tracking off there is nothing for that screen to show. */
    val localTrackingEnabled = dataStoreManager.localTrackingEnabled.mapLatest { it == DataStoreManager.TRUE }
    val enableLiquidGlass = dataStoreManager.enableLiquidGlass.mapLatest { it == DataStoreManager.TRUE }

    init {
        viewModelScope.launch {
            val currentScreenJob =
                launch {
                    dataStoreManager.getString("library_current_screen").first()?.let { chipType ->
                        LibraryChipType.fromStringValue(chipType)?.let {
                            _currentScreen.value = it
                        }
                    }
                }
            val cookieJob =
                launch {
                    dataStoreManager.cookie.distinctUntilChanged().collect {
                        _accountThumbnail.value = dataStoreManager.getString("AccountThumbUrl").first().takeIf { !it.isNullOrEmpty() }
                    }
                }
            currentScreenJob.join()
            cookieJob.join()
        }
    }

    fun setCurrentScreen(chipType: LibraryChipType) {
        _currentScreen.value = chipType
        viewModelScope.launch {
            dataStoreManager.putString("library_current_screen", chipType.toStringValue())
        }
    }

    fun getRecentlyAdded() {
        viewModelScope.launch {
            commonRepository.getAllRecentData().collectLatest { data ->
                val temp: MutableList<RecentlyType> = mutableListOf()
                temp.addAll(data)
                temp
                    .find {
                        it is PlaylistEntity && it.id.isRadioPlaylistId()
                    }.let {
                        temp.remove(it)
                    }
                temp.removeIf { it is SongEntity && it.inLibrary == Config.REMOVED_SONG_DATE_TIME }
                if (dataStoreManager.loggedIn.first() == DataStoreManager.TRUE) {
                    temp.removeIf { it is PlaylistEntity && it.id == "LM" }
                    temp.add(
                        PlaylistEntity(
                            title = getString(Res.string.youtube_liked_music),
                            author = "YouTube Music",
                            id = "LM",
                            description = "PIN",
                            thumbnails = "https://www.gstatic.com/youtube/media/ytm/images/pbg/liked-songs-delhi-1200.png",
                        ),
                    )
                }
                temp.reverse()
                _recentlyAdded.value = LocalResource.Success(temp.toImmutableList())
            }
        }
    }

    fun getYouTubePlaylist() {
        _youTubePlaylist.value = LocalResource.Loading()
        viewModelScope.launch {
            playlistRepository.getLibraryPlaylist().collect { data ->
                _youTubePlaylist.value = LocalResource.Success(data ?: emptyList())
            }
        }
    }

    fun getYouTubeMixedForYou() {
        _youTubeMixForYou.value = LocalResource.Loading()
        viewModelScope.launch {
            playlistRepository.getMixedForYou().collect { data ->
                _youTubeMixForYou.value = LocalResource.Success(data ?: emptyList())
            }
        }
    }

    fun getYouTubeLoggedIn(): Boolean = runBlocking { dataStoreManager.loggedIn.first() } == DataStoreManager.TRUE

    private var favoritePlaylistJob: Job? = null

    /**
     * Favourited albums and playlists, as one live list.
     *
     * This used to collect the album flow and then start a SECOND collect on the playlist flow
     * inside it. Both are Room flows, which never complete, so the inner collect never returned and
     * the outer collector stayed parked on its first emission for the life of the screen. The
     * effect: favouriting an album from its detail page was written correctly and then never read
     * again, because the only collector that would have seen it was blocked.
     *
     * Two more faults came from the same shape. The accumulator was created outside the inner
     * collect, so every playlist emission appended to a list that already held the previous ones and
     * the page filled with duplicates. And each call launched another permanent collector, so
     * switching chips back and forth stacked them.
     *
     * combine fixes all three: one collector, both sources live, a fresh list per emission.
     */
    fun getPlaylistFavorite() {
        favoritePlaylistJob?.cancel()
        favoritePlaylistJob =
            viewModelScope.launch {
                combine(
                    albumRepository.getLikedAlbums(),
                    playlistRepository.getLikedPlaylists(),
                ) { albums, playlists ->
                    (albums + playlists).sortedByDescending { item ->
                        // Newest first. The original comparator was commented "descending" but
                        // compared ascending, so a just-favourited item landed at the bottom.
                        when (item) {
                            is AlbumEntity -> item.favoriteAt ?: item.inLibrary
                            is PlaylistEntity -> item.favoriteAt ?: item.inLibrary
                            else -> null
                        }
                    }
                }.collect { sorted ->
                    _favoritePlaylist.value = LocalResource.Success(sorted)
                }
            }
    }

    fun getFavoritePodcasts() {
        viewModelScope.launch {
            podcastRepository.getFavoritePodcasts().collectLatest { podcasts ->
                val sortedList = podcasts.sortedByDescending { it.favoriteTime }
                _favoritePodcasts.value = LocalResource.Success(sortedList)
            }
        }
    }

    fun getCanvasSong() {
        _listCanvasSong.value = LocalResource.Loading()
        viewModelScope.launch {
            songRepository.getCanvasSong(max = 5).collect { data ->
                _listCanvasSong.value = LocalResource.Success(data)
            }
        }
    }

    fun getLocalPlaylist() {
        _yourLocalPlaylist.value = LocalResource.Loading()
        viewModelScope.launch {
            localPlaylistRepository.getAllLocalPlaylists().collect { values ->
//                    _listLocalPlaylist.postValue(values)
                _yourLocalPlaylist.value = LocalResource.Success(values.reversed())
            }
        }
    }

    fun getDownloadedPlaylist() {
        viewModelScope.launch {
            playlistRepository.getAllDownloadedPlaylist().collect { values ->
                _downloadedPlaylist.value = LocalResource.Success(values)
            }
        }
    }

    fun createPlaylist(title: String) {
        viewModelScope.launch {
            val localPlaylistEntity = LocalPlaylistEntity(title = title)
            localPlaylistRepository
                .insertLocalPlaylist(
                    localPlaylistEntity,
                    getString(Res.string.added_local_playlist),
                ).lastOrNull()
                ?.let {
                    log("Created playlist with id: $it")
                }
            getLocalPlaylist()
        }
    }

    fun deleteSong(videoId: String) {
        _recentlyAdded.value = LocalResource.Loading()
        viewModelScope.launch {
            songRepository.setInLibrary(videoId, Config.REMOVED_SONG_DATE_TIME)
            songRepository.resetTotalPlayTime(videoId)
            delay(500) // Wait for the database to update
            getRecentlyAdded()
        }
    }
}