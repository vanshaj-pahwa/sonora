package app.vanshajpahwa.sonora.ui.screen.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import app.vanshajpahwa.sonora.ui.component.ListControlsRow
import app.vanshajpahwa.sonora.ui.component.ListSort
import app.vanshajpahwa.sonora.ui.component.SongGridItem
import app.vanshajpahwa.sonora.resources.custom_order
import app.vanshajpahwa.sonora.resources.title
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.domain.data.entities.ArtistEntity
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.domain.mediaservice.handler.PlaylistType
import app.vanshajpahwa.domain.mediaservice.handler.QueueData
import app.vanshajpahwa.domain.utils.LocalResource
import app.vanshajpahwa.domain.utils.toArrayListTrack
import app.vanshajpahwa.domain.utils.toTrack
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.extension.getStringBlocking
import app.vanshajpahwa.sonora.ui.component.ArtistFullWidthItems
import app.vanshajpahwa.sonora.ui.component.EndOfPage
import app.vanshajpahwa.sonora.ui.component.NowPlayingBottomSheet
import app.vanshajpahwa.sonora.ui.component.PlaylistFullWidthItems
import app.vanshajpahwa.sonora.ui.component.RippleIconButton
import app.vanshajpahwa.sonora.ui.component.SongFullWidthItems
import app.vanshajpahwa.sonora.ui.icon.ArrowBackIosNew
import app.vanshajpahwa.sonora.ui.icon.Close
import app.vanshajpahwa.sonora.ui.icon.PlayCircle
import app.vanshajpahwa.sonora.ui.icon.Search
import app.vanshajpahwa.sonora.ui.icon.Shuffle
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.navigation.destination.list.AlbumDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.ArtistDestination
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.AnalyticsViewModel
import app.vanshajpahwa.sonora.viewModel.LibraryDynamicPlaylistViewModel
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.ui.component.LibraryEmptyState
import app.vanshajpahwa.sonora.resources.empty_favorite
import app.vanshajpahwa.sonora.resources.empty_followed
import app.vanshajpahwa.sonora.resources.empty_most_played
import app.vanshajpahwa.sonora.resources.empty_downloaded
import app.vanshajpahwa.sonora.resources.empty_top_tracks
import app.vanshajpahwa.sonora.resources.empty_top_artists
import app.vanshajpahwa.sonora.resources.empty_top_albums
import app.vanshajpahwa.sonora.resources.album_length
import app.vanshajpahwa.sonora.resources.artists
import app.vanshajpahwa.sonora.resources.downloaded
import app.vanshajpahwa.sonora.resources.favorite
import app.vanshajpahwa.sonora.resources.followed
import app.vanshajpahwa.sonora.resources.lower_plays
import app.vanshajpahwa.sonora.resources.most_played
import app.vanshajpahwa.sonora.resources.no_results_found
import app.vanshajpahwa.sonora.resources.search
import app.vanshajpahwa.sonora.resources.seconds
import app.vanshajpahwa.sonora.resources.your_top_albums
import app.vanshajpahwa.sonora.resources.your_top_artists
import app.vanshajpahwa.sonora.resources.your_top_tracks

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
@ExperimentalMaterial3Api
fun LibraryDynamicPlaylistScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    type: String,
    viewModel: LibraryDynamicPlaylistViewModel = koinViewModel(),
    analyticsViewModel: AnalyticsViewModel = koinViewModel(),
    sharedViewModel: SharedViewModel = koinInject(),
) {
    val nowPlayingVideoId by viewModel.nowPlayingVideoId.collectAsStateWithLifecycle()

    var chosenSong: SongEntity? by remember { mutableStateOf(null) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showSearchBar by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    // Session-scoped on purpose: rememberSaveable survives rotation but resets on a fresh visit.
    // Making the choice durable would mean a new DataStoreManager key, which is a domain-interface
    // change and deserves its own step.
    var listSort by rememberSaveable { mutableStateOf(ListSort.Default) }
    var isGridLayout by rememberSaveable { mutableStateOf(false) }

    val favorite by viewModel.listFavoriteSong.collectAsStateWithLifecycle()
    var tempFavorite by remember { mutableStateOf(emptyList<SongEntity>()) }
    val followed by viewModel.listFollowedArtist.collectAsStateWithLifecycle()
    var tempFollowed by remember { mutableStateOf(emptyList<ArtistEntity>()) }
    val mostPlayed by viewModel.listMostPlayedSong.collectAsStateWithLifecycle()
    var tempMostPlayed by remember { mutableStateOf(emptyList<SongEntity>()) }
    val downloaded by viewModel.listDownloadedSong.collectAsStateWithLifecycle()
    var tempDownloaded by remember { mutableStateOf(emptyList<SongEntity>()) }
    val analyticsUIState by analyticsViewModel.analyticsUIState.collectAsStateWithLifecycle()
    var tempTopTracks by remember { mutableStateOf(analyticsUIState.topTracks.data ?: emptyList()) }
    var tempTopArtists by remember { mutableStateOf(analyticsUIState.topArtists.data ?: emptyList()) }
    var tempTopAlbums by remember { mutableStateOf(analyticsUIState.topAlbums.data ?: emptyList()) }
    val hazeState =
        rememberHazeState(
            blurEnabled = true,
        )

    LaunchedEffect(query) {
        Logger.w("LibraryDynamicPlaylistScreen", "Check query: $query")
        tempFavorite = favorite.filter { it.title.contains(query, ignoreCase = true) }
        Logger.w("LibraryDynamicPlaylistScreen", "Check tempFavorite: $tempFavorite")
        tempFollowed = followed.filter { it.name.contains(query, ignoreCase = true) }
        Logger.w("LibraryDynamicPlaylistScreen", "Check tempFollowed: $tempFollowed")
        tempMostPlayed = mostPlayed.filter { it.title.contains(query, ignoreCase = true) }
        Logger.w("LibraryDynamicPlaylistScreen", "Check tempMostPlayed: $tempMostPlayed")
        tempDownloaded = downloaded.filter { it.title.contains(query, ignoreCase = true) }
        Logger.w("LibraryDynamicPlaylistScreen", "Check tempDownloaded: $tempDownloaded")
        tempTopTracks =
            analyticsUIState.topTracks.data
                ?.filter { it.second.title.contains(query, ignoreCase = true) }
                ?: emptyList()
        Logger.w("LibraryDynamicPlaylistScreen", "Check tempTopTracks: $tempTopTracks")
        tempTopArtists =
            analyticsUIState.topArtists.data
                ?.filter { it.second.name.contains(query, ignoreCase = true) }
                ?: emptyList()
        Logger.w("LibraryDynamicPlaylistScreen", "Check tempTopArtists: $tempTopArtists")
        tempTopAlbums =
            analyticsUIState.topAlbums.data
                ?.filter { it.second.title.contains(query, ignoreCase = true) }
                ?: emptyList()
        Logger.w("LibraryDynamicPlaylistScreen", "Check tempTopAlbums: $tempTopAlbums")
    }

    LazyColumn(
        modifier = Modifier.hazeSource(hazeState),
        contentPadding = innerPadding,
    ) {
        item {
            Spacer(Modifier.height(64.dp))
        }
        item {
            AnimatedVisibility(showSearchBar) {
                Spacer(Modifier.height(55.dp))
            }
        }
        val type = LibraryDynamicPlaylistType.toType(type)
        // A page with nothing in it used to render as a bare gap under a "0 tracks" header, which
        // reads as a failure rather than an empty shelf. Each type says what would fill it, and a
        // search that matches nothing says so instead of claiming the library is empty.
        val isEmpty =
            when (type) {
                LibraryDynamicPlaylistType.Favorite -> favorite.isEmpty()
                LibraryDynamicPlaylistType.Followed -> followed.isEmpty()
                LibraryDynamicPlaylistType.MostPlayed -> mostPlayed.isEmpty()
                LibraryDynamicPlaylistType.Downloaded -> downloaded.isEmpty()
                LibraryDynamicPlaylistType.TopTracks -> analyticsUIState.topTracks.data.isNullOrEmpty()
                LibraryDynamicPlaylistType.TopArtists -> analyticsUIState.topArtists.data.isNullOrEmpty()
                LibraryDynamicPlaylistType.TopAlbums -> analyticsUIState.topAlbums.data.isNullOrEmpty()
            }
        val isSearchingWithNoHits =
            showSearchBar && query.isNotEmpty() &&
                when (type) {
                    LibraryDynamicPlaylistType.Favorite -> tempFavorite.isEmpty()
                    LibraryDynamicPlaylistType.Followed -> tempFollowed.isEmpty()
                    LibraryDynamicPlaylistType.MostPlayed -> tempMostPlayed.isEmpty()
                    LibraryDynamicPlaylistType.Downloaded -> tempDownloaded.isEmpty()
                    LibraryDynamicPlaylistType.TopTracks -> tempTopTracks.isEmpty()
                    LibraryDynamicPlaylistType.TopArtists -> tempTopArtists.isEmpty()
                    LibraryDynamicPlaylistType.TopAlbums -> tempTopAlbums.isEmpty()
                }
        if (isEmpty || isSearchingWithNoHits) {
            item {
                LibraryEmptyState(
                    message =
                        if (isSearchingWithNoHits && !isEmpty) {
                            stringResource(Res.string.no_results_found)
                        } else {
                            stringResource(type.emptyMessage())
                        },
                )
            }
        } else if (type == LibraryDynamicPlaylistType.Followed) {
            items(
                if (query.isNotEmpty() && showSearchBar) {
                    tempFollowed
                } else {
                    followed
                },
                key = { it.channelId },
            ) { artist ->
                ArtistFullWidthItems(
                    artist,
                    onClickListener = {
                        navController.navigate(
                            ArtistDestination(
                                channelId = artist.channelId,
                            ),
                        )
                    },
                )
            }
        } else if (type == LibraryDynamicPlaylistType.TopArtists) {
            when (analyticsUIState.topArtists) {
                is LocalResource.Success if (!analyticsUIState.topArtists.data.isNullOrEmpty()) -> {
                    val data = analyticsUIState.topArtists.data ?: emptyList()
                    items(
                        if (query.isNotEmpty() && showSearchBar) {
                            tempTopArtists
                        } else {
                            data
                        },
                        key = { it.first.hashCode() },
                    ) { artist ->
                        ArtistFullWidthItems(
                            artist.second,
                            rightView = {
                                Box(Modifier.padding(horizontal = 8.dp)) {
                                    Text(
                                        text = "${artist.first.playCount} ${stringResource(Res.string.lower_plays)}",
                                        style = typo().bodySmall,
                                    )
                                }
                            },
                            onClickListener = {
                                navController.navigate(
                                    ArtistDestination(
                                        channelId = artist.second.channelId,
                                    ),
                                )
                            },
                        )
                    }
                }

                else -> {}
            }
        } else if (type == LibraryDynamicPlaylistType.TopAlbums) {
            when (analyticsUIState.topAlbums) {
                is LocalResource.Success if (!analyticsUIState.topAlbums.data.isNullOrEmpty()) -> {
                    val data = analyticsUIState.topAlbums.data ?: emptyList()
                    items(
                        if (query.isNotEmpty() && showSearchBar) {
                            tempTopAlbums
                        } else {
                            data
                        },
                        key = { it.first.hashCode() },
                    ) { album ->
                        PlaylistFullWidthItems(
                            album.second,
                            rightView = {
                                Box(Modifier.padding(horizontal = 8.dp)) {
                                    Text(
                                        text = "${album.first.playCount} ${stringResource(Res.string.lower_plays)}",
                                        style = typo().bodySmall,
                                    )
                                }
                            },
                            onClickListener = {
                                navController.navigate(
                                    AlbumDestination(
                                        browseId = album.second.browseId,
                                    ),
                                )
                            },
                        )
                    }
                }

                else -> {}
            }
        } else if (type == LibraryDynamicPlaylistType.TopTracks) {
            when (analyticsUIState.topTracks) {
                is LocalResource.Success if (!analyticsUIState.topTracks.data.isNullOrEmpty()) -> {
                    val data = analyticsUIState.topTracks.data ?: emptyList()
                    items(
                        if (query.isNotEmpty() && showSearchBar) {
                            tempTopTracks
                        } else {
                            data
                        },
                        key = { it.hashCode() },
                    ) { song ->
                        SongFullWidthItems(
                            songEntity = song.second,
                            isPlaying = song.second.videoId == nowPlayingVideoId,
                            modifier = Modifier.fillMaxWidth(),
                            onMoreClickListener = {
                                chosenSong = song.second
                                showBottomSheet = true
                            },
                            onClickListener = { videoId ->
                                val targetList = data.map { it.second }
                                val playTrack = song.second
                                with(sharedViewModel) {
                                    setQueueData(
                                        QueueData.Data(
                                            listTracks = targetList.toArrayListTrack(),
                                            firstPlayedTrack = playTrack.toTrack(),
                                            playlistId = null,
                                            playlistName = getStringBlocking(Res.string.your_top_tracks),
                                            playlistType = PlaylistType.RADIO,
                                            continuation = null,
                                        ),
                                    )
                                    loadMediaItem(
                                        playTrack.toTrack(),
                                        Config.PLAYLIST_CLICK,
                                        targetList.indexOf(playTrack).coerceAtLeast(0),
                                    )
                                }
                            },
                            onAddToQueue = {
                                sharedViewModel.addListToQueue(
                                    arrayListOf(song.second.toTrack()),
                                )
                            },
                            rightView = {
                                Column(
                                    modifier = Modifier.wrapContentWidth(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = "${song.first.totalListeningTime} ${stringResource(Res.string.seconds)}",
                                        style = typo().bodySmall,
                                    )
                                    Text(
                                        text = "${song.first.playCount} ${stringResource(Res.string.lower_plays)}",
                                        style = typo().bodySmall,
                                    )
                                }
                            },
                        )
                    }
                }

                else -> {}
            }
        } else {
            val songs =
                when (type) {
                    LibraryDynamicPlaylistType.Downloaded -> {
                        if (query.isNotEmpty() && showSearchBar) {
                            tempDownloaded
                        } else {
                            downloaded
                        }
                    }

                    LibraryDynamicPlaylistType.Favorite -> {
                        if (query.isNotEmpty() && showSearchBar) {
                            tempFavorite
                        } else {
                            favorite
                        }
                    }

                    LibraryDynamicPlaylistType.MostPlayed -> {
                        if (query.isNotEmpty() && showSearchBar) {
                            tempMostPlayed
                        } else {
                            mostPlayed
                        }
                    }
                }
            // Default keeps whatever order the page already means: play count for Most Played,
            // recency for Downloaded. Sorting is an override, not a replacement for that meaning.
            val sorted =
                when (listSort) {
                    ListSort.Default -> songs
                    ListSort.Title -> songs.sortedBy { it.title.lowercase() }
                    ListSort.Artist ->
                        songs.sortedBy {
                            it.artistName?.firstOrNull()?.lowercase().orEmpty()
                        }
                }
            item {
                ListControlsRow(
                    sort = listSort,
                    onSortChange = { listSort = it },
                    isGrid = isGridLayout,
                    onToggleLayout = { isGridLayout = !isGridLayout },
                    defaultSortLabel = stringResource(Res.string.custom_order),
                    titleLabel = stringResource(Res.string.title),
                    artistLabel = stringResource(Res.string.artists),
                )
            }
            if (isGridLayout) {
                // Chunked into pairs inside the existing LazyColumn rather than swapping in a
                // LazyVerticalGrid: the column already carries the search spacer, empty state and
                // end-of-page credit, and a second scrolling container would have to reproduce
                // all three.
                items(
                    sorted.chunked(2),
                    key = { pair -> pair.first().videoId },
                ) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        pair.forEach { song ->
                            SongGridItem(
                                song = song,
                                isPlaying = song.videoId == nowPlayingVideoId,
                                onClick = { viewModel.playSong(song.videoId, type = type) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        // Keeps a lone trailing tile at half width instead of stretching it.
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            } else {
            items(
                sorted,
                key = { it.hashCode() },
            ) { song ->
                SongFullWidthItems(
                    songEntity = song,
                    isPlaying = song.videoId == nowPlayingVideoId,
                    modifier = Modifier.fillMaxWidth(),
                    onMoreClickListener = {
                        chosenSong = song
                        showBottomSheet = true
                    },
                    onClickListener = { videoId ->
                        viewModel.playSong(videoId, type = type)
                    },
                    onAddToQueue = {
                        sharedViewModel.addListToQueue(
                            arrayListOf(song.toTrack()),
                        )
                    },
                )
            }
            }
        }
        item {
            EndOfPage()
        }
    }
    if (showBottomSheet) {
        NowPlayingBottomSheet(
            onDismiss = {
                showBottomSheet = false
                chosenSong = null
            },
            navController = navController,
            song = chosenSong ?: return,
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val type = LibraryDynamicPlaylistType.toType(type)
        val isSongType =
            type != LibraryDynamicPlaylistType.Followed &&
                type != LibraryDynamicPlaylistType.TopArtists &&
                type != LibraryDynamicPlaylistType.TopAlbums
        // Counts always come from the unfiltered lists, so the subtitle keeps reporting the
        // library total while the user is typing in the search bar.
        val subtitle =
            when (type) {
                LibraryDynamicPlaylistType.Favorite ->
                    stringResource(Res.string.album_length, favorite.size.toString(), "")
                LibraryDynamicPlaylistType.MostPlayed ->
                    stringResource(Res.string.album_length, mostPlayed.size.toString(), "")
                LibraryDynamicPlaylistType.Downloaded ->
                    stringResource(Res.string.album_length, downloaded.size.toString(), "")
                LibraryDynamicPlaylistType.Followed ->
                    "${followed.size} ${stringResource(Res.string.artists)}"
                else -> null
            }
        Box {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text =
                                stringResource(
                                    type.name(),
                                ),
                            style = typo().titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = typo().bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                },
                navigationIcon = {
                    Box(Modifier.padding(horizontal = 5.dp)) {
                        RippleIconButton(
                            SonoraIcons.ArrowBackIosNew,
                            Modifier
                                .size(32.dp),
                            true,
                            tint = MaterialTheme.colorScheme.onBackground,
                        ) {
                            navController.navigateUp()
                        }
                    }
                },
                actions = {
                    if (isSongType) {
                        RippleIconButton(
                            SonoraIcons.PlayCircle,
                            Modifier
                                .size(48.dp),
                            fillMaxSize = true,
                            // Play is the primary action on the page, so it carries the brand
                            // accent. Shuffle stays neutral: two accented buttons side by side
                            // cancel each other out and neither reads as primary.
                            tint = MaterialTheme.colorScheme.primary,
                        ) {
                            if (type == LibraryDynamicPlaylistType.TopTracks) {
                                val data = analyticsUIState.topTracks.data
                                if (!data.isNullOrEmpty()) {
                                    val first = data.first().second
                                    sharedViewModel.setQueueData(
                                        QueueData.Data(
                                            listTracks = data.map { it.second }.toArrayListTrack(),
                                            firstPlayedTrack = first.toTrack(),
                                            playlistId = null,
                                            playlistName = getStringBlocking(Res.string.your_top_tracks),
                                            playlistType = PlaylistType.RADIO,
                                            continuation = null,
                                        ),
                                    )
                                    sharedViewModel.loadMediaItem(
                                        first.toTrack(),
                                        Config.PLAYLIST_CLICK,
                                        0,
                                    )
                                }
                            } else {
                                viewModel.playAll(type)
                            }
                        }
                        RippleIconButton(
                            SonoraIcons.Shuffle,
                            Modifier.size(32.dp),
                            true,
                            tint = MaterialTheme.colorScheme.onBackground,
                        ) {
                            if (type == LibraryDynamicPlaylistType.TopTracks) {
                                val data = analyticsUIState.topTracks.data
                                if (!data.isNullOrEmpty()) {
                                    val shuffled = data.shuffled()
                                    val first = shuffled.first().second
                                    sharedViewModel.setQueueData(
                                        QueueData.Data(
                                            listTracks = shuffled.map { it.second }.toArrayListTrack(),
                                            firstPlayedTrack = first.toTrack(),
                                            playlistId = null,
                                            playlistName = getStringBlocking(Res.string.your_top_tracks),
                                            playlistType = PlaylistType.RADIO,
                                            continuation = null,
                                        ),
                                    )
                                    sharedViewModel.loadMediaItem(
                                        first.toTrack(),
                                        Config.PLAYLIST_CLICK,
                                        0,
                                    )
                                }
                            } else {
                                viewModel.shuffle(type)
                            }
                        }
                    }
                    Box(Modifier.padding(horizontal = 5.dp)) {
                        RippleIconButton(
                            if (showSearchBar) SonoraIcons.Close else SonoraIcons.Search,
                            Modifier
                                .size(32.dp),
                            true,
                            tint = MaterialTheme.colorScheme.onBackground,
                        ) {
                            showSearchBar = !showSearchBar
                        }
                    }
                },
                modifier =
                    Modifier
                        .hazeEffect(hazeState, style = HazeMaterials.ultraThin()) {
                            blurEnabled = true
                        },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    ),
            )
        }
        androidx.compose.animation.AnimatedVisibility(visible = showSearchBar) {
            SearchBar(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .padding(horizontal = 12.dp),
                inputField = {
                    CompositionLocalProvider(LocalTextStyle provides typo().bodySmall) {
                        SearchBarDefaults.InputField(
                            query = query,
                            onQueryChange = { query = it },
                            onSearch = { showSearchBar = false },
                            expanded = showSearchBar,
                            onExpandedChange = { showSearchBar = it },
                            placeholder = {
                                Text(
                                    stringResource(Res.string.search),
                                    style = typo().bodySmall,
                                )
                            },
                            leadingIcon = { Icon(SonoraIcons.Search, contentDescription = null) },
                        )
                    }
                },
                expanded = false,
                onExpandedChange = {},
                windowInsets = WindowInsets(0, 0, 0, 0),
            ) {
            }
        }
    }
}

sealed class LibraryDynamicPlaylistType {
    data object Favorite : LibraryDynamicPlaylistType()

    data object Followed : LibraryDynamicPlaylistType()

    data object MostPlayed : LibraryDynamicPlaylistType()

    data object Downloaded : LibraryDynamicPlaylistType()

    data object TopTracks : LibraryDynamicPlaylistType()

    data object TopArtists : LibraryDynamicPlaylistType()

    data object TopAlbums : LibraryDynamicPlaylistType()

    fun name(): StringResource =
        when (this) {
            Favorite -> Res.string.favorite
            Followed -> Res.string.followed
            MostPlayed -> Res.string.most_played
            Downloaded -> Res.string.downloaded
            TopAlbums -> Res.string.your_top_albums
            TopArtists -> Res.string.your_top_artists
            TopTracks -> Res.string.your_top_tracks
        }

    /** What would fill this page, said in the page's own terms rather than one generic line. */
    fun emptyMessage(): StringResource =
        when (this) {
            Favorite -> Res.string.empty_favorite
            Followed -> Res.string.empty_followed
            MostPlayed -> Res.string.empty_most_played
            Downloaded -> Res.string.empty_downloaded
            TopAlbums -> Res.string.empty_top_albums
            TopArtists -> Res.string.empty_top_artists
            TopTracks -> Res.string.empty_top_tracks
        }

    // For serialization and navigation
    fun toStringParams(): String =
        when (this) {
            Favorite -> "favorite"
            Followed -> "followed"
            MostPlayed -> "most_played"
            Downloaded -> "downloaded"
            TopAlbums -> "top_albums"
            TopArtists -> "top_artists"
            TopTracks -> "top_tracks"
        }

    companion object {
        fun toType(input: String): LibraryDynamicPlaylistType =
            when (input) {
                "favorite" -> Favorite
                "followed" -> Followed
                "most_played" -> MostPlayed
                "downloaded" -> Downloaded
                "top_albums" -> TopAlbums
                "top_artists" -> TopArtists
                "top_tracks" -> TopTracks
                else -> throw IllegalArgumentException("Unknown type: $this")
            }
    }
}