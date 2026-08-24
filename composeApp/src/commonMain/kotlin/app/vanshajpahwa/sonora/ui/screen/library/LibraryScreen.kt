package app.vanshajpahwa.sonora.ui.screen.library

import androidx.compose.animation.AnimatedVisibility
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import app.vanshajpahwa.common.LibraryChipType
import app.vanshajpahwa.domain.utils.LocalResource
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.extension.copy
import app.vanshajpahwa.sonora.extension.isScrollingUp
import app.vanshajpahwa.sonora.ui.component.Chip
import app.vanshajpahwa.sonora.ui.component.EndOfPage
import app.vanshajpahwa.sonora.ui.component.GridLibraryPlaylist
import app.vanshajpahwa.sonora.ui.component.LibraryItem
import app.vanshajpahwa.sonora.ui.component.LibraryItemState
import app.vanshajpahwa.sonora.ui.component.LibraryItemType
import app.vanshajpahwa.sonora.Platform
import app.vanshajpahwa.sonora.getPlatform
import app.vanshajpahwa.sonora.expect.ui.rememberBackdrop
import app.vanshajpahwa.sonora.expect.ui.layerBackdrop
import app.vanshajpahwa.sonora.ui.component.LibraryShortcuts
import app.vanshajpahwa.sonora.ui.navigation.destination.library.LibraryDynamicPlaylistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.CatalogDestination
import app.vanshajpahwa.sonora.ui.icon.AutoGraph
import app.vanshajpahwa.sonora.ui.icon.PeopleAlt
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.navigation.destination.home.AnalyticsDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.home.SettingsDestination
import app.vanshajpahwa.sonora.ui.icon.Settings
import app.vanshajpahwa.sonora.ui.component.RippleIconButton
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.LibraryViewModel
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.chart
import app.vanshajpahwa.sonora.resources.create
import app.vanshajpahwa.sonora.resources.downloaded_playlists
import app.vanshajpahwa.sonora.resources.favorite_playlists
import app.vanshajpahwa.sonora.resources.favorite_podcasts
import app.vanshajpahwa.sonora.resources.library
import app.vanshajpahwa.sonora.resources.mix_for_you
import app.vanshajpahwa.sonora.resources.no_YouTube_playlists
import app.vanshajpahwa.sonora.resources.no_favorite_playlists
import app.vanshajpahwa.sonora.resources.no_favorite_podcasts
import app.vanshajpahwa.sonora.resources.no_mixes_found
import app.vanshajpahwa.sonora.resources.no_playlists_added
import app.vanshajpahwa.sonora.resources.no_playlists_downloaded
import app.vanshajpahwa.sonora.resources.playlist_name
import app.vanshajpahwa.sonora.resources.playlist_name_cannot_be_empty
import app.vanshajpahwa.sonora.resources.your_library
import app.vanshajpahwa.sonora.resources.your_playlists
import app.vanshajpahwa.sonora.resources.your_youtube_playlists

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun LibraryScreen(
    innerPadding: PaddingValues,
    viewModel: LibraryViewModel = koinViewModel(),
    navController: NavController,
    onScrolling: (onTop: Boolean) -> Unit = {},
) {
    val density = LocalDensity.current

    val localTrackingEnabled by viewModel.localTrackingEnabled.collectAsStateWithLifecycle(initialValue = true)
    val loggedIn by viewModel.youtubeLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    val nowPlaying by viewModel.nowPlayingVideoId.collectAsStateWithLifecycle()
    val youTubePlaylist by viewModel.youTubePlaylist.collectAsStateWithLifecycle()
    val youTubeMixForYou by viewModel.youTubeMixForYou.collectAsStateWithLifecycle()
    val listCanvasSong by viewModel.listCanvasSong.collectAsStateWithLifecycle()
    val yourLocalPlaylist by viewModel.yourLocalPlaylist.collectAsStateWithLifecycle()
    val favoritePlaylist by viewModel.favoritePlaylist.collectAsStateWithLifecycle()
    val downloadedPlaylist by viewModel.downloadedPlaylist.collectAsStateWithLifecycle()
    val favoritePodcasts by viewModel.favoritePodcasts.collectAsStateWithLifecycle()
    val recentlyAdded by viewModel.recentlyAdded.collectAsStateWithLifecycle()
    val accountThumbnail by viewModel.accountThumbnail.collectAsStateWithLifecycle()
    val downloadedShortcut by viewModel.downloadedShortcut.collectAsStateWithLifecycle()
    val mostPlayedShortcut by viewModel.mostPlayedShortcut.collectAsStateWithLifecycle()
    val favoriteCount by viewModel.favoriteCount.collectAsStateWithLifecycle()
    val followedCount by viewModel.followedCount.collectAsStateWithLifecycle()

    // Library gets its own backdrop rather than reusing the app level one, for the same reason Home
    // does: the nav graph is already wrapped in a layerBackdrop, so glass drawn inside it would be
    // sampling the layer it is drawing into and take the RuntimeShader down. The source below is a
    // sibling of the cards, never an ancestor.
    val useGlass by viewModel.enableLiquidGlass.collectAsStateWithLifecycle(initialValue = false)
    val libraryGlass = useGlass && getPlatform() == Platform.Android
    val libraryBackdrop = rememberBackdrop(MaterialTheme.colorScheme.background)
    val glassBackdrop = libraryBackdrop.takeIf { libraryGlass }
    val hazeState =
        rememberHazeState(
            blurEnabled = true,
        )

    var topAppBarHeight by remember {
        mutableStateOf(0.dp)
    }
    var showAddSheet by remember { mutableStateOf(false) }

    LaunchedEffect(nowPlaying) {
        Logger.w("LibraryScreen", "Check nowPlaying: $nowPlaying")
        viewModel.getRecentlyAdded()
    }

    val chipRowState = rememberScrollState()
    val currentFilter by viewModel.currentScreen.collectAsStateWithLifecycle()

    LaunchedEffect(currentFilter) {
        when (currentFilter) {
            LibraryChipType.YOUTUBE_MUSIC_PLAYLIST -> {
                if (youTubePlaylist.data.isNullOrEmpty()) {
                    viewModel.getYouTubePlaylist()
                }
            }

            LibraryChipType.YOUTUBE_MIX_FOR_YOU -> {
                if (youTubeMixForYou.data.isNullOrEmpty()) {
                    viewModel.getYouTubeMixedForYou()
                }
            }

            LibraryChipType.YOUR_LIBRARY -> {
                viewModel.getCanvasSong()
                viewModel.getRecentlyAdded()
            }

            LibraryChipType.LOCAL_PLAYLIST -> {
                viewModel.getLocalPlaylist()
            }

            LibraryChipType.FAVORITE_PLAYLIST -> {
                viewModel.getPlaylistFavorite()
            }

            LibraryChipType.DOWNLOADED_PLAYLIST -> {
                viewModel.getDownloadedPlaylist()
            }

            LibraryChipType.FAVORITE_PODCAST -> {
                viewModel.getFavoritePodcasts()
            }

        }
    }

    Crossfade(
        modifier = Modifier.hazeSource(hazeState),
        targetState = currentFilter,
    ) { filter ->
        when (filter) {
            LibraryChipType.YOUR_LIBRARY -> {
                val state = rememberLazyListState()
                val isScrollingUp by state.isScrollingUp()
                LaunchedEffect(state) {
                    snapshotFlow { state.firstVisibleItemIndex }
                        .collect {
                            if (it <= 1) {
                                onScrolling.invoke(true)
                            } else {
                                onScrolling.invoke(isScrollingUp)
                            }
                        }
                }
                LazyColumn(
                    contentPadding =
                        innerPadding.copy(
                            top = topAppBarHeight,
                        ),
                    state = state,
                ) {
                    item {
                        Box {
                            // The gradient is the backdrop source. Glass over a perfectly flat
                            // surface has nothing to refract and reads as a plain translucent
                            // panel, so this gives it a gentle luminance ramp to bend.
                            Box(
                                modifier =
                                    Modifier
                                        .matchParentSize()
                                        .then(
                                            if (libraryGlass) {
                                                Modifier.layerBackdrop(libraryBackdrop)
                                            } else {
                                                Modifier
                                            },
                                        ).background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                                    MaterialTheme.colorScheme.background,
                                                ),
                                            ),
                                        ),
                            )
                        LibraryShortcuts(
                            downloaded = downloadedShortcut,
                            mostPlayed = mostPlayedShortcut,
                            favoriteCount = favoriteCount,
                            followedCount = followedCount,
                            onDownloaded = {
                                navController.navigate(
                                    LibraryDynamicPlaylistDestination(
                                        type = LibraryDynamicPlaylistType.Downloaded.toStringParams(),
                                    ),
                                )
                            },
                            onMostPlayed = {
                                navController.navigate(
                                    LibraryDynamicPlaylistDestination(
                                        type = LibraryDynamicPlaylistType.MostPlayed.toStringParams(),
                                    ),
                                )
                            },
                            onFavorite = {
                                navController.navigate(
                                    LibraryDynamicPlaylistDestination(
                                        type = LibraryDynamicPlaylistType.Favorite.toStringParams(),
                                    ),
                                )
                            },
                            onFollowed = {
                                navController.navigate(
                                    LibraryDynamicPlaylistDestination(
                                        type = LibraryDynamicPlaylistType.Followed.toStringParams(),
                                    ),
                                )
                            },
                            onAlbums = {
                                navController.navigate(CatalogDestination(type = CatalogDestination.ALBUMS))
                            },
                            onPodcasts = {
                                navController.navigate(CatalogDestination(type = CatalogDestination.PODCASTS))
                            },
                            backdrop = glassBackdrop,
                        )
                        }
                        Spacer(Modifier.height(6.dp))
                    }

                    if (!listCanvasSong.data.isNullOrEmpty()) {
                        item {
                            LibraryItem(
                                state =
                                    LibraryItemState(
                                        type = LibraryItemType.CanvasSong,
                                        data = listCanvasSong.data ?: emptyList(),
                                        isLoading = listCanvasSong is LocalResource.Loading,
                                    ),
                                navController = navController,
                            )
                        }
                    }

                    item {
                        LibraryItem(
                            state =
                                LibraryItemState(
                                    type =
                                        LibraryItemType.RecentlyAdded(
                                            playingVideoId = nowPlaying,
                                        ),
                                    data = recentlyAdded.data ?: emptyList(),
                                    isLoading = recentlyAdded is LocalResource.Loading,
                                ),
                            navController = navController,
                        )
                    }
                    item {
                        EndOfPage()
                    }
                }
            }

            LibraryChipType.YOUTUBE_MUSIC_PLAYLIST -> {
                GridLibraryPlaylist(
                    navController,
                    innerPadding.copy(top = topAppBarHeight),
                    youTubePlaylist,
                    emptyText = Res.string.no_YouTube_playlists,
                    onScrolling = onScrolling,
                ) {
                    viewModel.getYouTubePlaylist()
                }
            }

            LibraryChipType.YOUTUBE_MIX_FOR_YOU -> {
                GridLibraryPlaylist(
                    navController,
                    innerPadding.copy(top = topAppBarHeight),
                    youTubeMixForYou,
                    emptyText = Res.string.no_mixes_found,
                    onScrolling = onScrolling,
                ) {
                    viewModel.getYouTubeMixedForYou()
                }
            }

            LibraryChipType.LOCAL_PLAYLIST -> {
                GridLibraryPlaylist(
                    navController,
                    innerPadding.copy(top = topAppBarHeight),
                    yourLocalPlaylist,
                    onScrolling = onScrolling,
                    emptyText = Res.string.no_playlists_added,
                    createNewPlaylist = {
                        showAddSheet = true
                    },
                ) {
                    viewModel.getLocalPlaylist()
                }
            }

            LibraryChipType.FAVORITE_PLAYLIST -> {
                GridLibraryPlaylist(
                    navController,
                    innerPadding.copy(top = topAppBarHeight),
                    favoritePlaylist,
                    emptyText = Res.string.no_favorite_playlists,
                    onScrolling = onScrolling,
                ) {
                    viewModel.getPlaylistFavorite()
                }
            }

            LibraryChipType.DOWNLOADED_PLAYLIST -> {
                GridLibraryPlaylist(
                    navController,
                    innerPadding.copy(top = topAppBarHeight),
                    downloadedPlaylist,
                    emptyText = Res.string.no_playlists_downloaded,
                    onScrolling = onScrolling,
                ) {
                    viewModel.getDownloadedPlaylist()
                }
            }

            LibraryChipType.FAVORITE_PODCAST -> {
                GridLibraryPlaylist(
                    navController,
                    innerPadding.copy(top = topAppBarHeight),
                    favoritePodcasts,
                    emptyText = Res.string.no_favorite_podcasts,
                    onScrolling = onScrolling,
                ) {
                    viewModel.getFavoritePodcasts()
                }
            }

        }
    }
    val coroutineScope = rememberCoroutineScope()
    if (showAddSheet) {
        var newTitle by remember { mutableStateOf("") }
        val showAddSheetState =
            rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
            )
        val hideEditTitleBottomSheet: () -> Unit =
            {
                coroutineScope.launch {
                    showAddSheetState.hide()
                    showAddSheet = false
                }
            }
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = showAddSheetState,
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
            dragHandle = null,
            scrimColor = Color.Black.copy(alpha = .5f),
        ) {
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                shape = SonoraShapes.SheetTop,
                colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Card(
                        modifier =
                            Modifier
                                .width(60.dp)
                                .height(4.dp),
                        colors =
                            CardDefaults.cardColors().copy(
                                containerColor = MaterialTheme.colorScheme.outline,
                            ),
                        shape = SonoraShapes.Capsule,
                    ) {}
                    Spacer(modifier = Modifier.height(5.dp))
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { s -> newTitle = s },
                        label = {
                            Text(text = stringResource(Res.string.playlist_name))
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    TextButton(
                        onClick = {
                            if (newTitle.isBlank()) {
                                viewModel.makeToast(runBlocking { getString(Res.string.playlist_name_cannot_be_empty) })
                            } else {
                                viewModel.createPlaylist(newTitle)
                                hideEditTitleBottomSheet()
                            }
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                    ) {
                        Text(text = stringResource(Res.string.create))
                    }
                }
            }
        }
    }
    Column(
        Modifier
            .background(Color.Transparent)
            .hazeEffect(hazeState, style = HazeMaterials.ultraThin()) {
                blurEnabled = true
            }.onGloballyPositioned { coordinates ->
                topAppBarHeight = with(density) { coordinates.size.height.toDp() }
            },
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(Res.string.library),
                    style = typo().titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            actions = {
                // Hidden while local tracking is off. Nothing is recorded in that state, so the
                // screen behind this icon can only show zeroes, and an entry point to a guaranteed
                // empty screen is worse than no entry point.
                if (localTrackingEnabled) {
                    IconButton(
                        onClick = {
                            navController.navigate(AnalyticsDestination)
                        },
                    ) {
                        // The "NEW" tag that used to sit here was rendered at 5sp, less than half
                        // the smallest role in the scale and unreadable at arm's length, which is
                        // the one distance this app is designed for. A label nobody can read is not
                        // a label. Analytics is also no longer new, so the tag went rather than
                        // growing to a legible size and permanently crowding the icon.
                        Icon(SonoraIcons.AutoGraph, "Analytics", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
                // Settings sits rightmost here, the same slot it occupies on Home, so the gesture
                // for reaching it does not change with the tab you happen to be on. Getting to
                // Settings previously meant going back to Home first, which is a trip through an
                // unrelated screen to reach something global.
                RippleIconButton(
                    imageVector = SonoraIcons.Settings,
                    tint = MaterialTheme.colorScheme.onBackground,
                ) {
                    navController.navigate(SettingsDestination)
                }
            },
            navigationIcon = {
                AnimatedVisibility(
                    !accountThumbnail.isNullOrEmpty(),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    AsyncImage(
                        model =
                            ImageRequest
                                .Builder(LocalPlatformContext.current)
                                .data(accountThumbnail)
                                .crossfade(550)
                                .build(),
                        placeholder = rememberVectorPainter(SonoraIcons.PeopleAlt),
                        error = rememberVectorPainter(SonoraIcons.PeopleAlt),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .size(26.dp)
                                .clip(CircleShape),
                    )
                }
            },
        )
        Row(
            modifier =
                Modifier
                    .horizontalScroll(chipRowState)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
                    .background(Color.Transparent),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            LibraryChipType.entries.forEach { type ->
                if ((type == LibraryChipType.YOUTUBE_MUSIC_PLAYLIST || type == LibraryChipType.YOUTUBE_MIX_FOR_YOU) && !loggedIn) {
                    return@forEach
                }
                Chip(
                    isAnimated = false,
                    isSelected = type == currentFilter,
                    text =
                        when (type) {
                            LibraryChipType.YOUR_LIBRARY -> stringResource(Res.string.your_library)
                            LibraryChipType.YOUTUBE_MUSIC_PLAYLIST -> stringResource(Res.string.your_youtube_playlists)
                            LibraryChipType.YOUTUBE_MIX_FOR_YOU -> stringResource(Res.string.mix_for_you)
                            LibraryChipType.LOCAL_PLAYLIST -> stringResource(Res.string.your_playlists)
                            LibraryChipType.FAVORITE_PLAYLIST -> stringResource(Res.string.favorite_playlists)
                            LibraryChipType.DOWNLOADED_PLAYLIST -> stringResource(Res.string.downloaded_playlists)
                            LibraryChipType.FAVORITE_PODCAST -> stringResource(Res.string.favorite_podcasts)
                        },
                ) {
                    viewModel.setCurrentScreen(type)
                }
            }
        }
    }
}