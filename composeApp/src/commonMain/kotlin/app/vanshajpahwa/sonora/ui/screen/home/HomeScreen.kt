package app.vanshajpahwa.sonora.ui.screen.home

import androidx.compose.animation.AnimatedContent
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kmpalette.loader.rememberNetworkLoader
import com.kmpalette.rememberDominantColorState
import app.vanshajpahwa.common.CHART_SUPPORTED_COUNTRY
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.model.home.HomeItem
import app.vanshajpahwa.domain.data.model.home.chart.Chart
import app.vanshajpahwa.domain.data.model.mood.Mood
import app.vanshajpahwa.domain.extension.now
import app.vanshajpahwa.domain.mediaservice.handler.PlaylistType
import app.vanshajpahwa.domain.mediaservice.handler.QueueData
import app.vanshajpahwa.domain.utils.toSongEntity
import app.vanshajpahwa.domain.utils.toTrack
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.ui.component.rememberHolderPainter
import app.vanshajpahwa.sonora.extension.angledGradientBackground
import app.vanshajpahwa.sonora.extension.artworkScrimBrush
import app.vanshajpahwa.sonora.extension.isScrollingUp
import app.vanshajpahwa.sonora.extension.rgbFactor
import app.vanshajpahwa.sonora.ui.component.CenterLoadingBox
import app.vanshajpahwa.sonora.expect.ui.rememberBackdrop
import app.vanshajpahwa.sonora.expect.ui.layerBackdrop
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.sonora.Platform
import app.vanshajpahwa.sonora.getPlatform
import app.vanshajpahwa.sonora.ui.component.HomeGutter
import app.vanshajpahwa.sonora.ui.component.HomeArtistBubble
import app.vanshajpahwa.sonora.ui.component.HomeFolderTile
import app.vanshajpahwa.sonora.ui.component.HomePreviewTile
import app.vanshajpahwa.sonora.ui.component.HomeResumeCard
import app.vanshajpahwa.sonora.ui.component.HomeSectionHeader
import app.vanshajpahwa.sonora.ui.component.HomeShortcutTile
import app.vanshajpahwa.sonora.ui.component.HomeTrackCard
import app.vanshajpahwa.sonora.ui.component.HomeTripletRow
import app.vanshajpahwa.sonora.ui.component.Chip
import app.vanshajpahwa.sonora.ui.component.DropdownButton
import app.vanshajpahwa.sonora.ui.component.EndOfPage
import app.vanshajpahwa.sonora.ui.component.MoodCategoryCard
import app.vanshajpahwa.sonora.ui.component.HomeItem
import app.vanshajpahwa.sonora.ui.component.HomeItemContentPlaylist
import app.vanshajpahwa.sonora.ui.component.HomeShimmer
import app.vanshajpahwa.sonora.ui.component.ItemArtistChart
import app.vanshajpahwa.sonora.ui.component.OfflineErrorState
import app.vanshajpahwa.sonora.ui.component.NowPlayingBottomSheet
import app.vanshajpahwa.sonora.ui.component.QuickPicksItem
import app.vanshajpahwa.sonora.ui.component.RippleIconButton
import app.vanshajpahwa.sonora.ui.icon.History
import app.vanshajpahwa.sonora.ui.icon.Settings
import app.vanshajpahwa.sonora.ui.icon.Close
import app.vanshajpahwa.sonora.ui.icon.Search
import app.vanshajpahwa.sonora.ui.icon.DownloadForOffline
import app.vanshajpahwa.sonora.ui.icon.Favorite
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.navigation.destination.home.HomeDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.home.MoodDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.home.RecentlySongsDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.home.SettingsDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.library.LibraryDynamicPlaylistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.ArtistDestination
import app.vanshajpahwa.sonora.ui.screen.library.LibraryDynamicPlaylistType
import app.vanshajpahwa.sonora.ui.navigation.destination.list.PlaylistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.MoreLikeDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.login.LoginDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.home.LocalFilesDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.search.SearchDestination
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.HomeLocalSection
import app.vanshajpahwa.sonora.viewModel.HomeViewModel
import app.vanshajpahwa.sonora.viewModel.HomeViewModel.Companion.HOME_PARAMS_COMMUTE
import app.vanshajpahwa.sonora.viewModel.HomeViewModel.Companion.HOME_PARAMS_ENERGIZE
import app.vanshajpahwa.sonora.viewModel.HomeViewModel.Companion.HOME_PARAMS_FEEL_GOOD
import app.vanshajpahwa.sonora.viewModel.HomeViewModel.Companion.HOME_PARAMS_FOCUS
import app.vanshajpahwa.sonora.viewModel.HomeViewModel.Companion.HOME_PARAMS_PARTY
import app.vanshajpahwa.sonora.viewModel.HomeViewModel.Companion.HOME_PARAMS_RELAX
import app.vanshajpahwa.sonora.viewModel.HomeViewModel.Companion.HOME_PARAMS_ROMANCE
import app.vanshajpahwa.sonora.viewModel.HomeViewModel.Companion.HOME_PARAMS_SAD
import app.vanshajpahwa.sonora.viewModel.HomeViewModel.Companion.HOME_PARAMS_SLEEP
import app.vanshajpahwa.sonora.viewModel.HomeViewModel.Companion.HOME_PARAMS_WORKOUT
import app.vanshajpahwa.sonora.viewModel.ListState
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.Url
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.all
import app.vanshajpahwa.sonora.resources.app_name
import app.vanshajpahwa.sonora.resources.cancel
import app.vanshajpahwa.sonora.resources.chart
import app.vanshajpahwa.sonora.resources.commute
import app.vanshajpahwa.sonora.resources.energize
import app.vanshajpahwa.sonora.resources.feel_good
import app.vanshajpahwa.sonora.resources.focus
import app.vanshajpahwa.sonora.resources.go_to_log_in_page
import app.vanshajpahwa.sonora.resources.hide
import app.vanshajpahwa.sonora.resources.your_top_artists
import app.vanshajpahwa.sonora.resources.while_youre_offline
import app.vanshajpahwa.sonora.resources.local_files_subtitle
import app.vanshajpahwa.sonora.resources.log_out
import app.vanshajpahwa.sonora.resources.settings
import app.vanshajpahwa.sonora.resources.local_files
import app.vanshajpahwa.sonora.resources.more_like
import app.vanshajpahwa.sonora.resources.greeting_with_name
import app.vanshajpahwa.sonora.resources.late_night
import app.vanshajpahwa.sonora.resources.good_evening
import app.vanshajpahwa.sonora.resources.good_afternoon
import app.vanshajpahwa.sonora.resources.good_morning
import app.vanshajpahwa.sonora.resources.recently
import app.vanshajpahwa.sonora.resources.recently_played
import app.vanshajpahwa.sonora.resources.downloaded
import app.vanshajpahwa.sonora.resources.favorite
import app.vanshajpahwa.sonora.resources.discover
import app.vanshajpahwa.sonora.resources.see_all
import app.vanshajpahwa.sonora.resources.your_rotation
import app.vanshajpahwa.sonora.resources.let_s_pick_a_playlist_for_you
import app.vanshajpahwa.sonora.resources.let_s_start_with_a_radio
import app.vanshajpahwa.sonora.resources.log_in_warning
import app.vanshajpahwa.sonora.resources.party
import app.vanshajpahwa.sonora.resources.quick_picks
import app.vanshajpahwa.sonora.resources.relax
import app.vanshajpahwa.sonora.resources.romance
import app.vanshajpahwa.sonora.resources.sad
import app.vanshajpahwa.sonora.resources.sleep
import app.vanshajpahwa.sonora.resources.top_artists
import app.vanshajpahwa.sonora.resources.welcome_back
import app.vanshajpahwa.sonora.resources.what_is_best_choice_today
import app.vanshajpahwa.sonora.resources.workout

