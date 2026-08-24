package app.vanshajpahwa.sonora.viewModel

import androidx.lifecycle.viewModelScope
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.common.SELECTED_LANGUAGE
import app.vanshajpahwa.common.SUPPORTED_LANGUAGE
import app.vanshajpahwa.domain.data.entities.ArtistEntity
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.domain.data.player.GenericMediaItem
import app.vanshajpahwa.domain.data.model.home.HomeDataCombine
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.model.home.HomeItem
import app.vanshajpahwa.domain.data.model.home.chart.Chart
import app.vanshajpahwa.domain.data.model.mood.Mood
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.domain.manager.DataStoreManager.Values.TRUE
import app.vanshajpahwa.domain.mediaservice.handler.PlaylistType
import app.vanshajpahwa.domain.mediaservice.handler.QueueData
import app.vanshajpahwa.domain.repository.ArtistRepository
import app.vanshajpahwa.domain.repository.HomeRepository
import app.vanshajpahwa.domain.repository.SongRepository
import app.vanshajpahwa.domain.utils.toArrayListTrack
import app.vanshajpahwa.domain.utils.toTrack
import app.vanshajpahwa.domain.utils.Resource
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.viewModel.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.music_video
import app.vanshajpahwa.sonora.resources.new_release
import app.vanshajpahwa.sonora.resources.song
import app.vanshajpahwa.sonora.resources.view_count

private const val TAG = "HomeViewModel"

/**
 * How long each recommendation shelf stays on screen before the next one takes its place.
 *
 * Ten seconds rather than five: a row that swaps every five is still moving while the user is
 * reading the three titles on it, and worse, it can change under a finger already on its way to a
 * card.
 */
private const val SHELF_ROTATION_MS = 10_000L

@OptIn(FlowPreview::class)
private const val PLAY_IT_AGAIN_COUNT = 3

