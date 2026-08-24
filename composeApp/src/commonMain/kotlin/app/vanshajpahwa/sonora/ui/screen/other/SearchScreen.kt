package app.vanshajpahwa.sonora.ui.screen.other

import androidx.compose.animation.AnimatedContent
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.domain.data.entities.AlbumEntity
import app.vanshajpahwa.domain.data.entities.ArtistEntity
import app.vanshajpahwa.domain.data.entities.PlaylistEntity
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.model.searchResult.albums.AlbumsResult
import app.vanshajpahwa.domain.data.model.searchResult.artists.ArtistsResult
import app.vanshajpahwa.domain.data.model.searchResult.playlists.PlaylistsResult
import app.vanshajpahwa.domain.data.model.searchResult.songs.SongsResult
import app.vanshajpahwa.domain.data.model.searchResult.videos.VideosResult
import app.vanshajpahwa.domain.data.type.SearchResultType
import app.vanshajpahwa.domain.mediaservice.handler.PlaylistType
import app.vanshajpahwa.domain.mediaservice.handler.QueueData
import app.vanshajpahwa.domain.utils.connectArtists
import app.vanshajpahwa.domain.utils.toSongEntity
import app.vanshajpahwa.domain.utils.toTrack
import app.vanshajpahwa.sonora.Platform
import app.vanshajpahwa.sonora.extension.getScreenSizeInfo
import app.vanshajpahwa.sonora.getPlatform
import app.vanshajpahwa.sonora.ui.component.RecentEntityRow
import app.vanshajpahwa.sonora.ui.component.CenterLoadingBox
import app.vanshajpahwa.sonora.ui.component.MoodCategoryCard
import app.vanshajpahwa.sonora.ui.component.rememberHolderPainter
import app.vanshajpahwa.sonora.extension.getStringBlocking
import app.vanshajpahwa.sonora.ui.component.ArtistFullWidthItems
import app.vanshajpahwa.sonora.ui.component.Chip
import app.vanshajpahwa.sonora.ui.component.EndOfPage
import app.vanshajpahwa.sonora.ui.component.NowPlayingBottomSheet
import app.vanshajpahwa.sonora.ui.component.PlaylistFullWidthItems
import app.vanshajpahwa.sonora.ui.component.ShimmerSearchItem
import app.vanshajpahwa.sonora.ui.component.SongFullWidthItems
import app.vanshajpahwa.sonora.ui.icon.ArrowOutward
import app.vanshajpahwa.sonora.ui.icon.Close
import app.vanshajpahwa.sonora.ui.icon.History
import app.vanshajpahwa.sonora.ui.icon.Search
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.navigation.destination.home.SettingsDestination
import app.vanshajpahwa.sonora.ui.icon.Settings
import app.vanshajpahwa.sonora.ui.navigation.destination.home.MoodDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.AlbumDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.ArtistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.PlaylistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.PodcastDestination
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.SearchScreenUIState
import app.vanshajpahwa.sonora.viewModel.SearchType
import app.vanshajpahwa.sonora.viewModel.SearchViewModel
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import app.vanshajpahwa.sonora.viewModel.toStringRes
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.albums
import app.vanshajpahwa.sonora.resources.artists
import app.vanshajpahwa.sonora.resources.clear_search_history
import app.vanshajpahwa.sonora.resources.error_occurred
import app.vanshajpahwa.sonora.resources.everything_you_need
import app.vanshajpahwa.sonora.resources.in_search
import app.vanshajpahwa.sonora.resources.no_results_found
import app.vanshajpahwa.sonora.resources.playlists
import app.vanshajpahwa.sonora.resources.recently
import app.vanshajpahwa.sonora.resources.hide
import app.vanshajpahwa.sonora.resources.recently_played
import app.vanshajpahwa.sonora.resources.songs
import app.vanshajpahwa.sonora.resources.podcasts
import app.vanshajpahwa.sonora.resources.retry
import app.vanshajpahwa.sonora.resources.search_for
import app.vanshajpahwa.sonora.resources.search_for_songs_artists_albums_playlists_and_more
import app.vanshajpahwa.sonora.resources.song
import app.vanshajpahwa.sonora.resources.videos
import app.vanshajpahwa.sonora.resources.what_do_you_want_to_listen_to

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = koinInject(),
    sharedViewModel: SharedViewModel = koinInject(),
    navController: NavController,
) {
    val uriHandler = LocalUriHandler.current
    val focusManager = LocalFocusManager.current
    val searchScreenState by searchViewModel.searchScreenState.collectAsStateWithLifecycle()
    val uiState by searchViewModel.searchScreenUIState.collectAsStateWithLifecycle()
    val searchHistory by searchViewModel.searchHistory.collectAsStateWithLifecycle()
    val recentlyOpened by searchViewModel.recentlyOpened.collectAsStateWithLifecycle()
    val moodAndGenres by searchViewModel.moodAndGenres.collectAsStateWithLifecycle()
    val moodArtwork by searchViewModel.moodArtwork.collectAsStateWithLifecycle()

    var searchUIType by rememberSaveable { mutableStateOf(SearchUIType.EMPTY) }
    var searchText by rememberSaveable { mutableStateOf("") }
    var isSearchSubmitted by rememberSaveable { mutableStateOf(false) }
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    var isFocused by rememberSaveable { mutableStateOf(false) }

    // The bar floats OVER the content (a Box, not a Column) so there is something behind it to
    // blur — same arrangement HomeScreen uses. Each branch owns a scroll state, hoisted here so
    // the bar can tell whether the branch currently on screen is scrolled away from the top.
    // Two columns only on a phone held upright. Anywhere wider — tablet, landscape, desktop — two
    // columns stretch each tile to half the window, and since the tile keeps a 2:1 ratio it grows
    // absurdly tall with it.
    val screenInfo = getScreenSizeInfo()
    val isMobilePortrait = getPlatform() == Platform.Android && screenInfo.wDP < screenInfo.hDP
    val moodGridColumns = if (isMobilePortrait) 2 else 4

    val hazeState = rememberHazeState(blurEnabled = true)
    val suggestionsState = rememberLazyListState()
    val historyState = rememberLazyListState()
    val moodGridState = rememberLazyGridState()
    val resultsState = rememberLazyListState()
    var searchBarHeightPx by remember { mutableIntStateOf(0) }
    val searchBarHeight = with(LocalDensity.current) { searchBarHeightPx.toDp() }
    val isContentAtTop by remember {
        derivedStateOf {
            when (searchUIType) {
                SearchUIType.EMPTY ->
                    moodGridState.firstVisibleItemIndex == 0 && moodGridState.firstVisibleItemScrollOffset == 0
                SearchUIType.SEARCH_HISTORY ->
                    historyState.firstVisibleItemIndex == 0 && historyState.firstVisibleItemScrollOffset == 0
                SearchUIType.SEARCH_SUGGESTIONS ->
                    suggestionsState.firstVisibleItemIndex == 0 && suggestionsState.firstVisibleItemScrollOffset == 0
                SearchUIType.SEARCH_RESULTS ->
                    resultsState.firstVisibleItemIndex == 0 && resultsState.firstVisibleItemScrollOffset == 0
            }
        }
    }

    val searchForString = stringResource(Res.string.search_for)
    val songString = stringResource(Res.string.song).lowercase()
    val artistString = stringResource(Res.string.artists).lowercase()
    val albumString = stringResource(Res.string.albums).lowercase()
    val playlistString = stringResource(Res.string.playlists).lowercase()
    val videoString = stringResource(Res.string.videos).lowercase()
    val podcastString = stringResource(Res.string.podcasts).lowercase()

    // Animated Placeholder
    val placeholderTexts =
        remember {
            listOf(
                "$searchForString $songString...",
                "$searchForString $artistString...",
                "$searchForString $albumString...",
                "$searchForString $playlistString...",
                "$searchForString $videoString...",
                "$searchForString $podcastString...",
            )
        }

    var currentPlaceholderIndex by remember { mutableIntStateOf(0) }

    // Animate placeholder - pause when focused
    LaunchedEffect(isFocused) {
        while (!isFocused) {
            delay(3000) // Change every 3 seconds
            currentPlaceholderIndex = (currentPlaceholderIndex + 1) % placeholderTexts.size
        }
    }

    var sheetSong by remember { mutableStateOf<SongEntity?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val currentVideoId by searchViewModel.nowPlayingVideoId.collectAsStateWithLifecycle()
    val chipRowState = rememberScrollState()
    val pullToRefreshState = rememberPullToRefreshState()

    val onMoreClick: (SongEntity) -> Unit = { song ->
        sheetSong = song
        showBottomSheet = true
    }

    LaunchedEffect(searchText) {
        if (isFocused) {
            isSearchSubmitted = false
            isExpanded = true
        }
        if (searchText.isNotEmpty() && isFocused) {
            searchViewModel.suggestQuery(searchText)
        }
    }

    LaunchedEffect(isSearchSubmitted) {
        if (isSearchSubmitted) {
            isExpanded = false
        }
    }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            isExpanded = true
        }
    }

    // isExpanded is the search session, and it is the only thing this needs to read.
    //
    // The history branch used to require isFocused as well, which tied the list to the text field's
    // keyboard focus: dismissing the keyboard, or touching the list to scroll it, dropped focus and
    // the whole screen fell through to EMPTY. The history the user was reading vanished under their
    // finger and only came back by tapping the field again.
    //
    // isExpanded already models the intent properly. It is set when the field is focused and cleared
    // when a search is submitted, so the history now survives losing focus and the user can either
    // keep typing or pick a row.
    LaunchedEffect(isExpanded, searchText) {
        searchUIType =
            if (searchText.isNotEmpty() && isExpanded) {
                SearchUIType.SEARCH_SUGGESTIONS
            } else if (isExpanded) {
                SearchUIType.SEARCH_HISTORY
            } else if (searchText.isEmpty()) {
                SearchUIType.EMPTY
            } else {
                SearchUIType.SEARCH_RESULTS
            }
    }

    if (showBottomSheet) {
        NowPlayingBottomSheet(
            onDismiss = {
                showBottomSheet = false
                sheetSong = null
            },
            navController = navController,
            song = sheetSong,
        )
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Transparent),
    ) {
        // Content scrolls under the bar (it is the haze source), so it needs top padding
        // equal to the bar's measured height to keep its first item clear of it.
        Crossfade(
            targetState = searchUIType,
            modifier = Modifier.fillMaxSize().hazeSource(hazeState),
        ) {
            when (it) {
                SearchUIType.SEARCH_SUGGESTIONS -> {
                    LazyColumn(
                        Modifier.padding(horizontal = 16.dp),
                        state = suggestionsState,
                        contentPadding = PaddingValues(top = searchBarHeight, bottom = 10.dp),
                    ) {
                        items(searchScreenState.suggestYTItems) { item ->
                            SuggestItemRow(
                                searchResult = item,
                                onItemClick = { item ->
                                    // Record the query here too, not only on submit.
                                    //
                                    // This branch is the one that opens a suggested song, artist,
                                    // album or playlist straight from the dropdown, and it wrote
                                    // nothing. Typing "ap dhillon" and tapping the song that appeared
                                    // therefore played the track and left no trace in history, which
                                    // is why the list held a single entry: only searches finished by
                                    // pressing enter were ever saved.
                                    if (searchText.isNotBlank()) {
                                        searchViewModel.insertSearchHistory(searchText)
                                    }
                                    when (item) {
                                        is SongsResult, is VideosResult -> {
                                            val firstTrack: Track = (item as? SongsResult)?.toTrack() ?: (item as VideosResult).toTrack()
                                            searchViewModel.setQueueData(
                                                QueueData.Data(
                                                    listTracks = arrayListOf(firstTrack),
                                                    firstPlayedTrack = firstTrack,
                                                    playlistId = "RDAMVM${firstTrack.videoId}",
                                                    playlistName = "\"${searchText}\" ${getStringBlocking(Res.string.in_search)}",
                                                    playlistType = PlaylistType.RADIO,
                                                    continuation = null,
                                                ),
                                            )
                                            searchViewModel.loadMediaItem(firstTrack, type = Config.SONG_CLICK)
                                        }

                                        is ArtistsResult -> {
                                            navController.navigate(
                                                ArtistDestination(item.browseId),
                                            )
                                        }

                                        is AlbumsResult -> {
                                            navController.navigate(
                                                AlbumDestination(item.browseId),
                                            )
                                        }

                                        is PlaylistsResult -> {
                                            navController.navigate(
                                                PlaylistDestination(
                                                    item.browseId,
                                                ),
                                            )
                                        }
                                    }
                                },
                            )
                        }
                        items(searchScreenState.suggestQueries) { suggestion ->
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = ripple(),
                                            onClick = {
                                                searchText = suggestion
                                                focusManager.clearFocus()
                                                isSearchSubmitted = true
                                                searchViewModel.insertSearchHistory(suggestion)
                                                when (searchScreenState.searchType) {
                                                    SearchType.ALL -> searchViewModel.searchAll(suggestion)
                                                    SearchType.SONGS -> searchViewModel.searchSongs(suggestion)
                                                    SearchType.VIDEOS -> searchViewModel.searchVideos(suggestion)
                                                    SearchType.ALBUMS -> searchViewModel.searchAlbums(suggestion)
                                                    SearchType.ARTISTS -> searchViewModel.searchArtists(suggestion)
                                                    SearchType.PLAYLISTS -> searchViewModel.searchPlaylists(suggestion)
                                                    SearchType.FEATURED_PLAYLISTS -> searchViewModel.searchFeaturedPlaylist(suggestion)
                                                    SearchType.PODCASTS -> searchViewModel.searchPodcast(suggestion)
                                                }
                                            },
                                        ).padding(horizontal = 12.dp, vertical = 2.dp)
                                        .clip(SonoraShapes.Container),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = suggestion,
                                    style = typo().bodyMedium,
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = {
                                        searchText = suggestion
                                        focusRequester.requestFocus()
                                    },
                                ) {
                                    Icon(
                                        imageVector = SonoraIcons.ArrowOutward,
                                        contentDescription = "Search suggestion",
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            }
                        }
                        item {
                            EndOfPage(
                                withoutCredit = true,
                            )
                        }
                    }
                }

                SearchUIType.SEARCH_HISTORY -> {
                    // Search history state
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                    ) {
                        LazyColumn(
                            state = historyState,
                            contentPadding = PaddingValues(top = searchBarHeight, bottom = 10.dp),
                        ) {
                            stickyHeader {
                                Crossfade(
                                    targetState = searchHistory.isNotEmpty(),
                                ) {
                                    if (it) {
                                        Row(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .background(MaterialTheme.colorScheme.background),
                                        ) {
                                            TextButton(
                                                onClick = { searchViewModel.deleteSearchHistory() },
                                            ) {
                                                Text(
                                                    text = stringResource(Res.string.clear_search_history),
                                                    color = MaterialTheme.colorScheme.onBackground,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            items(searchHistory) { historyItem ->
                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                searchText = historyItem
                                                focusManager.clearFocus()
                                                isSearchSubmitted = true
                                                searchViewModel.insertSearchHistory(historyItem)
                                                when (searchScreenState.searchType) {
                                                    SearchType.ALL -> searchViewModel.searchAll(historyItem)
                                                    SearchType.SONGS -> searchViewModel.searchSongs(historyItem)
                                                    SearchType.VIDEOS -> searchViewModel.searchVideos(historyItem)
                                                    SearchType.ALBUMS -> searchViewModel.searchAlbums(historyItem)
                                                    SearchType.ARTISTS -> searchViewModel.searchArtists(historyItem)
                                                    SearchType.PLAYLISTS -> searchViewModel.searchPlaylists(historyItem)
                                                    SearchType.FEATURED_PLAYLISTS -> searchViewModel.searchFeaturedPlaylist(historyItem)
                                                    SearchType.PODCASTS -> searchViewModel.searchPodcast(historyItem)
                                                }
                                            }.padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = SonoraIcons.History,
                                        contentDescription = "Search history",
                                        modifier = Modifier.size(24.dp),
                                    )
                                    Spacer(modifier = Modifier.padding(horizontal = 12.dp))
                                    Text(
                                        text = historyItem,
                                        style = typo().bodyMedium,
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    // Removes this one query rather than filling it into the field.
                                    //
                                    // The arrow here used to copy the text up into the input, which
                                    // duplicated what tapping the row already does: the row runs the
                                    // search, so the only thing missing from this list was a way to
                                    // prune it. Clearing everything was the sole option before, and
                                    // that is far too blunt for removing one mistyped query.
                                    IconButton(
                                        onClick = { searchViewModel.deleteSearchHistoryItem(historyItem) },
                                    ) {
                                        Icon(
                                            imageVector = SonoraIcons.Close,
                                            contentDescription = stringResource(Res.string.hide),
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                }
                            }
                            // Below the typed searches, not above them. Focusing a search box is
                            // a signal that the user wants to search, so what they typed has to come
                            // first; putting eight entity rows in front of it pushed the actual
                            // history off screen. This section is the shortcut back to a page they
                            // already opened, which is a different job from repeating a query.
                            if (recentlyOpened.isNotEmpty()) {
                                item(key = "recent_opened_header") {
                                    Text(
                                        text = stringResource(Res.string.recently_played),
                                        style = typo().titleMedium,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 8.dp),
                                    )
                                }
                                items(recentlyOpened.take(6), key = { it.hashCode() }) { entity ->
                                    when (entity) {
                                        is ArtistEntity ->
                                            RecentEntityRow(
                                                title = entity.name,
                                                subtitle = "",
                                                typeLabel = stringResource(Res.string.artists),
                                                thumbnail = entity.thumbnails,
                                                isCircular = true,
                                                onClick = {
                                                    navController.navigate(ArtistDestination(entity.channelId))
                                                },
                                            )

                                        is AlbumEntity ->
                                            RecentEntityRow(
                                                title = entity.title,
                                                subtitle = entity.artistName?.firstOrNull().orEmpty(),
                                                typeLabel = stringResource(Res.string.albums),
                                                thumbnail = entity.thumbnails,
                                                isCircular = false,
                                                onClick = {
                                                    navController.navigate(AlbumDestination(entity.browseId))
                                                },
                                            )

                                        is PlaylistEntity ->
                                            RecentEntityRow(
                                                title = entity.title,
                                                subtitle = entity.author.orEmpty(),
                                                typeLabel = stringResource(Res.string.playlists),
                                                thumbnail = entity.thumbnails,
                                                isCircular = false,
                                                onClick = {
                                                    navController.navigate(PlaylistDestination(entity.id))
                                                },
                                            )

                                        is SongEntity ->
                                            RecentEntityRow(
                                                title = entity.title,
                                                subtitle = entity.artistName?.firstOrNull().orEmpty(),
                                                typeLabel = stringResource(Res.string.songs),
                                                thumbnail = entity.thumbnails,
                                                isCircular = false,
                                                onClick = {
                                                    val track = entity.toTrack()
                                                    searchViewModel.setQueueData(
                                                        QueueData.Data(
                                                            listTracks = arrayListOf(track),
                                                            firstPlayedTrack = track,
                                                            playlistId = "RDAMVM${entity.videoId}",
                                                            playlistName = entity.title,
                                                            playlistType = PlaylistType.RADIO,
                                                            continuation = null,
                                                        ),
                                                    )
                                                    searchViewModel.loadMediaItem(track, type = Config.SONG_CLICK)
                                                },
                                            )

                                        else -> Unit
                                    }
                                }
                            }
                            item {
                                EndOfPage(
                                    withoutCredit = true,
                                )
                            }
                        }
                    }
                }

                SearchUIType.EMPTY -> {
                    val mood = moodAndGenres
                    if (mood == null) {
                        // First run only: the repository serves its cached copy before hitting the
                        // network, so this spinner is never seen again after the first fetch.
                        CenterLoadingBox(Modifier.fillMaxSize())
                    } else {
                        // Capped and centred: on a wide desktop window the grid would otherwise
                        // span the whole width, stretching four tiles into long bars. 1100.dp
                        // keeps a tile near 250.dp, which is its natural size.
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(moodGridColumns),
                            modifier =
                                Modifier
                                    .fillMaxHeight()
                                    .widthIn(max = 1100.dp)
                                    .padding(horizontal = 16.dp),
                            state = moodGridState,
                            contentPadding = PaddingValues(top = searchBarHeight),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            // Breathing room on both sides of this block: above it
                                            // sits the floating search bar, below it the tile grid.
                                            .padding(top = 36.dp, bottom = 20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = stringResource(Res.string.everything_you_need),
                                        style = typo().titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = stringResource(Res.string.search_for_songs_artists_albums_playlists_and_more),
                                        style = typo().bodyMedium,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                            }
                            mood.sections.forEachIndexed { index, section ->
                                // First section runs straight on from the header block above it,
                                // so its own heading would just be a second title in a row.
                                if (index > 0) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Text(
                                            // Section titles come from YouTube already localised,
                                            // so there is no string resource to pick here.
                                            text = section.title,
                                            style = typo().titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.padding(top = 8.dp),
                                        )
                                    }
                                }
                                // Key must include the section: every section lives in this ONE
                                // grid, and "For you" repeats categories that also appear under
                                // Moods or Genres, so params alone collides.
                                itemsIndexed(
                                    section.items,
                                    key = { _, it -> "${section.title}/${it.params}" },
                                ) { itemIndex, item ->
                                    // LazyVerticalGrid only composes tiles inside the viewport, so
                                    // putting the request here IS the laziness — a category the
                                    // user never scrolls to never costs a browse.
                                    LaunchedEffect(item.params) {
                                        searchViewModel.loadMoodArtwork(item.params)
                                    }
                                    MoodCategoryCard(
                                        title = item.title,
                                        artworkUrl = moodArtwork[item.params],
                                        // Offset per section so the second section does not restart
                                        // on the same hue the first one opened with.
                                        colorIndex = index * 5 + itemIndex,
                                    ) {
                                        navController.navigate(MoodDestination(item.params))
                                    }
                                }
                            }
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                EndOfPage()
                            }
                        }
                        }
                    }
                }

                SearchUIType.SEARCH_RESULTS -> {
                    // Content area — chips now live in the blurred bar block above.
                    Column(modifier = Modifier.fillMaxSize()) {
                        PullToRefreshBox(
                            modifier = Modifier.fillMaxSize(),
                            state = pullToRefreshState,
                            onRefresh = {
                                val query = searchText.trim()
                                if (query.isNotEmpty()) {
                                    isSearchSubmitted = true
                                    searchViewModel.insertSearchHistory(query)
                                    when (searchScreenState.searchType) {
                                        SearchType.ALL -> searchViewModel.searchAll(query)
                                        SearchType.SONGS -> searchViewModel.searchSongs(query)
                                        SearchType.VIDEOS -> searchViewModel.searchVideos(query)
                                        SearchType.ALBUMS -> searchViewModel.searchAlbums(query)
                                        SearchType.ARTISTS -> searchViewModel.searchArtists(query)
                                        SearchType.PLAYLISTS -> searchViewModel.searchPlaylists(query)
                                        SearchType.FEATURED_PLAYLISTS -> searchViewModel.searchFeaturedPlaylist(query)
                                        SearchType.PODCASTS -> searchViewModel.searchPodcast(query)
                                    }
                                }
                            },
                            isRefreshing = uiState is SearchScreenUIState.Loading,
                            indicator = {
                                PullToRefreshDefaults.Indicator(
                                    state = pullToRefreshState,
                                    isRefreshing = uiState is SearchScreenUIState.Loading,
                                    // Anchored to the top of the box, which now starts under the
                                    // bar — without this offset the spinner sits behind the bar
                                    // and only its top sliver shows.
                                    modifier =
                                        Modifier
                                            .align(Alignment.TopCenter)
                                            .padding(top = searchBarHeight),
                                    containerColor = PullToRefreshDefaults.indicatorContainerColor,
                                    color = PullToRefreshDefaults.indicatorColor,
                                    maxDistance = PullToRefreshDefaults.PositionalThreshold - 5.dp,
                                )
                            },
                        ) {
                            Crossfade(targetState = uiState) { uiState ->
                                when (uiState) {
                                    is SearchScreenUIState.Loading -> {
                                        // Loading state — same top inset as the results list, or
                                        // the first shimmer row hides behind the bar and chips.
                                        LazyColumn(
                                            contentPadding =
                                                PaddingValues(
                                                    top = searchBarHeight,
                                                    bottom = 10.dp,
                                                ),
                                        ) {
                                            items(10) {
                                                ShimmerSearchItem()
                                            }
                                        }
                                    }

                                    is SearchScreenUIState.Success -> {
                                        // Success state with results
                                        Column(modifier = Modifier.fillMaxSize()) {
                                            // Search Results List
                                            val currentResults =
                                                when (searchScreenState.searchType) {
                                                    SearchType.ALL -> searchScreenState.searchAllResult
                                                    SearchType.SONGS -> searchScreenState.searchSongsResult
                                                    SearchType.VIDEOS -> searchScreenState.searchVideosResult
                                                    SearchType.ALBUMS -> searchScreenState.searchAlbumsResult
                                                    SearchType.ARTISTS -> searchScreenState.searchArtistsResult
                                                    SearchType.PLAYLISTS -> searchScreenState.searchPlaylistsResult
                                                    SearchType.FEATURED_PLAYLISTS -> searchScreenState.searchFeaturedPlaylistsResult
                                                    SearchType.PODCASTS -> searchScreenState.searchPodcastsResult
                                                }

                                            Crossfade(targetState = currentResults.isNotEmpty()) {
                                                if (it) {
                                                    LazyColumn(
                                                        contentPadding =
                                                            PaddingValues(
                                                                start = 4.dp,
                                                                end = 4.dp,
                                                                top = searchBarHeight,
                                                                bottom = 10.dp,
                                                            ),
                                                        state = resultsState,
                                                    ) {
                                                        items(currentResults) { result ->
                                                            when (result) {
                                                                is SongsResult -> {
                                                                    SongFullWidthItems(
                                                                        track = result.toTrack(),
                                                                        isPlaying = result.videoId == currentVideoId,
                                                                        modifier = Modifier,
                                                                        onMoreClickListener = {
                                                                            onMoreClick(result.toTrack().toSongEntity())
                                                                        },
                                                                        onClickListener = {
                                                                            val firstTrack = result.toTrack()
                                                                            searchViewModel.setQueueData(
                                                                                QueueData.Data(
                                                                                    listTracks = arrayListOf(firstTrack),
                                                                                    firstPlayedTrack = firstTrack,
                                                                                    playlistId = "RDAMVM${result.videoId}",
                                                                                    playlistName =
                                                                                        "\"${searchText}\" ${
                                                                                            getStringBlocking(
                                                                                                Res.string.in_search,
                                                                                            )
                                                                                        }",
                                                                                    playlistType = PlaylistType.RADIO,
                                                                                    continuation = null,
                                                                                ),
                                                                            )
                                                                            searchViewModel.loadMediaItem(firstTrack, Config.SONG_CLICK)
                                                                        },
                                                                        onAddToQueue = {
                                                                            sharedViewModel.addListToQueue(
                                                                                arrayListOf(result.toTrack()),
                                                                            )
                                                                        },
                                                                    )
                                                                }

                                                                is VideosResult -> {
                                                                    SongFullWidthItems(
                                                                        track = result.toTrack(),
                                                                        isPlaying = result.videoId == currentVideoId,
                                                                        modifier = Modifier,
                                                                        onMoreClickListener = {
                                                                            onMoreClick(result.toTrack().toSongEntity())
                                                                        },
                                                                        onClickListener = {
                                                                            val firstTrack = result.toTrack()
                                                                            searchViewModel.setQueueData(
                                                                                QueueData.Data(
                                                                                    listTracks = arrayListOf(firstTrack),
                                                                                    firstPlayedTrack = firstTrack,
                                                                                    playlistId = "RDAMVM${result.videoId}",
                                                                                    playlistName =
                                                                                        "\"${searchText}\" ${
                                                                                            getStringBlocking(
                                                                                                Res.string.in_search,
                                                                                            )
                                                                                        }",
                                                                                    playlistType = PlaylistType.RADIO,
                                                                                    continuation = null,
                                                                                ),
                                                                            )
                                                                            searchViewModel.loadMediaItem(firstTrack, Config.VIDEO_CLICK)
                                                                        },
                                                                        onAddToQueue = {
                                                                            sharedViewModel.addListToQueue(
                                                                                arrayListOf(result.toTrack()),
                                                                            )
                                                                        },
                                                                    )
                                                                }

                                                                is AlbumsResult -> {
                                                                    PlaylistFullWidthItems(
                                                                        data = result,
                                                                        onClickListener = {
                                                                            navController.navigate(
                                                                                AlbumDestination(
                                                                                    result.browseId,
                                                                                ),
                                                                            )
                                                                        },
                                                                    )
                                                                }

                                                                is ArtistsResult -> {
                                                                    ArtistFullWidthItems(
                                                                        data = result,
                                                                        onClickListener = {
                                                                            navController.navigate(
                                                                                ArtistDestination(
                                                                                    result.browseId,
                                                                                ),
                                                                            )
                                                                        },
                                                                    )
                                                                }

                                                                is PlaylistsResult -> {
                                                                    PlaylistFullWidthItems(
                                                                        data = result,
                                                                        onClickListener = {
                                                                            if (result.resultType == "Podcast") {
                                                                                navController.navigate(
                                                                                    PodcastDestination(
                                                                                        result.browseId,
                                                                                    ),
                                                                                )
                                                                            } else {
                                                                                navController.navigate(
                                                                                    PlaylistDestination(
                                                                                        result.browseId,
                                                                                    ),
                                                                                )
                                                                            }
                                                                        },
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        // Space at bottom to account for bottom navigation and mini player
                                                        item { Spacer(modifier = Modifier.height(150.dp)) }
                                                    }
                                                } else {
                                                    Box(
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Text(
                                                            text = stringResource(Res.string.no_results_found),
                                                            style = typo().titleMedium,
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier.fillMaxWidth(),
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    is SearchScreenUIState.Error -> {
                                        Box {
                                            // Error state
                                            Column(
                                                modifier = Modifier.align(Alignment.Center),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                            ) {
                                                Text(
                                                    text = stringResource(Res.string.error_occurred),
                                                    style = typo().titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.fillMaxWidth(),
                                                )
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Button(onClick = {
                                                    if (searchText.isNotEmpty()) {
                                                        searchViewModel.searchAll(searchText)
                                                    }
                                                }) {
                                                    Text(text = stringResource(Res.string.retry))
                                                }
                                            }
                                        }
                                    }

                                    SearchScreenUIState.Empty -> {
                                        // Empty state
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = stringResource(Res.string.no_results_found),
                                                style = typo().titleMedium,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth(),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // The measurement sits on this stable wrapper, NOT on the AnimatedContent inside it.
        //
        // Measuring the animated container fed a loop that could make the search bar vanish for good:
        // its height drives the list's top contentPadding, a mid transition measurement (zero while
        // the incoming child is still being laid out) collapsed that padding, the list shifted, its
        // firstVisibleItemIndex changed, isContentAtTop flipped, and the transition restarted. Latch
        // a zero once and the bar has no height left to come back to.
        //
        // The zero guard is the second half of the fix: a wrapper can still report zero on its very
        // first pass, and the last good height is always a better answer than none.
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .onGloballyPositioned {
                        if (it.size.height > 0) searchBarHeightPx = it.size.height
                    },
        ) {
        AnimatedContent(
            targetState = isContentAtTop,
            transitionSpec = {
                fadeIn(tween(300)).togetherWith(fadeOut(tween(300)))
            },
            label = "search_bar_scrim",
        ) { atTop ->
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .then(
                            if (atTop) {
                                Modifier.background(Color.Transparent)
                            } else {
                                Modifier.hazeEffect(hazeState, style = HazeMaterials.ultraThin()) {
                                    blurEnabled = true
                                }
                            },
                        ).padding(vertical = 10.dp),
            ) {
        // Search Bar with Animated Placeholder
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchText,
                    onQueryChange = { newText ->
                        searchText = newText
                    },
                    onSearch = { query ->
                        if (query.isNotEmpty()) {
                            isSearchSubmitted = true
                            focusManager.clearFocus()
                            searchViewModel.insertSearchHistory(query)
                            when (searchScreenState.searchType) {
                                SearchType.ALL -> searchViewModel.searchAll(query)
                                SearchType.SONGS -> searchViewModel.searchSongs(query)
                                SearchType.VIDEOS -> searchViewModel.searchVideos(query)
                                SearchType.ALBUMS -> searchViewModel.searchAlbums(query)
                                SearchType.ARTISTS -> searchViewModel.searchArtists(query)
                                SearchType.PLAYLISTS -> searchViewModel.searchPlaylists(query)
                                SearchType.FEATURED_PLAYLISTS -> searchViewModel.searchFeaturedPlaylist(query)
                                SearchType.PODCASTS -> searchViewModel.searchPodcast(query)
                            }
                        }
                    },
                    expanded = false,
                    onExpandedChange = {},
                    enabled = true,
                    placeholder = {
                        // Animated placeholder text
                        AnimatedContent(
                            targetState = currentPlaceholderIndex,
                            transitionSpec = {
                                (
                                    fadeIn(animationSpec = tween(500)) +
                                        slideInVertically { height -> height }
                                ).togetherWith(
                                    fadeOut(animationSpec = tween(500)) +
                                        slideOutVertically { height -> -height },
                                )
                            },
                            label = "placeholder_animation",
                        ) { index ->
                            Text(
                                text = placeholderTexts[index],
                                style = typo().labelMedium,
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = SonoraIcons.Search,
                            contentDescription = "Search",
                        )
                    },
                    trailingIcon = {
                        // Clear while typing, Settings otherwise. Settings is global but only
                        // reachable from Home, so getting to it from here meant a trip through an
                        // unrelated tab. This is the top right corner of the screen, the same
                        // corner it occupies on Home, and putting it in the slot the clear button
                        // already uses costs the field no width.
                        if (searchText.isNotEmpty()) {
                            IconButton(
                                modifier = Modifier.clip(CircleShape),
                                onClick = {
                                    searchText = ""
                                    isSearchSubmitted = false
                                },
                            ) {
                                Icon(
                                    imageVector = SonoraIcons.Close,
                                    contentDescription = "Clear search",
                                )
                            }
                        } else {
                            IconButton(
                                modifier = Modifier.clip(CircleShape),
                                onClick = { navController.navigate(SettingsDestination) },
                            ) {
                                Icon(
                                    imageVector = SonoraIcons.Settings,
                                    contentDescription = "Settings",
                                )
                            }
                        }
                    },
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        isFocused = it.isFocused
                    }.padding(horizontal = 16.dp),
            shape = SonoraShapes.Capsule,
            content = {},
        )
                // Filter chips ride along inside the blurred block instead of sitting in the
                // results branch. That way searchBarHeight covers them too, results scroll
                // underneath the whole thing, and the glass has something to blur.
                AnimatedVisibility(visible = searchUIType == SearchUIType.SEARCH_RESULTS) {
                    Row(
                        modifier =
                            Modifier
                                .horizontalScroll(chipRowState)
                                .padding(top = 10.dp)
                                .padding(horizontal = 12.dp),
                    ) {
                        SearchType.entries.forEach { id ->
                            val isSelected = id == searchScreenState.searchType
                            Spacer(modifier = Modifier.width(4.dp))
                            Chip(
                                isAnimated = uiState is SearchScreenUIState.Loading,
                                isSelected = isSelected,
                                text = stringResource(id.toStringRes()),
                            ) {
                                searchViewModel.setSearchType(id)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
fun SuggestItemRow(
    searchResult: SearchResultType,
    onItemClick: (SearchResultType) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onItemClick(searchResult) }
                .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val url =
            when (searchResult) {
                is SongsResult -> {
                    searchResult.thumbnails?.lastOrNull()?.url
                }

                is AlbumsResult -> {
                    searchResult.thumbnails.lastOrNull()?.url
                }

                is ArtistsResult -> {
                    searchResult.thumbnails.lastOrNull()?.url
                }

                is PlaylistsResult -> {
                    searchResult.thumbnails.lastOrNull()?.url
                }

                is VideosResult -> {
                    searchResult.thumbnails?.lastOrNull()?.url
                }

                else -> {
                    null
                }
            }

        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(SonoraShapes.Media),
        ) {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalPlatformContext.current)
                        .data(url)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .diskCacheKey(url)
                        .crossfade(true)
                        .build(),
                placeholder = rememberHolderPainter(),
                error = rememberHolderPainter(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(
                            if (searchResult is ArtistsResult) {
                                CircleShape
                            } else {
                                SonoraShapes.Container
                            },
                        ),
            )
        }

        Spacer(modifier = Modifier.padding(horizontal = 12.dp))

        Column(modifier = Modifier.weight(1f)) {
            val title =
                when (searchResult) {
                    is SongsResult -> {
                        searchResult.title
                    }

                    is AlbumsResult -> {
                        searchResult.title
                    }

                    is ArtistsResult -> {
                        searchResult.artist
                    }

                    is PlaylistsResult -> {
                        searchResult.title
                    }

                    is VideosResult -> {
                        searchResult.title
                    }

                    else -> {
                        null
                    }
                } ?: "Unknown"

            Text(
                text = title,
                style = typo().labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))

            val subtitle =
                when (searchResult) {
                    is SongsResult -> searchResult.artists?.map { it.name }?.connectArtists()
                    is AlbumsResult -> searchResult.artists.map { it.name }.connectArtists()
                    is PlaylistsResult -> searchResult.author.ifEmpty { "YouTube Music" }
                    is ArtistsResult -> stringResource(Res.string.artists)
                    is VideosResult -> searchResult.artists?.map { it.name }?.connectArtists()
                    else -> null
                } ?: "Unknown"

            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = typo().bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

enum class SearchUIType {
    EMPTY,
    SEARCH_HISTORY,
    SEARCH_SUGGESTIONS,
    SEARCH_RESULTS,
}