// DataStore key for blog-promo one-shot dialog. Bump the suffix (v2, v3, …) to re-promote.

private val listOfHomeChip =
    listOf(
        Res.string.all,
        Res.string.relax,
        Res.string.sleep,
        Res.string.energize,
        Res.string.sad,
        Res.string.romance,
        Res.string.feel_good,
        Res.string.workout,
        Res.string.party,
        Res.string.commute,
        Res.string.focus,
    )

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@ExperimentalFoundationApi
@Composable
fun HomeScreen(
    onScrolling: (onTop: Boolean) -> Unit = {},
    viewModel: HomeViewModel =
        koinViewModel(),
    sharedViewModel: SharedViewModel =
        koinInject(),
    navController: NavController,
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val isScrollingUp by scrollState.isScrollingUp()
    val accountInfo by viewModel.accountInfo.collectAsStateWithLifecycle()
    val homeData by viewModel.homeItemList.collectAsStateWithLifecycle()
    val newRelease by viewModel.newRelease.collectAsStateWithLifecycle()
    val chart by viewModel.chart.collectAsStateWithLifecycle()
    val moodMomentAndGenre by viewModel.exploreMoodItem.collectAsStateWithLifecycle()
    val chartLoading by viewModel.loadingChart.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    var accountShow by rememberSaveable {
        mutableStateOf(false)
    }
    val regionChart by viewModel.regionCodeChart.collectAsStateWithLifecycle()
    val reloadDestination by sharedViewModel.reloadDestination.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val chipRowState = rememberScrollState()
    val params by viewModel.params.collectAsStateWithLifecycle()
    val homeListState by viewModel.homeListState.collectAsStateWithLifecycle()
    val continuation by viewModel.continuation.collectAsStateWithLifecycle()

    val shouldShowLogInAlert by viewModel.showLogInAlert.collectAsStateWithLifecycle()
    val logInNoteDismissed by sharedViewModel.logInNoteDismissed.collectAsStateWithLifecycle()
    val mostPlayed by viewModel.mostPlayed.collectAsStateWithLifecycle()
    val liked by viewModel.liked.collectAsStateWithLifecycle()
    val downloaded by viewModel.downloaded.collectAsStateWithLifecycle()
    val resumeSong by viewModel.resumeSong.collectAsStateWithLifecycle()
    val resumePositionMs by viewModel.resumePositionMs.collectAsStateWithLifecycle()
    val hasPlayedThisSession by viewModel.hasPlayedThisSession.collectAsStateWithLifecycle()

    // Quick picks is lifted out of the feed and rendered as Discover's first item, above the mood
    // chips. Sorting it to the front of the same list was not enough: the chips live in the Discover
    // header item, so a sorted shelf still landed underneath them.
    //
    // It stays inside Discover rather than going above the local block, so the part of Home that
    // paints instantly and works offline never shifts, and a slow or signed out start leaves no gap
    // at the top waiting for the network.
    //
    // Computed here because LazyListScope is not a composable scope: neither stringResource nor
    // remember can run inside it.
    val quickPicksTitle = stringResource(Res.string.quick_picks)
    // Both lists use the SAME predicate, deliberately. They briefly did not: the feed removed the
    // shelf by title while the lifted copy also demanded non empty contents, so whenever that second
    // check failed the shelf was dropped from the feed and never re-added, and Quick picks vanished
    // from Home entirely instead of falling back to a normal shelf.
    //
    // Matching is trimmed and case insensitive because this compares YouTube's own wording against a
    // local string, and an exact match is a brittle thing to hang a whole section on.
    val isQuickPicks: (String) -> Boolean =
        remember(quickPicksTitle) {
            { title -> title.trim().equals(quickPicksTitle.trim(), ignoreCase = true) }
        }
    val quickPicksItem =
        remember(homeData, isQuickPicks) {
            homeData.firstOrNull { isQuickPicks(it.title) }
        }
    val feedWithoutQuickPicks =
        remember(homeData, isQuickPicks) {
            homeData.filterNot { isQuickPicks(it.title) }
        }
    val topArtists by viewModel.topArtists.collectAsStateWithLifecycle()
    val nowPlayingVideoId by viewModel.nowPlayingVideoId.collectAsStateWithLifecycle()
    val visibleShelf by viewModel.visibleShelf.collectAsStateWithLifecycle()
    val recent by viewModel.recent.collectAsStateWithLifecycle()

    // Three at random from the played pool, chosen in the ViewModel. Deliberately not remembered
    // here: play time ticks while a song plays, so a remember keyed on the list would reshuffle the
    // row under the user mid-listen.
    val rotation by viewModel.playItAgain.collectAsStateWithLifecycle()
    val hasLocalContent = resumeSong != null || rotation.isNotEmpty() || downloaded.isNotEmpty()


    val backgroundColor = MaterialTheme.colorScheme.background
    val isLightTheme = backgroundColor.luminance() > 0.5f

    // Home sits INSIDE the app level backdrop source (App.kt wraps the whole nav graph in it), so a
    // glass surface here would be nested in the layer it samples: a render feedback loop that takes
    // the RuntimeShader down. This is a Home local backdrop instead, sourced from the artwork
    // gradient behind the header, with the glass surfaces as siblings of it.
    val moodArtwork by viewModel.moodArtwork.collectAsStateWithLifecycle()
    val liquidGlassSetting by sharedViewModel.getEnableLiquidGlass().collectAsStateWithLifecycle(DataStoreManager.FALSE)
    val useGlass = liquidGlassSetting == DataStoreManager.TRUE && getPlatform() == Platform.Android
    val homeBackdrop = rememberBackdrop(backgroundColor)
    val glassBackdrop = homeBackdrop.takeIf { useGlass }
    var topHeaderColor by remember {
        mutableStateOf(backgroundColor)
    }
    val animatedColor by animateColorAsState(topHeaderColor, tween(500))
    val mainHomeThumbnail by viewModel.mainHomeThumbnail.collectAsStateWithLifecycle()
    val networkLoader = rememberNetworkLoader(HttpClient(CIO))
    val dominantColorState =
        rememberDominantColorState(
            defaultColor = backgroundColor,
            defaultOnColor = backgroundColor,
            loader = networkLoader,
        )

    LaunchedEffect(mainHomeThumbnail) {
        mainHomeThumbnail?.let {
            dominantColorState.updateFrom(Url(it))
        }
    }

    LaunchedEffect(dominantColorState, isLightTheme) {
        snapshotFlow { dominantColorState.color }.collect {
            // Light theme: pull the artwork color toward white for a soft pastel header;
            // dark theme keeps the original darkened tone.
            topHeaderColor = if (isLightTheme) lerp(it, Color.White, 0.85f) else it.rgbFactor(0.3f)
        }
    }

    var topAppBarHeightPx by rememberSaveable {
        mutableIntStateOf(0)
    }

    val hazeState =
        rememberHazeState(
            blurEnabled = true,
        )

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex }
            .collect {
                if (it <= 1) {
                    onScrolling.invoke(true)
                } else {
                    onScrolling.invoke(isScrollingUp)
                }
            }
    }

    val onRefresh: () -> Unit = {
        isRefreshing = true
        viewModel.getHomeItemList(params)
        Logger.w("HomeScreen", "onRefresh")
    }
    LaunchedEffect(key1 = reloadDestination) {
        if (reloadDestination == HomeDestination::class) {
            if (scrollState.firstVisibleItemIndex > 1) {
                Logger.w("HomeScreen", "scrollState.firstVisibleItemIndex: ${scrollState.firstVisibleItemIndex}")
                scrollState.animateScrollToItem(0)
                sharedViewModel.reloadDestinationDone()
            } else {
                Logger.w("HomeScreen", "scrollState.firstVisibleItemIndex: ${scrollState.firstVisibleItemIndex}")
                onRefresh.invoke()
            }
        }
    }
    LaunchedEffect(key1 = loading) {
        if (!loading) {
            isRefreshing = false
            sharedViewModel.reloadDestinationDone()
            coroutineScope.launch {
                pullToRefreshState.animateToHidden()
            }
        }
    }
    LaunchedEffect(key1 = homeData) {
        accountShow = homeData.find { it.subtitle == accountInfo?.first } == null
    }

    val shouldStartPaginate =
        remember {
            derivedStateOf {
                homeListState != ListState.PAGINATION_EXHAUST &&
                    (
                        scrollState.layoutInfo.visibleItemsInfo
                            .lastOrNull()
                            ?.index ?: -9
                    ) >= (scrollState.layoutInfo.totalItemsCount - 1)
            }
        }

    LaunchedEffect(key1 = shouldStartPaginate.value) {
        Logger.d("HomeScreen", "shouldStartPaginate: ${shouldStartPaginate.value}")
        Logger.d("HomeScreen", "homeListState: $homeListState")
        Logger.d("HomeScreen", "Continuation: $continuation")
        if (shouldStartPaginate.value && homeListState == ListState.IDLE) {
            viewModel.getContinueHomeItem(
                continuation,
            )
        }
    }