class HomeViewModel(
    private val dataStoreManager: DataStoreManager,
    private val homeRepository: HomeRepository,
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository,
    private val moodArtworkStore: MoodArtworkStore,
    private val remoteFeedHealth: RemoteFeedHealth,
) : BaseViewModel() {
    // Same store Search uses, so the mood and genre tiles here are already warm if Search was open.
    val moodArtwork: StateFlow<Map<String, String>> get() = moodArtworkStore.artwork

    fun loadMoodArtwork(params: String) = moodArtworkStore.load(params)

    /**
     * Artists to show on Home: the ones the user follows, topped up from everything else in the
     * library so the row is not empty for someone who has never pressed follow.
     */
    private val _topArtists: MutableStateFlow<List<ArtistEntity>> = MutableStateFlow(emptyList())
    val topArtists: StateFlow<List<ArtistEntity>> = _topArtists
    // Local listening data. All three queries already existed end to end and were only ever read by
    // a Library sub page, so Home showed nothing the user had actually listened to. These are what
    // let the first screen render instantly and work with no network.
    private val _mostPlayed: MutableStateFlow<List<SongEntity>> = MutableStateFlow(emptyList())
    val mostPlayed: StateFlow<List<SongEntity>> = _mostPlayed

    /**
     * The three tracks behind "Play it again", picked at random rather than by play time.
     *
     * Held here rather than remembered in the composable, because the pool changes constantly: play
     * time ticks up while a song is playing, so a remember keyed on the list would reshuffle the
     * row under the user mid-listen. Deciding once per ViewModel means the pick is stable for the
     * whole visit, including navigating away and back, and only re-rolls on a fresh app start.
     *
     * The chosen ids are kept and only re-rolled when one of them drops out of the pool entirely,
     * so ordinary play-time updates re-resolve the same three songs instead of replacing them.
     */
    private val _playItAgain: MutableStateFlow<List<SongEntity>> = MutableStateFlow(emptyList())
    val playItAgain: StateFlow<List<SongEntity>> = _playItAgain
    private var playItAgainIds: List<String> = emptyList()

    private fun refreshPlayItAgain(pool: List<SongEntity>) {
        // distinct first: a song can be both most played and liked, and it must not fill two of the
        // three slots.
        val unique = pool.distinctBy { it.videoId }
        if (unique.isEmpty()) {
            playItAgainIds = emptyList()
            _playItAgain.value = emptyList()
            return
        }
        // The count has to be part of the validity test, not just whether the ids still resolve.
        //
        // Both source flows start empty, so combine fires once with an empty pool, then again the
        // moment the first song arrives. That early emission picked a single id, and from then on
        // every later emission saw "my one id is still in the pool" and treated the pick as good,
        // so the row stayed stuck at one song no matter how many turned up afterwards.
        val wanted = minOf(PLAY_IT_AGAIN_COUNT, unique.size)
        val stillValid =
            playItAgainIds.size == wanted && playItAgainIds.all { id -> unique.any { it.videoId == id } }
        if (!stillValid) {
            playItAgainIds = unique.shuffled().take(wanted).map { it.videoId }
        }
        _playItAgain.value = playItAgainIds.mapNotNull { id -> unique.firstOrNull { it.videoId == id } }
    }

    private val _liked: MutableStateFlow<List<SongEntity>> = MutableStateFlow(emptyList())
    val liked: StateFlow<List<SongEntity>> = _liked

    private val _downloaded: MutableStateFlow<List<SongEntity>> = MutableStateFlow(emptyList())
    val downloaded: StateFlow<List<SongEntity>> = _downloaded

    /**
     * Recently added songs, newest first (the DAO orders getAllSongs by inLibrary DESC).
     *
     * Capped at 50 because this only feeds a count on a shortcut tile and the head of the list for
     * the resume card. The screen behind the tile does its own, unbounded query.
     */
    private val _recent: MutableStateFlow<List<SongEntity>> = MutableStateFlow(emptyList())
    val recent: StateFlow<List<SongEntity>> = _recent

    /**
     * The track the resume card offers.
     *
     * Driven by the saved playback position, not by the library list. Two things were wrong with
     * reading the head of [recent]: `getAllSongs` is a suspend DAO query the repository wraps in
     * `flow { emit(...) }`, so it emits once and never again, leaving the card frozen on whatever was
     * newest when Home first composed; and "newest row in the library" is not the same idea as "what
     * you were listening to", so it could name a track the user never actually played last.
     *
     * `recentMediaId` is written by the player as playback moves, and `getSongAsFlow` is a real Room
     * Flow, so this now tracks reality and updates live.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val resumeSong: StateFlow<SongEntity?> =
        combine(
            dataStoreManager.recentMediaId,
            songRepository.getRecentSongsAsFlow(1),
        ) { savedId, newest ->
            // recentMediaId is only ever written when the "save recent song and queue" setting is on,
            // and that setting is off by default. Reading it alone therefore gave a card that could
            // never appear on a default install, which is exactly what happened. The newest track in
            // the library is the honest fallback: it is what the user last played, just without an
            // exact position to resume from.
            savedId.takeIf { it.isNotBlank() } ?: newest.firstOrNull()?.videoId
        }.distinctUntilChanged()
            .flatMapLatest { id ->
                if (id.isNullOrBlank()) flowOf(null) else songRepository.getSongAsFlow(id)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /**
     * A "More like X" shelf.
     *
     * Seeded from local listening rather than the remote feed, so the shelves follow what the user
     * actually plays. [seedChannelId] is set only on the artist shelf, and it is what lets the full
     * list screen re-run the same artist radio instead of a song's related list.
     */
    data class HomeRecommendation(
        val seedName: String,
        val seedVideoId: String,
        val seedChannelId: String? = null,
        val tracks: List<Track> = emptyList(),
    )

    private val _recommendations: MutableStateFlow<List<HomeRecommendation>> = MutableStateFlow(emptyList())
    val recommendations: StateFlow<List<HomeRecommendation>> = _recommendations

    private val _shelfIndex: MutableStateFlow<Int> = MutableStateFlow(0)

    /**
     * The one recommendation shelf on screen right now.
     *
     * Four stacked "More like" rows pushed the actual feed off the bottom of Home and read as
     * repetition, since they share a card style and differ only by heading. Showing one and cycling
     * keeps every seed reachable without spending four screens on them.
     *
     * The modulo is against the current list size rather than a stored maximum, so the index stays
     * valid when shelves finish loading at different times and the list grows underneath it.
     */
    val visibleShelf: StateFlow<HomeRecommendation?> =
        combine(_recommendations, _shelfIndex) { shelves, index ->
            if (shelves.isEmpty()) null else shelves[index % shelves.size]
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private fun rotateShelves() {
        viewModelScope.launch {
            while (true) {
                delay(SHELF_ROTATION_MS)
                // Nothing to rotate through with one shelf, and advancing the index anyway would
                // restart its animation for no visible change.
                if (_recommendations.value.size > 1) {
                    _shelfIndex.update { it + 1 }
                }
            }
        }
    }

    /** How many song shelves to build. Each one costs a network round trip on Home. */
    private val songShelfCount = 3

    /**
     * Rebuilds every shelf from the current library.
     *
     * The artist shelf uses the artist's own radio, not the related list of one of their songs. That
     * distinction is the whole reason it can coexist with the song shelves: seeding it from a track
     * the user already has would return a list nearly identical to that track's own shelf.
     *
     * Every shelf is fetched in its own coroutine and its own runCatching. That is not defensive
     * habit, it is the fix for a real failure: these repository flows are written as
     * `flow { runCatching { api().onSuccess { emit() }.onFailure { emit() } } }`, so when the call
     * itself throws rather than returning a failed Result, the flow completes having emitted
     * nothing. `first()` on an empty flow throws, and because the shelves were built in one
     * sequential loop that exception killed the coroutine and every shelf still queued behind it.
     * One bad response left a single shelf on screen and no error anywhere.
     */
    private suspend fun buildRecommendations() = coroutineScope {
                val library = (_mostPlayed.value + _liked.value).distinctBy { it.videoId }
                if (library.isEmpty()) return@coroutineScope
                val topArtist = _topArtists.value.firstOrNull()

                val artistShelf =
                    async {
                        runCatching {
                            val artist = topArtist ?: return@runCatching null
                            val radio =
                                artistRepository
                                    .getArtistData(artist.channelId)
                                    .firstOrNull()
                                    ?.data
                                    ?.radioId ?: return@runCatching null
                            val tracks =
                                songRepository
                                    .getRadioFromEndpoint(radio)
                                    .firstOrNull()
                                    ?.data
                                    ?.first
                                    .orEmpty()
                            if (tracks.isEmpty()) {
                                null
                            } else {
                                HomeRecommendation(
                                    seedName = artist.name,
                                    seedVideoId = tracks.first().videoId,
                                    seedChannelId = artist.channelId,
                                    tracks = tracks,
                                )
                            }
                        }.onFailure { Logger.w(TAG, "Artist shelf failed: ${it.message}") }
                            .getOrNull()
                    }

                val songShelves =
                    library.take(songShelfCount).map { song ->
                        async {
                            runCatching {
                                val tracks =
                                    songRepository
                                        .getRelatedData(song.videoId)
                                        .firstOrNull()
                                        ?.data
                                        ?.first
                                        .orEmpty()
                                if (tracks.isEmpty()) {
                                    null
                                } else {
                                    HomeRecommendation(
                                        seedName = song.title,
                                        seedVideoId = song.videoId,
                                        tracks = tracks,
                                    )
                                }
                            }.onFailure { Logger.w(TAG, "Shelf for ${song.title} failed: ${it.message}") }
                                .getOrNull()
                        }
                    }

                // Artist first, then songs in library order, so the row order is stable between
                // launches instead of following whichever request happened to answer first.
                _recommendations.value =
                    (listOf(artistShelf) + songShelves)
                        .awaitAll()
                        .filterNotNull()
    }

    /** Plays a track from one of the recommendation shelves, queueing that whole shelf behind it. */
    fun playRecommendation(
        videoId: String,
        shelf: List<Track>,
    ) {
        val track = shelf.find { it.videoId == videoId } ?: return
        setQueueData(
            QueueData.Data(
                listTracks = ArrayList(shelf),
                firstPlayedTrack = track,
                playlistId = null,
                playlistName = "More like ${track.title}",
                playlistType = PlaylistType.RADIO,
                continuation = null,
            ),
        )
        loadMediaItem(
            track,
            Config.PLAYLIST_CLICK,
            shelf.indexOf(track).coerceAtLeast(0),
        )
    }

    /**
     * Saved playback position for [resumeSong], in milliseconds.
     *
     * Same source the player itself restores from, so the card, the time it shows and the point it
     * resumes at can never disagree with each other.
     */
    val resumePositionMs: StateFlow<Long> =
        dataStoreManager.recentPosition
            .map { it.toLongOrNull() ?: 0L }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    /**
     * True once the user has actually started playing something in this run of the app.
     *
     * This is the resume card's visibility switch, and it is deliberately NOT "is a track loaded".
     * That was the first attempt and it made the card unreachable: with save-and-restore enabled the
     * app rebuilds the previous queue at launch, so an item is loaded from the first frame and the
     * card hid itself permanently.
     *
     * Latching on the first play instead gives the behaviour the card is named after. Reopen the app
     * cold and it is there, offering the track you left and the time remaining. Press play on
     * anything and it retires for the session. It never toggles back on a pause, so it cannot flicker
     * against a mini player that is still on screen.
     */
    private val _hasPlayedThisSession: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val hasPlayedThisSession: StateFlow<Boolean> = _hasPlayedThisSession

    private fun observePlaybackStart() {
        viewModelScope.launch {
            mediaPlayerHandler.controlState.collect { state ->
                if (state.isPlaying) _hasPlayedThisSession.value = true
            }
        }
        // A loaded track retires the card too, not only a playing one.
        //
        // The mini player appears as soon as a track is loaded, playing or not. Keying the card
        // purely on "has played" meant a restored-but-paused session showed both at once: the card
        // offering to resume the very track the mini player was already sitting on, saying the same
        // thing twice in one screen.
        //
        // Still monotonic, so the original flicker concern does not return: this only ever moves
        // from false to true, and a pause can never bring the card back mid session.
        viewModelScope.launch {
            mediaPlayerHandler.nowPlaying.collect { item ->
                if (item != null) _hasPlayedThisSession.value = true
            }
        }
    }

    /**
     * Resumes the card's track.
     *
     * Two paths, because the handler's restore is not always available. `mayBeRestoreQueue()` is the
     * better one when it works: it rebuilds the saved queue and seeks to the stored position. But its
     * entire body sits behind `if (saveRecentSongAndQueue == TRUE)`, and that setting is off by
     * default, so calling it on a default install silently did nothing at all. That is why tapping
     * the card played no music.
     *
     * With the setting off there is no stored position to honour anyway, so the fallback simply plays
     * the track from the start.
     */
    fun resumePlayback() {
        viewModelScope.launch {
            val song = resumeSong.value ?: return@launch
            if (dataStoreManager.saveRecentSongAndQueue.first() == DataStoreManager.Values.TRUE) {
                mediaPlayerHandler.mayBeRestoreQueue()
            } else {
                playLocal(song.videoId, HomeLocalSection.Resume)
            }
        }
    }

    /**
     * Signs out of YouTube.
     *
     * Mirrors SettingsViewModel.logOutAllYouTube for the account menu in the Home header, so the two
     * routes leave the app in the same state rather than one clearing less than the other.
     */
    fun logOutYouTube() {
        viewModelScope.launch {
            dataStoreManager.putString("AccountName", "")
            dataStoreManager.putString("AccountThumbUrl", "")
            dataStoreManager.setLoggedIn(false)
            dataStoreManager.setCookie("", null)
            // Refetch, or Home is left showing "Can't connect" over a working connection.
            //
            // The feed in memory was fetched with the old cookie. Clearing it does not invalidate
            // that list or trigger a reload, so the next request fails against the now stale
            // credentials and homeData ends up empty, which is the same state the offline block keys
            // on. The short delay lets the DataStore write land before the request reads it.
            delay(500)
            getHomeItemList(_params.value)
        }
    }

    private fun observeLocalLibrary() {
        rotateShelves()
        observePlaybackStart()
        viewModelScope.launch {
            songRepository.getMostPlayedSongs().collectLatest { songs ->
                _mostPlayed.value = songs.sortedByDescending { it.totalPlayTime }
            }
        }
        viewModelScope.launch {
            songRepository.getLikedSongs().collectLatest { songs ->
                _liked.value = songs
            }
        }
        viewModelScope.launch {
            songRepository.getDownloadedSongs().collectLatest { songs ->
                _downloaded.value = songs ?: emptyList()
            }
        }
        viewModelScope.launch {
            // Observed rather than the suspend getAllSongs, so the Recently count follows the library.
            // Additions, not plays. The tile used to count songs with play time while the screen
            // it opens lists everything added, so it read "2" over a list of ninety six.
            songRepository.getAllSongsAsFlow(50).collectLatest { songs ->
                _recent.value = songs
            }
        }
        viewModelScope.launch {
            // Waits for real content before asking YouTube for anything: on a fresh install both
            // lists are empty and there is nothing to be "more like".
            combine(_mostPlayed, _liked) { played, liked ->
                // Most played first, topped up with liked songs so the row still fills on a fresh
                // install where nothing has accumulated play time yet.
                played + liked
            }.collect { pool -> refreshPlayItAgain(pool) }
        }
        viewModelScope.launch {
            combine(_mostPlayed, _liked, _topArtists) { played, liked, artists ->
                // Keyed on the seeds themselves, not just the first one. The old key was the first
                // video id, so it stopped changing after the very first song arrived and later
                // emissions still restarted the fetch without ever being recognised as different.
                val songSeeds =
                    (played + liked)
                        .distinctBy { it.videoId }
                        .take(songShelfCount)
                        .map { it.videoId }
                songSeeds to artists.firstOrNull()?.channelId
            }.distinctUntilChanged()
                // The database and the artist query each emit several times while they settle after
                // launch. Without this the build was cancelled and restarted on every one of those
                // emissions, and the only result that ever reached the screen came from an early run
                // that finished while the library still held a single song and no artists at all.
                .debounce(700)
                .collectLatest { (songSeeds, _) ->
                    if (songSeeds.isNotEmpty()) buildRecommendations()
                }
        }
        viewModelScope.launch {
            combine(
                artistRepository.getFollowedArtists(),
                artistRepository.getAllArtists(30),
            ) { followed, all ->
                // Two passes, because an artist has two different identities here.
                //
                // channelId is the row's primary key, so distinctBy on it can never collapse
                // anything the database already stores separately. YouTube publishes an artist
                // channel AND an auto generated "Topic" channel for the same person, and this
                // library held both for Karan Aujla: same name, same picture, two ids. To a
                // listener that is one artist appearing twice.
                //
                // So dedupe by name as well, and keep the best row of each group: a followed
                // artist first, then one that actually has a picture. groupBy preserves insertion
                // order, so followed artists stay at the front of the row.
                (followed + all)
                    .distinctBy { it.channelId }
                    .groupBy { it.name.trim().lowercase() }
                    .values
                    .mapNotNull { sameName ->
                        sameName.firstOrNull { it.followed }
                            ?: sameName.firstOrNull { !it.thumbnails.isNullOrBlank() }
                            ?: sameName.firstOrNull()
                    }
            }.collectLatest { artists ->
                _topArtists.value = artists
            }
        }
    }

    /**
     * Play [videoId] from one of the local Home sections, queueing the whole section behind it.
     *
     * Same shape as the Library screens use: publish the queue first, then hand the player the track
     * and its index, so next/previous walk the list the user was looking at rather than a radio.
     */
    fun playLocal(
        videoId: String,
        source: HomeLocalSection,
    ) {
        val list =
            when (source) {
                HomeLocalSection.MostPlayed -> mostPlayed.value
                HomeLocalSection.Liked -> liked.value
                HomeLocalSection.Downloaded -> downloaded.value
                HomeLocalSection.Resume -> listOfNotNull(resumeSong.value)
            }
        val track = list.find { it.videoId == videoId } ?: return
        setQueueData(
            QueueData.Data(
                listTracks = list.toArrayListTrack(),
                firstPlayedTrack = track.toTrack(),
                playlistId = null,
                playlistName = source.label,
                playlistType = PlaylistType.RADIO,
                continuation = null,
            ),
        )
        loadMediaItem(
            track.toTrack(),
            Config.PLAYLIST_CLICK,
            list.indexOf(track).coerceAtLeast(0),
        )
    }

    private val _homeItemList: MutableStateFlow<List<HomeItem>> =
        MutableStateFlow(arrayListOf())
    val homeItemList: StateFlow<List<HomeItem>> = _homeItemList

    private var _homeListState = MutableStateFlow<ListState>(ListState.IDLE)
    val homeListState: StateFlow<ListState> = _homeListState

    private var _continuation = MutableStateFlow<String?>(null)
    val continuation: StateFlow<String?> = _continuation

    private val _exploreMoodItem: MutableStateFlow<Mood?> = MutableStateFlow(null)
    val exploreMoodItem: StateFlow<Mood?> = _exploreMoodItem
    private val _accountInfo: MutableStateFlow<Pair<String?, String?>?> = MutableStateFlow(null)
    val accountInfo: StateFlow<Pair<String?, String?>?> = _accountInfo

    private var homeJob: Job? = null

    val showSnackBarErrorState = MutableSharedFlow<String>()

    private val _chart: MutableStateFlow<Chart?> = MutableStateFlow(null)
    val chart: StateFlow<Chart?> = _chart
    private val _newRelease: MutableStateFlow<List<HomeItem>> = MutableStateFlow(arrayListOf())
    val newRelease: StateFlow<List<HomeItem>> = _newRelease
    var regionCodeChart: MutableStateFlow<String?> = MutableStateFlow(null)

    val loading = MutableStateFlow<Boolean>(true)
    val loadingChart = MutableStateFlow<Boolean>(true)
    private var regionCode: String = ""
    private var language: String = ""

    private val _songEntity: MutableStateFlow<SongEntity?> = MutableStateFlow(null)
    val songEntity: StateFlow<SongEntity?> = _songEntity

    private var _params: MutableStateFlow<String?> = MutableStateFlow(null)
    val params: StateFlow<String?> = _params

    // For showing alert that should log in to YouTube
    private val _showLogInAlert: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showLogInAlert: StateFlow<Boolean> = _showLogInAlert

    val dataSyncId =
        dataStoreManager
            .dataSyncId
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    private val _mainHomeThumbnail: MutableStateFlow<String?> = MutableStateFlow(null)
    val mainHomeThumbnail: StateFlow<String?> = _mainHomeThumbnail

    init {
        if (runBlocking { dataStoreManager.cookie.first() }.isEmpty() &&
            runBlocking {
                dataStoreManager.shouldShowLogInRequiredAlert.first() == TRUE
            }
        ) {
            _showLogInAlert.update { true }
        }
        homeJob = Job()
        observeLocalLibrary()
        // Retry from the app wide offline bar, which sits outside this screen's composition.
        viewModelScope.launch {
            remoteFeedHealth.retries.collect { getHomeItemList(params.value) }
        }
        viewModelScope.launch {
            regionCodeChart.value = dataStoreManager.chartKey.first()
            exploreChart(regionCodeChart.value ?: "ZZ")
            language = dataStoreManager.getString(SELECTED_LANGUAGE).first()
                ?: SUPPORTED_LANGUAGE.codes.first()
            //  refresh when region change
            val job1 =
                launch {
                    dataStoreManager.location.distinctUntilChanged().collect {
                        regionCode = it
                        getHomeItemList(params.value)
                    }
                }
            //  refresh when language change
            val job2 =
                launch {
                    dataStoreManager.language.distinctUntilChanged().collect {
                        language = it
                        getHomeItemList(params.value)
                    }
                }
            val job3 =
                launch {
                    dataStoreManager.cookie.distinctUntilChanged().collect {
                        getHomeItemList(params.value)
                        _accountInfo.emit(
                            Pair(
                                dataStoreManager.getString("AccountName").first(),
                                dataStoreManager.getString("AccountThumbUrl").first(),
                            ),
                        )
                    }
                }
            val job4 =
                launch {
                    params.collectLatest {
                        getHomeItemList(it)
                    }
                }
            val job5 =
                launch {
                    dataStoreManager
                        .cookie
                        .distinctUntilChanged()
                        .collectLatest {
                            if (it.isNotEmpty()) {
                                Logger.w(tag, "Cookie changed, refreshing home")
                                loading.value = true
                                delay(1000) // To wait for the cookie to be saved properly
                                getHomeItemList(params.value)
                            }
                        }
                }
            val job6 =
                launch {
                    homeItemList.collectLatest { list ->
                        _mainHomeThumbnail.value =
                            list
                                .firstOrNull()
                                ?.contents
                                ?.firstOrNull()
                                ?.thumbnails
                                ?.lastOrNull()
                                ?.url
                    }
                }
            job1.join()
            job2.join()
            job3.join()
            job4.join()
            job5.join()
            job6.join()
        }
    }

    fun doneShowLogInAlert(neverShowAgain: Boolean = false) {
        viewModelScope.launch {
            _showLogInAlert.update { false }
            if (neverShowAgain) {
                dataStoreManager.setShouldShowLogInRequiredAlert(false)
            }
        }
    }

    fun getHomeItemList(params: String? = null) {
        loading.value = true
        _homeListState.value = ListState.LOADING
        language =
            runBlocking {
                dataStoreManager.getString(SELECTED_LANGUAGE).first()
                    ?: SUPPORTED_LANGUAGE.codes.first()
            }
        regionCode = runBlocking { dataStoreManager.location.first() }
        homeJob?.cancel()
        homeJob =
            viewModelScope.launch {
                combine(
                    homeRepository.getHomeData(
                        params,
                        getString(Res.string.view_count),
                        getString(Res.string.song),
                    ),
                    homeRepository.getMoodAndMomentsData(),
                    homeRepository.getChartData(dataStoreManager.chartKey.first()),
                    homeRepository.getNewRelease(
                        getString(Res.string.new_release),
                        getString(Res.string.music_video),
                    ),
                ) { home, exploreMood, exploreChart, newRelease ->
                    HomeDataCombine(home, exploreMood, exploreChart, newRelease)
                }.collect { result ->
                    val home = result.home
                    Logger.d("home size", "${home.data?.second?.size}")
                    val exploreMoodItem = result.mood
                    val chart = result.chart
                    val newRelease = result.newRelease
                    when (home) {
                        is Resource.Success -> {
                            _continuation.value = home.data?.first
                            _homeItemList.value = home.data?.second ?: listOf()
                        }

                        // A failed refresh must not erase a good feed.
                        //
                        // Any non-Success used to blank this list, and Home reads an empty list as
                        // being offline, so one transient error (a 403, a rate limit, a parse miss
                        // on a single shelf) replaced a screen full of content with "Can't connect"
                        // while the network was perfectly healthy. Nothing here ever checked the
                        // network, so that message was always an assumption rather than a finding.
                        // Keeping the last good shelves makes a failure cost freshness instead of
                        // the whole screen.
                        else -> {
                            _continuation.value = null
                        }
                    }
                    if (continuation.value.isNullOrEmpty()) {
                        _homeListState.value = ListState.PAGINATION_EXHAUST
                    } else {
                        _homeListState.value = ListState.IDLE
                    }
                    when (chart) {
                        is Resource.Success -> {
                            _chart.value = chart.data
                        }

                        else -> {
                            // Keep the last good chart, as above.
                        }
                    }
                    when (newRelease) {
                        is Resource.Success -> {
                            _newRelease.value = newRelease.data ?: arrayListOf()
                        }

                        else -> {
                            // Keep the last good releases, as above.
                        }
                    }
                    when (exploreMoodItem) {
                        is Resource.Success -> {
                            _exploreMoodItem.value = exploreMoodItem.data
                        }

                        else -> {
                            // Keep the last good moods, as above.
                        }
                    }
                    // Every network-only feed failing, which is as close to "offline" as anything
                    // here can honestly get.
                    //
                    // Mood is deliberately excluded. getMoodAndMomentsData emits its DataStore cache
                    // before it touches the network, so it reports Success with no connection at
                    // all. Including it made a total failure impossible once that cache existed,
                    // and the bar never appeared. The other three have no cache and only succeed
                    // when a request actually returned.
                    remoteFeedHealth.report(
                        home !is Resource.Success &&
                            chart !is Resource.Success &&
                            newRelease !is Resource.Success,
                    )
                    regionCodeChart.value = dataStoreManager.chartKey.first()
                    Logger.d("HomeViewModel", "getHomeItemList: $result")
                    dataStoreManager.cookie.first().let {
                        if (it != "") {
                            _accountInfo.emit(
                                Pair(
                                    dataStoreManager.getString("AccountName").first(),
                                    dataStoreManager.getString("AccountThumbUrl").first(),
                                ),
                            )
                        }
                    }
                    when {
                        home is Resource.Error -> home.message
                        exploreMoodItem is Resource.Error -> exploreMoodItem.message
                        chart is Resource.Error -> chart.message
                        else -> null
                    }?.let {
                        showSnackBarErrorState.emit(it)
                        Logger.w("Error", "getHomeItemList: ${home.message}")
                        Logger.w("Error", "getHomeItemList: ${exploreMoodItem.message}")
                        Logger.w("Error", "getHomeItemList: ${chart.message}")
                    }
                    loading.value = false
                }
            }
    }

    fun getContinueHomeItem(continuation: String?) {
        viewModelScope.launch {
            if (continuation.isNullOrEmpty()) {
                _homeListState.value = ListState.PAGINATION_EXHAUST
                return@launch
            } else {
                log("Get more home item with continuation: $continuation")
                _homeListState.value = ListState.PAGINATING
                homeRepository
                    .getHomeDataContinue(
                        continuation,
                        getString(Res.string.view_count),
                        getString(Res.string.song),
                    ).collect { home ->
                        when (home) {
                            is Resource.Success -> {
                                _continuation.value = home.data?.first
                                val newItems = home.data?.second ?: listOf()
                                _homeItemList.update { it + newItems }
                                if (home.data?.first.isNullOrEmpty()) {
                                    _homeListState.value = ListState.PAGINATION_EXHAUST
                                } else {
                                    _homeListState.value = ListState.IDLE
                                }
                            }

                            is Resource.Error -> {
                                _continuation.value = null
                                Logger.w(tag, "getContinueHomeItem: ${home.message}")
                                showSnackBarErrorState.emit(home.message ?: "Unknown error")
                                _homeListState.value = ListState.PAGINATION_EXHAUST
                            }
                        }
                    }
            }
        }
    }

    fun exploreChart(region: String) {
        viewModelScope.launch {
            loadingChart.value = true
            homeRepository
                .getChartData(
                    region,
                ).collect { values ->
                    regionCodeChart.value = region
                    dataStoreManager.setChartKey(region)
                    when (values) {
                        is Resource.Success -> {
                            _chart.value = values.data
                        }

                        else -> {
                            _chart.value = null
                        }
                    }
                    loadingChart.value = false
                }
        }
    }

    fun setParams(params: String?) {
        _params.value = params
    }

    override fun onCleared() {
        super.onCleared()
        homeJob?.cancel()
    }

    companion object {
        // Home params
        const val HOME_PARAMS_RELAX = "ggM8SgQIBxADSgQIBRABSgQICRABSgQIChABSgQIDRABSgQICBABSgQIBBABSgQIDhABSgQIAxABSgQIBhAB"
        const val HOME_PARAMS_SLEEP = "ggM8SgQIBxABSgQIBRADSgQICRABSgQIChABSgQIDRABSgQICBABSgQIBBABSgQIDhABSgQIAxABSgQIBhAB"
        const val HOME_PARAMS_ENERGIZE = "ggM8SgQIBxABSgQIBRABSgQICRADSgQIChABSgQIDRABSgQICBABSgQIBBABSgQIDhABSgQIAxABSgQIBhAB"
        const val HOME_PARAMS_SAD = "ggM8SgQIBxABSgQIBRABSgQICRABSgQIChADSgQIDRABSgQICBABSgQIBBABSgQIDhABSgQIAxABSgQIBhAB"
        const val HOME_PARAMS_ROMANCE = "ggM8SgQIBxABSgQIBRABSgQICRABSgQIChABSgQIDRADSgQICBABSgQIBBABSgQIDhABSgQIAxABSgQIBhAB"
        const val HOME_PARAMS_FEEL_GOOD = "ggM8SgQIBxABSgQIBRABSgQICRABSgQIChABSgQIDRABSgQICBADSgQIBBABSgQIDhABSgQIAxABSgQIBhAB"
        const val HOME_PARAMS_WORKOUT = "ggM8SgQIBxABSgQIBRABSgQICRABSgQIChABSgQIDRABSgQICBABSgQIBBADSgQIDhABSgQIAxABSgQIBhAB"
        const val HOME_PARAMS_PARTY = "ggM8SgQIBxABSgQIBRABSgQICRABSgQIChABSgQIDRABSgQICBABSgQIBBABSgQIDhADSgQIAxABSgQIBhAB"
        const val HOME_PARAMS_COMMUTE = "ggM8SgQIBxABSgQIBRABSgQICRABSgQIChABSgQIDRABSgQICBABSgQIBBABSgQIDhABSgQIAxADSgQIBhAB"
        const val HOME_PARAMS_FOCUS = "ggM8SgQIBxABSgQIBRABSgQICRABSgQIChABSgQIDRABSgQICBABSgQIBBABSgQIDhABSgQIAxABSgQIBhAD"
    }
}

/** Which local Home section a play request came from, so the right list gets queued. */
enum class HomeLocalSection(
    val label: String,
) {
    Resume("Continue"),
    MostPlayed("Your rotation"),
    Liked("Liked"),
    Downloaded("Offline"),
}