//    if (shouldShowGetDataSyncIdBottomSheet) {
//        GetDataSyncIdBottomSheet(
//            cookie = youTubeCookie,
//            onDismissRequest = {
//                shouldShowGetDataSyncIdBottomSheet = false
//            },
//)
//    }

    Box {
        PullToRefreshBox(
            modifier =
                Modifier
                    .hazeSource(hazeState),
            state = pullToRefreshState,
            onRefresh = onRefresh,
            isRefreshing = isRefreshing,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing,
                    modifier =
                        Modifier
                            .align(Alignment.TopCenter)
                            .padding(
                                top =
                                    with(LocalDensity.current) {
                                        topAppBarHeightPx.toDp()
                                    },
                            ),
                    containerColor = PullToRefreshDefaults.indicatorContainerColor,
                    color = PullToRefreshDefaults.indicatorColor,
                    maxDistance = PullToRefreshDefaults.PositionalThreshold,
                )
            },
        ) {
            // Paint what is already on the device straight away.
            //
            // The whole screen used to shimmer until the remote feed came back, even though the
            // resume card, the rotation and the downloads were all in hand from the first frame and
            // needed no network at all. On a slow connection that was seconds of skeleton covering
            // content that was ready to draw. The full screen shimmer now only wins when there is
            // genuinely nothing local to show, so the page paints immediately and the YouTube
            // shelves slot in underneath as they arrive.
            val showFullScreenShimmer = loading && !hasLocalContent
            Crossfade(targetState = showFullScreenShimmer, label = "Home Shimmer") { shimmering ->
                if (!shimmering) {
                    // Only surrender the whole screen when there is nothing at all to show. With
                    // local sections on Home an empty remote feed usually just means no network, and
                    // that is precisely when the downloads and liked songs below are worth reaching.
                    if (homeData.isEmpty() && !hasLocalContent) {
                        OfflineErrorState(
                            onRetry = onRefresh,
                            onOpenDownloaded = {
                                navController.navigate(
                                    LibraryDynamicPlaylistDestination(
                                        type = LibraryDynamicPlaylistType.Downloaded.toStringParams(),
                                    ),
                                )
                            },
                        )
                        return@Crossfade
                    }
                    LazyColumn(
                        state = scrollState,
                        // 18 rather than 28: the shelves used to need the extra air because each
                        // card reserved more height than it drew. With that slack gone, 28 read as a
                        // hole between sections.
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        // Clears the floating bottom bar and the mini player above it. Without this
                        // the last section is permanently hidden behind them, which is only visible
                        // once you scroll to the end.
                        contentPadding = PaddingValues(bottom = 160.dp),
                    ) {
                        // Everything above Discover comes from the local database, so it paints on the
                        // first frame and keeps working with no network.
                        item(key = "home_local") {
                            Box {
                                // A Box takes the height of its tallest child, so the fixed 300dp
                                // wash below would floor the whole section and leave empty space
                                // under it whenever the shelves are short, as they are before
                                // anything has been played. matchParentSize follows the Column
                                // instead of sizing it, and clipToBounds trims the wash when the
                                // Column is shorter.
                                Box(
                                    modifier =
                                        Modifier
                                            .matchParentSize()
                                            .clipToBounds(),
                                ) {
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .height(300.dp)
                                                .then(
                                                    if (useGlass) Modifier.layerBackdrop(homeBackdrop) else Modifier,
                                                ).angledGradientBackground(listOf(animatedColor, backgroundColor), 25f),
                                    ) {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(180.dp)
                                                    .align(Alignment.BottomCenter)
                                                    .background(artworkScrimBrush(backgroundColor)),
                                        )
                                    }
                                }
                                Column {
                                    Spacer(
                                        Modifier.height(
                                            with(LocalDensity.current) { topAppBarHeightPx.toDp() },
                                        ),
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    if (shouldShowLogInAlert && !logInNoteDismissed) {
                                        Box(Modifier.padding(horizontal = HomeGutter)) {
                                            LogInRequiredNote(
                                                onLogIn = {
                                                    viewModel.doneShowLogInAlert()
                                                    navController.navigate(LoginDestination)
                                                },
                                                onDismiss = { sharedViewModel.dismissLogInNote() },
                                            )
                                        }
                                        Spacer(Modifier.height(16.dp))
                                    }
                                    // Shown until the first play of the session, then retired. See
                                    // hasPlayedThisSession for why this is not keyed on "a track is
                                    // loaded". AnimatedVisibility with a fade and size change rather
                                    // than an if, so it leaves by sliding the rest of Home up instead
                                    // of snapping it.
                                    AnimatedVisibility(
                                        visible = resumeSong != null && !hasPlayedThisSession,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically(),
                                    ) {
                                        val song = resumeSong
                                        if (song != null) {
                                            Column {
                                                HomeResumeCard(
                                                    title = song.title,
                                                    subtitle = song.artistName?.joinToString().orEmpty(),
                                                    thumbnail = song.thumbnails,
                                                    backdrop = glassBackdrop,
                                                    remainingLabel =
                                                        remainingTimeLabel(
                                                            positionMs = resumePositionMs,
                                                            durationSeconds = song.durationSeconds,
                                                        ),
                                                    onPlay = { viewModel.resumePlayback() },
                                                )
                                                Spacer(Modifier.height(26.dp))
                                            }
                                        }
                                    }
                                    if (rotation.isNotEmpty()) {
                                        HomeSectionHeader(
                                            title = stringResource(Res.string.your_rotation),
                                            actionLabel = stringResource(Res.string.see_all),
                                            onAction = {
                                                navController.navigate(
                                                    LibraryDynamicPlaylistDestination(
                                                        type = LibraryDynamicPlaylistType.MostPlayed.toStringParams(),
                                                    ),
                                                )
                                            },
                                        )
                                        Spacer(Modifier.height(12.dp))
                                        // Chunked rather than one long row: the row is a three
                                        // across grid, so more songs means more rows and every card
                                        // keeps the size it has in every other section.
                                        rotation.chunked(3).forEach { rowItems ->
                                        HomeTripletRow(items = rowItems) { song ->
                                            HomeTrackCard(
                                                title = song.title,
                                                subtitle = song.artistName?.joinToString().orEmpty(),
                                                thumbnail = song.thumbnails,
                                                isPlaying = song.videoId == nowPlayingVideoId,
                                                onPlay = {
                                                    viewModel.playLocal(song.videoId, HomeLocalSection.MostPlayed)
                                                },
                                            )
                                        }
                                            Spacer(Modifier.height(12.dp))
                                        }
                                        Spacer(Modifier.height(14.dp))
                                    }
                                    if (topArtists.isNotEmpty()) {
                                        HomeSectionHeader(title = stringResource(Res.string.your_top_artists))
                                        Spacer(Modifier.height(12.dp))
                                        Row(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .horizontalScroll(rememberScrollState())
                                                    .padding(horizontal = HomeGutter),
                                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                                        ) {
                                            // Scrolls sideways rather than showing a fixed three, which
                                            // is the one place a carousel earns its keep: circular
                                            // artist portraits are narrow, so several fit on screen at
                                            // once and nothing ends up sliced in half at the edge.
                                            topArtists.take(12).forEach { artist ->
                                                HomeArtistBubble(
                                                    name = artist.name,
                                                    thumbnail = artist.thumbnails,
                                                    onClick = {
                                                        navController.navigate(
                                                            ArtistDestination(channelId = artist.channelId),
                                                        )
                                                    },
                                                )
                                            }
                                        }
                                        Spacer(Modifier.height(26.dp))
                                    }
                                    // Two wide cards where three squares were. Favourites moved to
                                    // the header, and these two now preview their top track, which a
                                    // third-of-a-row square could never fit legibly.
                                    // An empty tile is an empty promise. Downloaded reading zero
                                    // took half the row to say there was nothing there, so a tile
                                    // now appears only when it has something, the survivor takes the
                                    // full width, and with neither the row does not draw at all.
                                    if (downloaded.isNotEmpty() || recent.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = HomeGutter),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        val topDownload = downloaded.firstOrNull()
                                        val topRecent = recent.firstOrNull()
                                        if (downloaded.isNotEmpty()) {
                                        HomePreviewTile(
                                            label = stringResource(Res.string.downloaded),
                                            count = downloaded.size,
                                            icon = SonoraIcons.DownloadForOffline,
                                            tint = MaterialTheme.colorScheme.tertiary,
                                            previewTitle = topDownload?.title,
                                            previewSubtitle = topDownload?.artistName?.firstOrNull(),
                                            previewThumbnail = topDownload?.thumbnails,
                                            backdrop = glassBackdrop,
                                            modifier = Modifier.weight(1f),
                                            onClick = {
                                                navController.navigate(
                                                    LibraryDynamicPlaylistDestination(
                                                        type = LibraryDynamicPlaylistType.Downloaded.toStringParams(),
                                                    ),
                                                )
                                            },
                                        )
                                        }
                                        if (recent.isNotEmpty()) {
                                        HomePreviewTile(
                                            label = stringResource(Res.string.recently_played),
                                            count = recent.size,
                                            icon = SonoraIcons.History,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            previewTitle = topRecent?.title,
                                            previewSubtitle = topRecent?.artistName?.firstOrNull(),
                                            previewThumbnail = topRecent?.thumbnails,
                                            backdrop = glassBackdrop,
                                            modifier = Modifier.weight(1f),
                                            onClick = { navController.navigate(RecentlySongsDestination) },
                                        )
                                        }
                                    }
                                    }
                                }
                            }
                        }
                        // Recommendations seeded from what the user plays, not from the remote feed.
                        // They sit above Discover because they follow this library; Discover below is
                        // whatever YouTube is promoting generally.
                        // One shelf, swapped on a timer, rather than one row per seed. Crossfade
                        // rather than a slide: the row keeps its position and only its contents
                        // change, so nothing below it moves while the user is reading.
                        if (visibleShelf != null) {
                            item(key = "more_like_rotating") {
                                Crossfade(
                                    targetState = visibleShelf,
                                    animationSpec = tween(durationMillis = 450),
                                    label = "recommendationShelf",
                                ) { shelf ->
                                    if (shelf != null) {
                                        Column {
                                            HomeSectionHeader(
                                                title = stringResource(Res.string.more_like, shelf.seedName),
                                                underline = shelf.seedName,
                                                actionLabel = stringResource(Res.string.see_all),
                                                onAction = {
                                                    navController.navigate(
                                                        MoreLikeDestination(
                                                            seedVideoId = shelf.seedVideoId,
                                                            seedName = shelf.seedName,
                                                            seedChannelId = shelf.seedChannelId,
                                                        ),
                                                    )
                                                },
                                            )
                                            Spacer(Modifier.height(12.dp))
                                            HomeTripletRow(items = shelf.tracks) { track ->
                                                HomeTrackCard(
                                                    title = track.title,
                                                    subtitle = track.artists?.joinToString { it.name }.orEmpty(),
                                                    thumbnail = track.thumbnails?.lastOrNull()?.url,
                                                    isPlaying = track.videoId == nowPlayingVideoId,
                                                    onPlay = {
                                                        viewModel.playRecommendation(track.videoId, shelf.tracks)
                                                    },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (homeData.isNotEmpty()) {
                            item(key = "discover_header") {
                                Column {
                                    HomeSectionHeader(title = stringResource(Res.string.discover))
                                    HomeMoodChips(
                                        params = params,
                                        loading = loading,
                                        chipRowState = chipRowState,
                                        viewModel = viewModel,
                                    )
                                }
                            }
                            // Absent when signed out, offline, or simply not returned. No item is
                            // emitted then, so Discover starts with the chips and leaves no gap.
                            quickPicksItem?.let { picks ->
                                item(key = "quick_picks") {
                                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                        QuickPicks(
                                            homeItem =
                                                picks.copy(
                                                    contents =
                                                        picks.contents.mapNotNull { ct ->
                                                            ct?.copy(
                                                                artists =
                                                                    ct.artists?.let { art ->
                                                                        if (art.size > 1) art.dropLast(1) else art
                                                                    },
                                                            )
                                                        },
                                                ),
                                            navController = navController,
                                            viewModel = viewModel,
                                        )
                                    }
                                }
                            }

                        }
                        itemsIndexed(feedWithoutQuickPicks, key = { _, item ->
                            item.hashCode().toString() + (mainHomeThumbnail ?: "nothumb")
                        }) { _, item ->
                            Column(
                                modifier =
                                    Modifier
                                        .padding(horizontal = 16.dp),
                            ) {
                                // Non blank name, not merely non null. Logging out clears the stored
                                // account strings but still emits a Pair, so a null check passes with
                                // ("", "") and "Welcome back" rendered over an empty name for a user
                                // who had just signed out.
                                if (!accountInfo?.first.isNullOrBlank() &&
                                    accountShow &&
                                    item == feedWithoutQuickPicks.firstOrNull()
                                ) {
                                    AccountLayout(
                                        accountName = accountInfo?.first ?: "",
                                        url = accountInfo?.second ?: "",
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                                HomeItem(
                                    navController = navController,
                                    data = item,
                                )
                            }
                        }
                        if (homeData.isEmpty()) {
                            // No feed means no network in almost every case. Rather than only saying
                            // so, point at the two things that still work: what is already downloaded,
                            // and the music sitting on the device that never needed a connection.
                            item(key = "offline_inline") {
                                Column {
                                    HomeSectionHeader(title = stringResource(Res.string.while_youre_offline))
                                    Spacer(Modifier.height(12.dp))
                                    // Local files only. Downloaded is deliberately NOT repeated here:
                                    // the shortcut row above always carries it, online and offline, so
                                    // adding it again put the same destination on screen twice a few
                                    // hundred pixels apart. Local files is the one entry point that is
                                    // genuinely specific to being offline, so it takes the full width.
                                    HomeFolderTile(
                                        label = stringResource(Res.string.local_files),
                                        subtitle = stringResource(Res.string.local_files_subtitle),
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = HomeGutter),
                                        backdrop = glassBackdrop,
                                        onClick = { navController.navigate(LocalFilesDestination) },
                                    )
                                }
                            }
                        }
                        item {
                            AnimatedVisibility(
                                homeListState == ListState.PAGINATING,
                                enter = expandVertically() + expandVertically(),
                                exit = fadeOut() + shrinkVertically(),
                            ) {
                                CenterLoadingBox(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                )
                            }
                        }
                        if (homeListState == ListState.PAGINATION_EXHAUST) {
                            items(newRelease, key = { it.hashCode() }) {
                                AnimatedVisibility(
                                    visible = newRelease.isNotEmpty(),
                                ) {
                                    Box(
                                        modifier =
                                            Modifier
                                                .padding(horizontal = 16.dp),
                                    ) {
                                        HomeItem(
                                            navController = navController,
                                            data = it,
                                        )
                                    }
                                }
                            }
                            item {
                                AnimatedVisibility(
                                    visible = moodMomentAndGenre != null,
                                ) {
                                    Box(
                                        modifier =
                                            Modifier
                                                .padding(horizontal = 16.dp),
                                    ) {
                                        moodMomentAndGenre?.let {
                                            MoodMomentAndGenre(
                                                mood = it,
                                                navController = navController,
                                                moodArtwork = moodArtwork,
                                                onRequestArtwork = viewModel::loadMoodArtwork,
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                Column(
                                    Modifier
                                        .padding(vertical = 10.dp)
                                        .padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    ChartTitle()
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Crossfade(targetState = regionChart) {
                                        Logger.w("HomeScreen", "regionChart: $it")
                                        if (it != null) {
                                            DropdownButton(
                                                items = CHART_SUPPORTED_COUNTRY.itemsData.toList(),
                                                defaultSelected =
                                                    CHART_SUPPORTED_COUNTRY.itemsData.getOrNull(
                                                        CHART_SUPPORTED_COUNTRY.items.indexOf(it),
                                                    )
                                                        ?: CHART_SUPPORTED_COUNTRY.itemsData[1],
                                            ) {
                                                viewModel.exploreChart(
                                                    CHART_SUPPORTED_COUNTRY.items[
                                                        CHART_SUPPORTED_COUNTRY.itemsData.indexOf(
                                                            it,
                                                        ),
                                                    ],
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Crossfade(
                                        targetState = chartLoading,
                                        label = "Chart",
                                    ) { loading ->
                                        if (!loading) {
                                            chart?.let {
                                                ChartData(
                                                    chart = it,
                                                    navController = navController,
                                                )
                                            }
                                        } else {
                                            CenterLoadingBox(
                                                modifier =
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .height(400.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            EndOfPage()
                        }
                    }
                } else {
                    Column {
                        Spacer(
                            Modifier.height(
                                with(LocalDensity.current) {
                                    topAppBarHeightPx.toDp()
                                },
                            ),
                        )
                        HomeShimmer()
                    }
                }
            }
        }
        AnimatedContent(
            targetState = scrollState.firstVisibleItemIndex == 0 && scrollState.firstVisibleItemScrollOffset == 0,
            transitionSpec = {
                fadeIn(tween(300)).togetherWith(fadeOut(tween(300)))
            },
        ) { target ->
            Column(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .then(
                            if (target) {
                                Modifier.background(Color.Transparent)
                            } else {
                                Modifier
                                    .hazeEffect(hazeState, style = HazeMaterials.ultraThin()) {
                                        blurEnabled = true
                                    }
                            },
                        ).onGloballyPositioned { coordinates ->
                            topAppBarHeightPx = coordinates.size.height
                        },
            ) {
                AnimatedVisibility(
                    visible = isScrollingUp,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    HomeTopAppBar(
                        navController = navController,
                        accountName = accountInfo?.first,
                        avatarUrl = accountInfo?.second,
                        onLogOut = { viewModel.logOutYouTube() },
                    )
                }
                AnimatedVisibility(
                    visible = !isScrollingUp,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    Spacer(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(
                                    WindowInsets.statusBars,
                                ),
                    )
                }
            }
        }
    }
}

/**
 * YouTube's mood filter for the Discover feed.
 *
 * It sits with the feed it filters rather than pinned above the whole screen. Pinning it there made
 * sense while Home was nothing but that feed; now that the top of Home is local content, a mood
 * filter floating over the resume card was controlling something the user could not even see.
 */
@Composable
private fun HomeMoodChips(
    params: String?,
    loading: Boolean,
    chipRowState: ScrollState,
    viewModel: HomeViewModel,
) {
        Row(
            modifier =
                Modifier
                    .horizontalScroll(chipRowState)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .background(Color.Transparent),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            listOfHomeChip.forEach { id ->
                val isSelected =
                    when (params) {
                        HOME_PARAMS_RELAX -> id == Res.string.relax
                        HOME_PARAMS_SLEEP -> id == Res.string.sleep
                        HOME_PARAMS_ENERGIZE -> id == Res.string.energize
                        HOME_PARAMS_SAD -> id == Res.string.sad
                        HOME_PARAMS_ROMANCE -> id == Res.string.romance
                        HOME_PARAMS_FEEL_GOOD -> id == Res.string.feel_good
                        HOME_PARAMS_WORKOUT -> id == Res.string.workout
                        HOME_PARAMS_PARTY -> id == Res.string.party
                        HOME_PARAMS_COMMUTE -> id == Res.string.commute
                        HOME_PARAMS_FOCUS -> id == Res.string.focus
                        else -> id == Res.string.all
                    }
                Chip(
                    isAnimated = loading,
                    isSelected = isSelected,
                    text = stringResource(id),
                ) {
                    when (id) {
                        Res.string.all -> viewModel.setParams(null)
                        Res.string.relax -> viewModel.setParams(HOME_PARAMS_RELAX)
                        Res.string.sleep -> viewModel.setParams(HOME_PARAMS_SLEEP)
                        Res.string.energize -> viewModel.setParams(HOME_PARAMS_ENERGIZE)
                        Res.string.sad -> viewModel.setParams(HOME_PARAMS_SAD)
                        Res.string.romance -> viewModel.setParams(HOME_PARAMS_ROMANCE)
                        Res.string.feel_good -> viewModel.setParams(HOME_PARAMS_FEEL_GOOD)
                        Res.string.workout -> viewModel.setParams(HOME_PARAMS_WORKOUT)
                        Res.string.party -> viewModel.setParams(HOME_PARAMS_PARTY)
                        Res.string.commute -> viewModel.setParams(HOME_PARAMS_COMMUTE)
                        Res.string.focus -> viewModel.setParams(HOME_PARAMS_FOCUS)
                    }
                }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    navController: NavController,
    accountName: String? = null,
    avatarUrl: String? = null,
    onLogOut: () -> Unit = {},
) {
    var showAccountMenu by remember { mutableStateOf(false) }
    // Buckets shifted from the original 6/13/18/0 split so they match how people actually describe
    // the day: morning ends at noon, evening starts at 5, and anything from 10pm is late night.
    val hour = remember { now().time.hour }
    val greeting =
        when (hour) {
            in 5..11 -> stringResource(Res.string.good_morning)
            in 12..16 -> stringResource(Res.string.good_afternoon)
            in 17..21 -> stringResource(Res.string.good_evening)
            // "Good night" is what you say when leaving, which is the wrong thing to greet someone
            // with as they open a music app at 2am. This one just names the hour.
            else -> stringResource(Res.string.late_night)
        }
    // The greeting is the second line only; the name is what personalises it, so with no account
    // signed in this falls back to the bare time of day rather than an empty line.
    //
    // First name only. A greeting is the one place an app speaks to someone the way a person would,
    // and nobody says the surname out loud. It also keeps the line short enough to survive the row
    // it shares with two icons, which a long full name does not.
    val firstName =
        remember(accountName) {
            accountName?.trim()?.substringBefore(' ')?.takeIf { it.isNotBlank() }
        }
    val title =
        if (!firstName.isNullOrBlank()) {
            stringResource(Res.string.greeting_with_name, greeting, firstName)
        } else {
            greeting
        }
    // Wordmark with the greeting beneath it, then search and settings on the right.
    //
    // This used to be the app name over a time of day greeting, with notifications, history and
    // settings beside it. That is a whole row of screen height on the screen the app opens to, and
    // none of it is something you can play. History now has a tile in the local section below, and
    // settings moved into the overflow, leaving search as the only action that belongs at this level.
    TopAppBar(
        windowInsets =
            TopAppBarDefaults.windowInsets.exclude(
                TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Start),
            ),
        title = {
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = stringResource(Res.string.app_name),
                    style = typo().titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                )
                Text(
                    text = title,
                    style = typo().bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        actions = {
            // Favourites moved up from the card row. The gear became the avatar rather than a third
            // icon beside it: three actions plus a greeting is more than this row can hold, and the
            // account menu needs somewhere to live anyway.
            RippleIconButton(imageVector = SonoraIcons.Favorite, tint = MaterialTheme.colorScheme.onBackground) {
                navController.navigate(
                    LibraryDynamicPlaylistDestination(
                        type = LibraryDynamicPlaylistType.Favorite.toStringParams(),
                    ),
                )
            }
            RippleIconButton(imageVector = SonoraIcons.Search, tint = MaterialTheme.colorScheme.onBackground) {
                navController.navigate(SearchDestination)
            }
            Box {
                if (avatarUrl.isNullOrBlank()) {
                    // Signed out: no picture to show and nothing to sign out of, so the gear stays
                    // and goes straight to Settings rather than opening a menu with one item.
                    RippleIconButton(imageVector = SonoraIcons.Settings, tint = MaterialTheme.colorScheme.onBackground) {
                        navController.navigate(SettingsDestination)
                    }
                } else {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = rememberHolderPainter(),
                        error = rememberHolderPainter(),
                        modifier =
                            Modifier
                                .padding(horizontal = 8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable { showAccountMenu = true },
                    )
                }
                DropdownMenu(
                    expanded = showAccountMenu,
                    onDismissRequest = { showAccountMenu = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Res.string.settings), style = typo().bodyMedium) },
                        onClick = {
                            showAccountMenu = false
                            navController.navigate(SettingsDestination)
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(Res.string.log_out), style = typo().bodyMedium) },
                        onClick = {
                            showAccountMenu = false
                            onLogOut()
                        },
                    )
                }
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            ),
    )
}

@Composable
fun AccountLayout(
    accountName: String,
    url: String,
) {
    Column {
        Text(
            text = stringResource(Res.string.welcome_back),
            style = typo().bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 3.dp),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
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
                            CircleShape,
                        ),
            )
            Text(
                text = accountName,
                style = typo().headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier =
                    Modifier
                        .padding(start = 8.dp),
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun QuickPicks(
    homeItem: HomeItem,
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val lazyListState = rememberLazyGridState()
    val snapperFlingBehavior = rememberSnapFlingBehavior(SnapLayoutInfoProvider(lazyGridState = lazyListState, snapPosition = SnapPosition.Start))
    val density = LocalDensity.current
    var widthDp by remember {
        mutableStateOf(0.dp)
    }
    var bottomSheetShow by remember { mutableStateOf(false) }
    var track by remember { mutableStateOf<Track?>(null) }

    if (bottomSheetShow) {
        NowPlayingBottomSheet(
            onDismiss = { bottomSheetShow = false },
            song = track?.toSongEntity(),
            navController = navController,
        )
    }

    Column(
        Modifier
            .padding(vertical = 8.dp)
            .onGloballyPositioned { coordinates ->
                with(density) {
                    widthDp = (coordinates.size.width).toDp()
                }
            },
    ) {
        Text(
            text = stringResource(Res.string.let_s_start_with_a_radio),
            style = typo().bodySmall,
        )
        Text(
            text = stringResource(Res.string.quick_picks),
            style = typo().headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
        )
        LazyHorizontalGrid(
            rows = GridCells.Fixed(4),
            modifier = Modifier.height(256.dp),
            state = lazyListState,
            flingBehavior = snapperFlingBehavior,
        ) {
            items(homeItem.contents, key = { it.hashCode() }) {
                if (it != null) {
                    QuickPicksItem(
                        onClick = {
                            val firstQueue: Track = it.toTrack()
                            viewModel.setQueueData(
                                QueueData.Data(
                                    listTracks = arrayListOf(firstQueue),
                                    firstPlayedTrack = firstQueue,
                                    playlistId = "RDAMVM${it.videoId}",
                                    playlistName = "\"${it.title}\" Radio",
                                    playlistType = PlaylistType.RADIO,
                                    continuation = null,
                                ),
                            )
                            viewModel.loadMediaItem(
                                firstQueue,
                                type = Config.SONG_CLICK,
                            )
                        },
                        onLongClick = {
                            track = it.toTrack()
                            bottomSheetShow = true
                        },
                        data = it,
                        widthDp = widthDp,
                    )
                }
            }
        }
    }
}

@Composable
fun MoodMomentAndGenre(
    mood: Mood,
    navController: NavController,
    moodArtwork: Map<String, String>,
    onRequestArtwork: (String) -> Unit,
) {
    Column(
        Modifier
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = stringResource(Res.string.let_s_pick_a_playlist_for_you),
            style = typo().bodyMedium,
        )
        // One block per section YouTube returned, headed by ITS OWN title. Hard-coding
        // "Moods & moment" / "Genre" here (and reading mood.moodsMoments / mood.genres by
        // index) mislabelled every row as soon as a signed-in account got an extra
        // "For you" section, and hid the real Genres section altogether.
        mood.sections.forEachIndexed { sectionIndex, section ->
            val gridState = rememberLazyGridState()
            val flingBehavior = rememberSnapFlingBehavior(SnapLayoutInfoProvider(lazyGridState = gridState))
            Text(
                text = section.title,
                style = typo().headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
            )
            LazyHorizontalGrid(
                // 3 rows of the shared 2:1 tile at 158.dp wide, so 79.dp tall plus its padding.
                // That width is what a Search tile measures in its two column grid on this device,
                // so the two screens show the same size card rather than merely the same design.
                rows = GridCells.Fixed(3),
                modifier = Modifier.height(273.dp),
                state = gridState,
                flingBehavior = flingBehavior,
            ) {
                itemsIndexed(section.items, key = { _, it -> it.params }) { itemIndex, item ->
                    // Composed only inside the viewport, so a category never scrolled to never
                    // costs a browse. The store is shared with Search, so it may already be here.
                    LaunchedEffect(item.params) {
                        onRequestArtwork(item.params)
                    }
                    MoodCategoryCard(
                        title = item.title,
                        artworkUrl = moodArtwork[item.params],
                        colorIndex = sectionIndex * 5 + itemIndex,
                        modifier = Modifier.width(158.dp).padding(6.dp),
                    ) {
                        navController.navigate(
                            MoodDestination(
                                item.params,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChartTitle() {
    Column {
        Text(
            text = stringResource(Res.string.what_is_best_choice_today),
            style = typo().bodyMedium,
        )
        Text(
            text = stringResource(Res.string.chart),
            style = typo().headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
        )
    }
}

@Composable
fun ChartData(
    chart: Chart,
    navController: NavController,
) {
    var gridWidthDp by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    val lazyListState2 = rememberLazyGridState()
    val snapperFlingBehavior2 = rememberSnapFlingBehavior(SnapLayoutInfoProvider(lazyGridState = lazyListState2))

    Column(
        Modifier.onGloballyPositioned { coordinates ->
            with(density) {
                gridWidthDp = (coordinates.size.width).toDp()
            }
        },
    ) {
        chart.listChartItem.forEach { item ->
            Text(
                text = item.title,
                style = typo().headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
            )
            val lazyListState = rememberLazyListState()
            val snapperFlingBehavior = rememberSnapFlingBehavior(SnapLayoutInfoProvider(lazyListState = lazyListState))
            LazyRow(flingBehavior = snapperFlingBehavior) {
                items(item.playlists.size, key = { index ->
                    val data = item.playlists[index]
                    data.id + data.title + index
                }) {
                    HomeItemContentPlaylist(
                        onClick = {
                            navController.navigate(
                                PlaylistDestination(
                                    playlistId = item.playlists[it].id,
                                    isYourYouTubePlaylist = false,
                                ),
                            )
                        },
                        data = item.playlists[it],
                    )
                }
            }
        }
        Text(
            text = stringResource(Res.string.top_artists),
            style = typo().headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
        )
        LazyHorizontalGrid(
            rows = GridCells.Fixed(3),
            modifier = Modifier.height(240.dp),
            state = lazyListState2,
            flingBehavior = snapperFlingBehavior2,
        ) {
            items(chart.artists.itemArtists.size, key = { index ->
                val item = chart.artists.itemArtists[index]
                item.title + item.browseId + index
            }) {
                val data = chart.artists.itemArtists[it]
                ItemArtistChart(
                    onClick = {
                        navController.navigate(
                            ArtistDestination(
                                channelId = data.browseId,
                            ),
                        )
                    },
                    data = data,
                    widthDp = gridWidthDp,
                )
            }
        }
    }
}

/**
 * The "YouTube requires you to log in" message, as a note at the top of Home rather than a dialog.
 *
 * It used to open as an AlertDialog on launch, which blocked the app to deliver information the user
 * can act on whenever they like. As a note it says the same thing without taking the screen, and the
 * close button hides it for the rest of the session (see SharedViewModel.dismissLogInNote), so it
 * returns on the next launch instead of being silenced for good by a "do not show again" checkbox.
 */
@Composable
private fun LogInRequiredNote(
    onLogIn: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(SonoraShapes.Container)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(start = 14.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.log_in_warning),
                style = typo().bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.go_to_log_in_page),
                style = typo().bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier
                        .clip(SonoraShapes.Container)
                        .clickable(onClick = onLogIn)
                        .padding(vertical = 6.dp, horizontal = 4.dp),
            )
        }
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                imageVector = SonoraIcons.Close,
                contentDescription = stringResource(Res.string.hide),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

/**
 * "12:34 left" for the resume card, or null when there is nothing meaningful to say.
 *
 * Null rather than "0:00 left" in the two cases that would otherwise lie: a track with no known
 * duration, and a position at or past the end, which is what a finished track leaves behind.
 */
private fun remainingTimeLabel(
    positionMs: Long,
    durationSeconds: Int,
): String? {
    if (durationSeconds <= 0) return null
    val remainingSeconds = durationSeconds - (positionMs / 1000L).toInt()
    if (remainingSeconds <= 0) return null
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')} left"
}
