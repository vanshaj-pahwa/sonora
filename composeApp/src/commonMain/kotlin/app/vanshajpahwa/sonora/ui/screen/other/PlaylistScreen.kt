package app.vanshajpahwa.sonora.ui.screen.other

import androidx.compose.animation.AnimatedVisibility
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import app.vanshajpahwa.sonora.ui.component.titleMarquee
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.toBitmap
import com.kmpalette.rememberPaletteState
import app.vanshajpahwa.domain.data.entities.DownloadState
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.utils.toSongEntity
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.ui.component.rememberHolderPainter
import app.vanshajpahwa.sonora.Platform
import app.vanshajpahwa.sonora.expect.ui.layerBackdrop
import app.vanshajpahwa.sonora.expect.ui.rememberBackdrop
import app.vanshajpahwa.sonora.expect.ui.toImageBitmap
import app.vanshajpahwa.sonora.extension.angledGradientBackground
import app.vanshajpahwa.sonora.extension.artworkScrimBrush
import app.vanshajpahwa.sonora.extension.getColorFromPalette
import app.vanshajpahwa.sonora.extension.getScreenSizeInfo
import app.vanshajpahwa.sonora.extension.getStringBlocking
import app.vanshajpahwa.sonora.extension.toImmersiveBackground
import app.vanshajpahwa.sonora.getPlatform
import app.vanshajpahwa.sonora.ui.component.CenterLoadingBox
import app.vanshajpahwa.sonora.ui.component.ArtistChip
import app.vanshajpahwa.sonora.ui.component.DescriptionView
import app.vanshajpahwa.sonora.ui.component.DetailActionRow
import app.vanshajpahwa.sonora.ui.component.DetailCircleButton
import app.vanshajpahwa.sonora.ui.component.EndOfPage
import app.vanshajpahwa.sonora.ui.component.HeartCheckBox
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.sonora.ui.component.LiquidGlassIconButton
import app.vanshajpahwa.sonora.ui.component.LoadingDialog
import app.vanshajpahwa.sonora.ui.component.NowPlayingBottomSheet
import app.vanshajpahwa.sonora.ui.component.PlaylistBottomSheet
import app.vanshajpahwa.sonora.ui.component.RippleIconButton
import app.vanshajpahwa.sonora.ui.component.SongFullWidthItems
import app.vanshajpahwa.sonora.ui.component.rememberSelectionState
import app.vanshajpahwa.sonora.ui.component.liquidGlass
import app.vanshajpahwa.sonora.ui.icon.ArrowBackIosNew
import app.vanshajpahwa.sonora.ui.icon.Close
import app.vanshajpahwa.sonora.ui.icon.Favorite
import app.vanshajpahwa.sonora.ui.icon.Check
import app.vanshajpahwa.sonora.ui.icon.DownloadForOffline
import app.vanshajpahwa.sonora.ui.icon.MoreVert
import app.vanshajpahwa.sonora.ui.icon.Pause
import app.vanshajpahwa.sonora.ui.icon.PauseCircle
import app.vanshajpahwa.sonora.ui.icon.PlayArrow
import app.vanshajpahwa.sonora.ui.icon.PlayCircle
import app.vanshajpahwa.sonora.ui.icon.Search
import app.vanshajpahwa.sonora.ui.icon.Sensors
import app.vanshajpahwa.sonora.ui.icon.Shuffle
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.navigation.destination.list.ArtistDestination
import app.vanshajpahwa.sonora.ui.theme.LocalIsDarkTheme
import app.vanshajpahwa.sonora.ui.theme.seed
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.ListState
import app.vanshajpahwa.sonora.viewModel.PlaylistUIEvent
import app.vanshajpahwa.sonora.viewModel.PlaylistUIState
import app.vanshajpahwa.sonora.viewModel.PlaylistViewModel
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import app.vanshajpahwa.sonora.viewModel.UIEvent
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.album_length
import app.vanshajpahwa.sonora.resources.baseline_downloaded
import app.vanshajpahwa.sonora.resources.downloaded
import app.vanshajpahwa.sonora.resources.downloading
import app.vanshajpahwa.sonora.resources.error
import app.vanshajpahwa.sonora.resources.no_description
import app.vanshajpahwa.sonora.resources.playlist
import app.vanshajpahwa.sonora.resources.radio
import app.vanshajpahwa.sonora.resources.search
import app.vanshajpahwa.sonora.resources.unlimited

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun PlaylistScreen(
    viewModel: PlaylistViewModel = koinViewModel(),
    sharedViewModel: SharedViewModel = koinInject(),
    playlistId: String,
    isYourYouTubePlaylist: Boolean,
    navController: NavController,
) {
    // Home shelves navigate with the browseEndpoint id, which is "VL" + the playlist id
    // (HomeParser reads title.runs[0].navigationEndpoint.browseEndpoint.browseId). Every radio
    // prefix check and the watch endpoint expect the bare id, so normalise once on the way in
    // rather than stripping "VL" again at each consumer.
    val id = playlistId.removePrefix("VL")
    val tag = "PlaylistScreen"

    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/downloading_animation.json").decodeToString(),
        )
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val continuation by viewModel.continuation.collectAsStateWithLifecycle()
    val listColors by viewModel.listColors.collectAsStateWithLifecycle()
    val downloadState by viewModel.downloadState.collectAsStateWithLifecycle()
    val liked by viewModel.liked.collectAsStateWithLifecycle()
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val tracksListState by viewModel.tracksListState.collectAsStateWithLifecycle()

    var showSearchBar by rememberSaveable { mutableStateOf(false) }
    var searchBarHeightPx by remember { mutableStateOf(0) }

    val isLiquidGlassEnabled by sharedViewModel
        .getEnableLiquidGlass()
        .collectAsStateWithLifecycle(DataStoreManager.FALSE)
    val glassOn = isLiquidGlassEnabled == DataStoreManager.TRUE
    val authorThumbnail by viewModel.authorThumbnail.collectAsStateWithLifecycle()
    // Feeds the author id to the view model so it can look the picture up locally. Without this the
    // flow never has an id and the chip silently renders the name only. Uses the uiState already
    // collected above rather than subscribing to the same flow a second time.
    LaunchedEffect(uiState) {
        (uiState as? PlaylistUIState.Success)?.data?.author?.let { viewModel.setAuthor(it.id, it.name) }
    }

    val selection = rememberSelectionState()

    val lazyState = rememberLazyListState()
    val firstItemVisible by remember {
        derivedStateOf {
            lazyState.firstVisibleItemIndex == 0
        }
    }
    var shouldHideTopBar by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }

    val filteredTrack by remember {
        derivedStateOf {
            if (query.isEmpty() || !showSearchBar) {
                tracks
            } else {
                tracks.filter {
                    it.title.contains(query, ignoreCase = true) ||
                        it.artists?.joinToString(", ")?.contains(query, ignoreCase = true) == true
                }
            }
        }
    }

    LaunchedEffect(uiState) {
        Logger.d(tag, "uiState hash: ${uiState.hashCode()}")
        Logger.d(tag, "uiState data: ${uiState.data}")
    }

    LaunchedEffect(showSearchBar) {
        if (showSearchBar) {
            viewModel.getFullTracks {}
            lazyState.animateScrollToItem(0)
        }
    }

    val shouldStartPaginate =
        remember {
            derivedStateOf {
                tracksListState != ListState.PAGINATION_EXHAUST &&
                    (
                        lazyState.layoutInfo.visibleItemsInfo
                            .lastOrNull()
                            ?.index ?: -9
                    ) >= (lazyState.layoutInfo.totalItemsCount - 6)
            }
        }

    LaunchedEffect(key1 = shouldStartPaginate.value) {
        Logger.d(tag, "shouldStartPaginate: ${shouldStartPaginate.value}")
        Logger.d(tag, "tracksListState: $tracksListState")
        Logger.d(tag, "Continuation: $continuation")
        if (shouldStartPaginate.value && tracksListState == ListState.IDLE) {
            viewModel.getContinuationTrack(
                id,
                continuation,
            )
        }
    }

    val queueData by sharedViewModel.getQueueDataState().collectAsStateWithLifecycle()
    val playingPlaylistId by remember {
        derivedStateOf {
            queueData?.data?.playlistId
        }
    }

    val playingTrack by sharedViewModel.nowPlayingState
        .mapLatest {
            it?.songEntity
        }.collectAsState(initial = null)
    val isPlaying by sharedViewModel.controllerState.map { it.isPlaying }.collectAsState(initial = false)

    var currentItem by remember {
        mutableStateOf<Track?>(null)
    }

    var itemBottomSheetShow by remember {
        mutableStateOf(false)
    }
    var playlistBottomSheetShow by remember {
        mutableStateOf(false)
    }

    val onPlaylistItemClick: (videoId: String) -> Unit = { videoId ->
        viewModel.onUIEvent(
            PlaylistUIEvent.ItemClick(
                videoId = videoId,
            ),
        )
    }
    val onItemMoreClick: (videoId: String) -> Unit = { videoId ->
        currentItem = tracks.firstOrNull { it.videoId == videoId }
        if (currentItem != null) {
            itemBottomSheetShow = true
        }
    }
    val onPlaylistMoreClick: () -> Unit = {
        playlistBottomSheetShow = true
    }

    LaunchedEffect(key1 = id) {
        if (id != uiState.data?.id) {
            Logger.w(tag, "new id: $id")
            viewModel.getData(id)
        }
    }
    LaunchedEffect(key1 = firstItemVisible) {
        shouldHideTopBar = !firstItemVisible
    }
    val paletteState = rememberPaletteState()
    val hazeState =
        rememberHazeState(
            blurEnabled = true,
        )
    var bitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    // Track which thumbnail URL we've already extracted a palette from.
    // Prevents palette flash when LazyColumn recycles the header item on scroll —
    // AsyncImage re-mount fires onSuccess again, but we skip the regenerate.
    var paletteGeneratedFor by remember {
        mutableStateOf<String?>(null)
    }
    val currentThumbnail = (uiState as? PlaylistUIState.Success)?.data?.thumbnail

    LaunchedEffect(bitmap) {
        val bm = bitmap
        if (bm != null && currentThumbnail != null && paletteGeneratedFor != currentThumbnail) {
            paletteState.generate(bm)
            paletteGeneratedFor = currentThumbnail
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { paletteState.palette }
            .distinctUntilChanged()
            .collectLatest {
                viewModel.setBrush(listOf(it.getColorFromPalette(), Color.Black))
            }
    }

    // Apple Music-inspired immersive treatment: gated to mobile portrait so tablets,
    // foldable open state, landscape orientation, and Desktop keep the existing layout.
    val screenInfo = getScreenSizeInfo()
    val isMobilePortrait = getPlatform() == Platform.Android && screenInfo.wDP < screenInfo.hDP
    val dominantColor = listColors.firstOrNull() ?: Color.Black
    // Apple Music-style page background from the artwork's dominant tone (see UIExt.toImmersiveBackground).
    val mutedPaletteBg = paletteState.palette.toImmersiveBackground()
    val artworkSizeDp =
        if (isMobilePortrait) {
            (screenInfo.wDP * 0.85f).coerceIn(280f, 380f).toInt()
        } else {
            250
        }

    // Loading dialog
    val showLoadingDialog by viewModel.showLoadingDialog.collectAsStateWithLifecycle()
    if (showLoadingDialog.first) {
        LoadingDialog(
            true,
            showLoadingDialog.second,
        )
    }
//    Box {
    Crossfade(
        targetState = uiState,
    ) { state ->
        Logger.w(tag, "State hash: ${state.hashCode()}")
        when (state) {
            is PlaylistUIState.Success -> {
                val data = state.data
                Logger.d(tag, "data: $data")
                if (data == null) return@Crossfade
                val hazeState =
                    rememberHazeState(
                        blurEnabled = true,
                    )
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(if (isMobilePortrait) mutedPaletteBg else Color.Black)
                            .hazeSource(hazeState),
                    state = lazyState,
                ) {
                    if (!showSearchBar) {
                        item(contentType = "header") {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .background(Color.Transparent)
                                        .animateItem(),
                            ) {
                                if (!isMobilePortrait) {
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth(),
                                    ) {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(260.dp)
                                                    .clip(
                                                        SonoraShapes.Container,
                                                    ).angledGradientBackground(listColors, 25f),
                                        )
                                        Box(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(180.dp)
                                                    .align(Alignment.BottomCenter)
                                                    .background(artworkScrimBrush(Color.Black)),
                                        )
                                    }
                                }
                                Column(
                                    Modifier
                                        .background(Color.Transparent),
                                ) {
                                    if (!isMobilePortrait) {
                                        Row(
                                            modifier =
                                                Modifier
                                                    .wrapContentWidth()
                                                    .padding(16.dp)
                                                    .windowInsetsPadding(WindowInsets.statusBars),
                                        ) {
                                            RippleIconButton(
                                                imageVector = SonoraIcons.ArrowBackIosNew,
                                            ) {
                                                navController.navigateUp()
                                            }
                                            Spacer(Modifier.weight(1f))
                                            IconButton(
                                                onClick = {
                                                    showSearchBar = !showSearchBar
                                                },
                                            ) {
                                                Icon(SonoraIcons.Search, null, tint = Color.White)
                                            }
                                        }
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.Start,
                                    ) {
                                        if (isMobilePortrait) {
                                            // Apple Music-style: edge-to-edge artwork + liquid glass buttons.
                                            // Glass buttons MUST be siblings of the backdrop source (not children)
                                            // to avoid render feedback loop / RuntimeShader crash.
                                            val artworkBackdrop = rememberBackdrop(Color.Black)
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .height((screenInfo.hDP / 2).dp),
                                            ) {
                                                // Inner Box — backdrop SOURCE (artwork + overlays only, NO glass)
                                                Box(modifier = Modifier.fillMaxSize().layerBackdrop(artworkBackdrop)) {
                                                    AsyncImage(
                                                        model =
                                                            ImageRequest
                                                                .Builder(LocalPlatformContext.current)
                                                                .data(data.thumbnail)
                                                                .diskCachePolicy(CachePolicy.ENABLED)
                                                                .memoryCachePolicy(CachePolicy.ENABLED)
                                                                .diskCacheKey(data.thumbnail)
                                                                .memoryCacheKey(data.thumbnail)
                                                                .crossfade(false)
                                                                .build(),
                                                        placeholder = rememberHolderPainter(),
                                                        error = rememberHolderPainter(),
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        onSuccess = {
                                                            bitmap = it.result.image.toImageBitmap()
                                                        },
                                                        modifier = Modifier.fillMaxSize(),
                                                    )
                                                    // Scrim spans 70% of the artwork (not a fixed 200dp): the
                                                    // shorter the ramp, the steeper the alpha, and a steep ramp
                                                    // is what makes the fade read as an edge. See
                                                    // artworkScrimBrush for the curve itself.
                                                    Box(
                                                        modifier =
                                                            Modifier
                                                                .fillMaxWidth()
                                                                .height((screenInfo.hDP * 0.35f).dp)
                                                                .align(Alignment.BottomCenter)
                                                                .background(artworkScrimBrush(mutedPaletteBg)),
                                                    )
                                                    Column(
                                                        modifier =
                                                            Modifier
                                                                .align(Alignment.BottomCenter)
                                                                .fillMaxWidth()
                                                                .padding(horizontal = 16.dp)
                                                                .padding(bottom = 16.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                    ) {
                                                        Text(
                                                            text = data.title,
                                                            style = typo().titleLarge,
                                                            color = Color.White,
                                                            maxLines = 2,
                                                            textAlign = TextAlign.Center,
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        CompositionLocalProvider(
                                                            LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
                                                        ) {
                                                            ArtistChip(
                                                                name = data.author.name,
                                                                thumbnail = authorThumbnail,
                                                                onClick = {
                                                                    if (data.author.id.isNotEmpty()) {
                                                                        navController.navigate(ArtistDestination(data.author.id))
                                                                    }
                                                                },
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = "${
                                                                if (data.isRadio) {
                                                                    stringResource(Res.string.radio)
                                                                } else {
                                                                    stringResource(Res.string.playlist)
                                                                }
                                                            } • ${data.year}",
                                                            style = typo().bodyMedium,
                                                            color = Color(0xC4FFFFFF),
                                                            textAlign = TextAlign.Center,
                                                        )
                                                    }
                                                }
                                                // Back + Heart + Search button overlays on artwork top — liquid glass
                                                Row(
                                                    modifier =
                                                        Modifier
                                                            .align(Alignment.TopCenter)
                                                            .fillMaxWidth()
                                                            .padding(horizontal = 12.dp, vertical = 4.dp)
                                                            .windowInsetsPadding(WindowInsets.statusBars),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    if (glassOn) {
                                                        LiquidGlassIconButton(
                                                            backdrop = artworkBackdrop,
                                                            imageVector = SonoraIcons.ArrowBackIosNew,
                                                            modifier =
                                                                Modifier
                                                                    .size(48.dp),
                                                        ) {
                                                            navController.navigateUp()
                                                        }
                                                    } else {
                                                        IconButton(
                                                            onClick = { navController.navigateUp() },
                                                            modifier = Modifier.size(48.dp),
                                                        ) {
                                                            Icon(
                                                                imageVector = SonoraIcons.ArrowBackIosNew,
                                                                contentDescription = "Back",
                                                                tint = Color.White,
                                                                modifier = Modifier.size(20.dp),
                                                            )
                                                        }
                                                    }
                                                    Spacer(Modifier.weight(1f))
                                                    Row(
                                                        modifier =
                                                            Modifier
                                                                .height(48.dp)
                                                                .then(
                                                                    // Glass only when the toggle is on. With it off these
                                                                    // still rendered glass, so the setting looked broken.
                                                                    // The fallback is a scrim rather than nothing: these
                                                                    // are white glyphs sitting on album art, and on a pale
                                                                    // cover they vanish without a floor under them. Same
                                                                    // reasoning as the badge scrim on the home cards.
                                                                    if (glassOn) {
                                                                        Modifier.liquidGlass(artworkBackdrop, SonoraShapes.Media)
                                                                    } else {
                                                                        Modifier
                                                                            .clip(SonoraShapes.Media)
                                                                            .background(Color.Black.copy(alpha = 0.35f))
                                                                    },
                                                                ),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                    ) {
                                                        if (!data.isRadio) {
                                                            Box(
                                                                modifier = Modifier.size(48.dp),
                                                                contentAlignment = Alignment.Center,
                                                            ) {
                                                                HeartCheckBox(
                                                                    size = 28,
                                                                    checked = liked,
                                                                    onStateChange = {
                                                                        viewModel.onUIEvent(PlaylistUIEvent.Favorite)
                                                                    },
                                                                )
                                                            }
                                                        }
                                                        IconButton(
                                                            onClick = {
                                                                showSearchBar = !showSearchBar
                                                            },
                                                        ) {
                                                            Icon(SonoraIcons.Search, null, tint = Color.White)
                                                        }
                                                        IconButton(
                                                            onClick = onPlaylistMoreClick,
                                                        ) {
                                                            Icon(
                                                                imageVector = SonoraIcons.MoreVert,
                                                                contentDescription = "More",
                                                                tint = Color.White,
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            AsyncImage(
                                                model =
                                                    ImageRequest
                                                        .Builder(LocalPlatformContext.current)
                                                        .data(data.thumbnail)
                                                        .diskCachePolicy(CachePolicy.ENABLED)
                                                        .diskCacheKey(data.thumbnail)
                                                        .crossfade(true)
                                                        .build(),
                                                placeholder = rememberHolderPainter(),
                                                error = rememberHolderPainter(),
                                                contentDescription = null,
                                                contentScale = ContentScale.FillHeight,
                                                onSuccess = {
                                                    bitmap = it.result.image.toImageBitmap()
                                                },
                                                modifier =
                                                    Modifier
                                                        .height(artworkSizeDp.dp)
                                                        .wrapContentWidth()
                                                        .align(Alignment.CenterHorizontally)
                                                        .clip(SonoraShapes.Media),
                                            )
                                        }
                                        Box(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .wrapContentHeight(),
                                        ) {
                                            Column(Modifier.padding(horizontal = 32.dp)) {
                                                if (!isMobilePortrait) {
                                                    Spacer(modifier = Modifier.size(25.dp))
                                                    Text(
                                                        text = data.title,
                                                        style = typo().titleMedium,
                                                        color = Color.White,
                                                        maxLines = 2,
                                                    )
                                                    Column(
                                                        modifier = Modifier.padding(vertical = 4.dp),
                                                    ) {
                                                        CompositionLocalProvider(
                                                            LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
                                                        ) {
                                                            ArtistChip(
                                                                name = data.author.name,
                                                                thumbnail = authorThumbnail,
                                                                onClick = {
                                                                    if (data.author.id.isNotEmpty()) {
                                                                        navController.navigate(ArtistDestination(data.author.id))
                                                                    }
                                                                },
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.size(4.dp))
                                                        Text(
                                                            text = "${
                                                                if (data.isRadio) {
                                                                    stringResource(Res.string.radio)
                                                                } else {
                                                                    stringResource(Res.string.playlist)
                                                                }
                                                            } • ${data.year}",
                                                            style = typo().bodyMedium,
                                                        )
                                                    }
                                                }
                                                if (isMobilePortrait) {
                                                    // Apple Music-style action row:
                                                    // [Shuffle][Play pill][Download/More] (cluster centered, all 48dp matching size)
                                                    val isThisPlaying = isPlaying && playingPlaylistId == data.id
                                                    DetailActionRow(
                                                        isPlaying = isThisPlaying,
                                                        playLabel = if (isThisPlaying) "Pause" else "Play",
                                                        onPlayPause = {
                                                            if (isThisPlaying) {
                                                                sharedViewModel.onUIEvent(UIEvent.PlayPause)
                                                            } else {
                                                                viewModel.onUIEvent(PlaylistUIEvent.PlayAll)
                                                            }
                                                        },
                                                        // A radio has no fixed order to shuffle.
                                                        leading =
                                                            if (data.isRadio) {
                                                                null
                                                            } else {
                                                                {
                                                                    DetailCircleButton(
                                                                        icon = SonoraIcons.Shuffle,
                                                                        contentDescription = "Shuffle",
                                                                        onClick = { viewModel.onUIEvent(PlaylistUIEvent.Shuffle) },
                                                                    )
                                                                }
                                                            },
                                                        trailing = {
                                                            if (!data.isRadio) {
                                                                Box(
                                                                    modifier =
                                                                        Modifier
                                                                            .size(48.dp)
                                                                            .clip(CircleShape)
                                                                            .background(Color.White.copy(alpha = 0.12f)),
                                                                    contentAlignment = Alignment.Center,
                                                                ) {
                                                                    Crossfade(targetState = downloadState) { state ->
                                                                        when (state) {
                                                                            DownloadState.STATE_DOWNLOADED -> {
                                                                                Box(
                                                                                    modifier =
                                                                                        Modifier
                                                                                            .fillMaxSize()
                                                                                            .clickable {
                                                                                                viewModel.makeToast(
                                                                                                    getStringBlocking(Res.string.downloaded),
                                                                                                )
                                                                                            },
                                                                                    contentAlignment = Alignment.Center,
                                                                                ) {
                                                                                    Icon(
                                                                                        painter = painterResource(Res.drawable.baseline_downloaded),
                                                                                        tint = Color(0xFF00A0CB),
                                                                                        contentDescription = "",
                                                                                        modifier = Modifier.size(22.dp),
                                                                                    )
                                                                                }
                                                                            }

                                                                            DownloadState.STATE_DOWNLOADING -> {
                                                                                Box(
                                                                                    modifier =
                                                                                        Modifier
                                                                                            .fillMaxSize()
                                                                                            .clickable {
                                                                                                viewModel.makeToast(
                                                                                                    getStringBlocking(Res.string.downloading),
                                                                                                )
                                                                                            },
                                                                                    contentAlignment = Alignment.Center,
                                                                                ) {
                                                                                    Image(
                                                                                        painter =
                                                                                            rememberLottiePainter(
                                                                                                composition = composition,
                                                                                                iterations = Compottie.IterateForever,
                                                                                            ),
                                                                                        contentDescription = "Lottie animation",
                                                                                        modifier = Modifier.size(28.dp),
                                                                                    )
                                                                                }
                                                                            }

                                                                            else -> {
                                                                                Box(
                                                                                    modifier =
                                                                                        Modifier
                                                                                            .fillMaxSize()
                                                                                            .clickable {
                                                                                                Logger.w(
                                                                                                    "PlaylistScreen",
                                                                                                    "downloadState: $downloadState",
                                                                                                )
                                                                                                viewModel.onUIEvent(PlaylistUIEvent.Download)
                                                                                            },
                                                                                    contentAlignment = Alignment.Center,
                                                                                ) {
                                                                                    Icon(
                                                                                        imageVector = SonoraIcons.DownloadForOffline,
                                                                                        tint = Color.White,
                                                                                        contentDescription = "Download",
                                                                                        modifier = Modifier.size(22.dp),
                                                                                    )
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        },
                                                    )
                                                } else {
                                                    Row(
                                                        modifier =
                                                            Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                    ) {
                                                        Crossfade(isPlaying && playingPlaylistId == data.id) { isThisPlaying ->
                                                            if (isThisPlaying) {
                                                                RippleIconButton(
                                                                    imageVector = SonoraIcons.PauseCircle,
                                                                    fillMaxSize = true,
                                                                    tint = seed,
                                                                    modifier = Modifier.size(48.dp),
                                                                ) {
                                                                    sharedViewModel.onUIEvent(UIEvent.PlayPause)
                                                                }
                                                            } else {
                                                                RippleIconButton(
                                                                    imageVector = SonoraIcons.PlayCircle,
                                                                    fillMaxSize = true,
                                                                    tint = seed,
                                                                    modifier = Modifier.size(48.dp),
                                                                ) {
                                                                    viewModel.onUIEvent(PlaylistUIEvent.PlayAll)
                                                                }
                                                            }
                                                        }
                                                        if (!data.isRadio) {
                                                            HeartCheckBox(
                                                                size = 32,
                                                                checked = liked,
                                                                onStateChange = {
                                                                    viewModel.onUIEvent(PlaylistUIEvent.Favorite)
                                                                },
                                                            )
                                                            Crossfade(targetState = downloadState) {
                                                                when (it) {
                                                                    DownloadState.STATE_DOWNLOADED -> {
                                                                        Box(
                                                                            modifier =
                                                                                Modifier
                                                                                    .size(36.dp)
                                                                                    .clip(
                                                                                        CircleShape,
                                                                                    ).clickable {
                                                                                        viewModel.makeToast(getStringBlocking(Res.string.downloaded))
                                                                                    },
                                                                        ) {
                                                                            Icon(
                                                                                painter = painterResource(Res.drawable.baseline_downloaded),
                                                                                tint = Color(0xFF00A0CB),
                                                                                contentDescription = "",
                                                                                modifier =
                                                                                    Modifier
                                                                                        .size(36.dp)
                                                                                        .padding(2.dp),
                                                                            )
                                                                        }
                                                                    }

                                                                    DownloadState.STATE_DOWNLOADING -> {
                                                                        Box(
                                                                            modifier =
                                                                                Modifier
                                                                                    .size(36.dp)
                                                                                    .clip(
                                                                                        CircleShape,
                                                                                    ).clickable {
                                                                                        viewModel.makeToast(getStringBlocking(Res.string.downloading))
                                                                                    },
                                                                        ) {
                                                                            Image(
                                                                                painter =
                                                                                    rememberLottiePainter(
                                                                                        composition = composition,
                                                                                        iterations = Compottie.IterateForever,
                                                                                    ),
                                                                                contentDescription = "Lottie animation",
                                                                                modifier = Modifier.fillMaxSize(),
                                                                            )
                                                                        }
                                                                    }

                                                                    else -> {
                                                                        RippleIconButton(
                                                                            fillMaxSize = true,
                                                                            imageVector = SonoraIcons.DownloadForOffline,
                                                                            modifier = Modifier.size(36.dp),
                                                                        ) {
                                                                            Logger.w("PlaylistScreen", "downloadState: $downloadState")
                                                                            viewModel.onUIEvent(PlaylistUIEvent.Download)
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        Spacer(Modifier.weight(1f))
                                                        if (!data.isRadio) {
                                                            RippleIconButton(
                                                                modifier =
                                                                    Modifier.size(36.dp),
                                                                imageVector = SonoraIcons.Sensors,
                                                                fillMaxSize = true,
                                                            ) {
                                                                viewModel.onUIEvent(PlaylistUIEvent.StartRadio)
                                                            }
                                                            Spacer(Modifier.size(5.dp))
                                                            RippleIconButton(
                                                                modifier =
                                                                    Modifier.size(36.dp),
                                                                imageVector = SonoraIcons.Shuffle,
                                                                fillMaxSize = true,
                                                            ) {
                                                                viewModel.onUIEvent(PlaylistUIEvent.Shuffle)
                                                            }
                                                            Spacer(Modifier.size(5.dp))
                                                        }
                                                        RippleIconButton(
                                                            modifier =
                                                                Modifier.size(36.dp),
                                                            imageVector = SonoraIcons.MoreVert,
                                                            fillMaxSize = true,
                                                        ) {
                                                            onPlaylistMoreClick()
                                                        }
                                                    }
                                                }
                                                val uriHandler = LocalUriHandler.current
                                                // Same as the album page: an empty description gets
                                                // no row, rather than a row saying it is empty.
                                                val playlistDescription = state.data.description?.takeIf { it.isNotBlank() }
                                                if (playlistDescription != null) {
                                                    DescriptionView(
                                                    modifier =
                                                        Modifier
                                                            .padding(
                                                                top = 8.dp,
                                                            ),
                                                        text = playlistDescription,
                                                    limitLine = 3,
                                                    onTimeClicked = {},
                                                    onURLClicked = { url ->
                                                        uriHandler.openUri(url)
                                                    },
                                                )
                                                }
                                                Text(
                                                    text =
                                                        if (data.isRadio) {
                                                            stringResource(Res.string.unlimited)
                                                        } else {
                                                            stringResource(
                                                                Res.string.album_length,
                                                                (data.trackCount).toString(),
                                                            )
                                                        },
                                                    color = Color.White,
                                                    style = typo().bodyMedium,
                                                    modifier = Modifier.padding(vertical = 8.dp),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        item {
                            val density = LocalDensity.current
                            Spacer(
                                Modifier.height(
                                    with(density) { searchBarHeightPx.toDp() },
                                ),
                            )
                        }
                    }
                    items(count = filteredTrack.size, key = { index ->
                        val item = filteredTrack.getOrNull(index)
                        (item?.videoId ?: "") + "item_$index"
                    }) { index ->
                        val item = filteredTrack.getOrNull(index)
                        if (item != null) {
                            Column(modifier = Modifier.animateItem()) {
                                if (playingTrack?.videoId == item.videoId && isPlaying) {
                                    SongFullWidthItems(
                                        forceDark = true,
                                        isPlaying = true,
                                        track = item,
                                        showDuration = true,
                                        onMoreClickListener = { onItemMoreClick(it) },
                                        onClickListener = {
                                            Logger.w("PlaylistScreen", "index: $index")
                                            onPlaylistItemClick(it)
                                        },
                                        onAddToQueue = {
                                            sharedViewModel.addListToQueue(
                                                arrayListOf(item),
                                            )
                                        },
                                        selectionActive = selection.isActive,
                                        selected = selection.isSelected(item.videoId),
                                        onLongClick = { selection.toggle(it) },
                                        modifier = Modifier,
                                    )
                                } else {
                                    SongFullWidthItems(
                                        forceDark = true,
                                        isPlaying = false,
                                        track = item,
                                        showDuration = true,
                                        onMoreClickListener = { onItemMoreClick(it) },
                                        onClickListener = {
                                            Logger.w("PlaylistScreen", "index: $index")
                                            onPlaylistItemClick(it)
                                        },
                                        onAddToQueue = {
                                            sharedViewModel.addListToQueue(
                                                arrayListOf(item),
                                            )
                                        },
                                        selectionActive = selection.isActive,
                                        selected = selection.isSelected(item.videoId),
                                        onLongClick = { selection.toggle(it) },
                                        modifier = Modifier,
                                    )
                                }
                                if (isMobilePortrait && index < filteredTrack.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                                        thickness = 0.5.dp,
                                        color = Color.White.copy(alpha = 0.12f),
                                    )
                                }
                            }
                        }
                    }
                    when (tracksListState) {
                        ListState.IDLE -> {
                            // DO NOTHING
                            item {
                                EndOfPage()
                            }
                        }

                        ListState.LOADING, ListState.PAGINATING -> {
                            item {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CenterLoadingBox(
                                        modifier = Modifier.size(80.dp),
                                    )
                                }
                            }
                            item {
                                EndOfPage()
                            }
                        }

                        ListState.ERROR -> {
                            item {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(64.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = stringResource(Res.string.error),
                                        style = typo().bodyMedium,
                                    )
                                }
                            }
                            item {
                                EndOfPage()
                            }
                        }

                        ListState.PAGINATION_EXHAUST -> {
                            item {
                                EndOfPage()
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showSearchBar,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically(),
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { searchBarHeightPx = it.size.height }
                            .then(
                                if (isMobilePortrait) {
                                    Modifier.hazeEffect(hazeState) {
                                        blurEnabled = true
                                        blurRadius = 24.dp
                                        backgroundColor = mutedPaletteBg
                                        tints = listOf(HazeTint(mutedPaletteBg.copy(alpha = 0.55f)))
                                    }
                                } else {
                                    Modifier.background(Color.Black)
                                },
                            ),
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .windowInsetsPadding(WindowInsets.statusBars),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RippleIconButton(
                                imageVector = SonoraIcons.ArrowBackIosNew,
                            ) {
                                navController.navigateUp()
                            }
                            SearchBar(
                                modifier =
                                    Modifier
                                        .height(50.dp)
                                        .padding(horizontal = 12.dp)
                                        .weight(1f),
                                colors =
                                    SearchBarDefaults.colors().copy(
                                        containerColor = Color.Transparent,
                                    ),
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
                                                    style = typo().bodyMedium,
                                                )
                                            },
                                        )
                                    }
                                },
                                expanded = false,
                                onExpandedChange = {},
                                windowInsets = WindowInsets(0, 0, 0, 0),
                            ) {
                            }
                            IconButton(
                                onClick = {
                                    showSearchBar = !showSearchBar
                                },
                            ) {
                                Icon(SonoraIcons.Close, null, tint = Color.White)
                            }
                        }
                    }
                }

                if (itemBottomSheetShow && currentItem != null) {
                    val track = currentItem?.toSongEntity() ?: return@Crossfade
                    NowPlayingBottomSheet(
                        onDismiss = {
                            itemBottomSheetShow = false
                            currentItem = null
                        },
                        navController = navController,
                        song = track,
                    )
                }
                if (playlistBottomSheetShow) {
                    Logger.w("PlaylistScreen", "PlaylistBottomSheet")
                    val addToQueue = {
                        viewModel.getFullTracks { track ->
                            sharedViewModel.addListToQueue(
                                track.toCollection(arrayListOf()),
                            )
                        }
                    }
                    PlaylistBottomSheet(
                        onDismiss = { playlistBottomSheetShow = false },
                        playlistId = data.id,
                        playlistName = data.title,
                        isYourYouTubePlaylist = isYourYouTubePlaylist && !data.isRadio,
                        onSaveToLocal = {
                            viewModel.getFullTracks { track ->
                                viewModel.saveToLocal(track)
                            }
                        },
                        onEditTitle = { newTitle ->
                            viewModel.updatePlaylistTitle(newTitle, data.id)
                        },
                        onAddToQueue = if (data.isRadio) null else addToQueue,
                    )
                }
                AnimatedVisibility(
                    visible = shouldHideTopBar && !showSearchBar,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically(),
                ) {
                    TopAppBar(
                        windowInsets =
                            TopAppBarDefaults.windowInsets.exclude(
                                TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Start),
                            ),
                        title = {
                            Text(
                                text = data.title,
                                style = typo().titleMedium,
                                maxLines = 1,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight(
                                            align = Alignment.CenterVertically,
                                        ).titleMarquee(
                                            iterations = Int.MAX_VALUE,
                                            animationMode = MarqueeAnimationMode.Immediately,
                                        ).focusable(),
                            )
                        },
                        navigationIcon = {
                            Box(Modifier.padding(horizontal = 5.dp)) {
                                RippleIconButton(
                                    SonoraIcons.ArrowBackIosNew,
                                    Modifier
                                        .size(32.dp),
                                    true,
                                ) {
                                    navController.navigateUp()
                                }
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    showSearchBar = !showSearchBar
                                },
                            ) {
                                Icon(SonoraIcons.Search, null, tint = Color.White)
                            }
                            // The primary action pins into the collapsed bar, so a long track list
                            // never puts the control the page exists for out of reach.
                            IconButton(
                                onClick = {
                                    if (isPlaying && playingPlaylistId == data.id) {
                                        sharedViewModel.onUIEvent(UIEvent.PlayPause)
                                    } else {
                                        viewModel.onUIEvent(PlaylistUIEvent.PlayAll)
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector =
                                        if (isPlaying && playingPlaylistId == data.id) {
                                            SonoraIcons.Pause
                                        } else {
                                            SonoraIcons.PlayArrow
                                        },
                                    contentDescription = "Play",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        },
                        colors =
                            TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                            ),
                        modifier =
                            if (isMobilePortrait) {
                                Modifier.hazeEffect(hazeState) {
                                    blurEnabled = true
                                    blurRadius = 24.dp
                                    backgroundColor = mutedPaletteBg
                                    tints = listOf(HazeTint(mutedPaletteBg.copy(alpha = 0.55f)))
                                }
                            } else {
                                Modifier.angledGradientBackground(listColors, 90f)
                            },
                    )
                }

                // Contextual bar for multi select. Sits above the normal top bar rather than
                // replacing it conditionally, so the two never fight over the same slot and the
                // count is always the topmost thing while the mode is on.
                AnimatedVisibility(
                    visible = selection.isActive,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically(),
                ) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "${selection.count} selected",
                                style = typo().titleMedium,
                                maxLines = 1,
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { selection.clear() }) {
                                Icon(
                                    imageVector = SonoraIcons.Close,
                                    contentDescription = "Cancel selection",
                                    tint = Color.White,
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { selection.selectAll(filteredTrack.map { it.videoId }) },
                            ) {
                                Icon(
                                    imageVector = SonoraIcons.Check,
                                    contentDescription = "Select all",
                                    tint = Color.White,
                                )
                            }
                            IconButton(
                                onClick = {
                                    viewModel.onUIEvent(
                                        PlaylistUIEvent.DownloadTracks(selection.selected.toList()),
                                    )
                                    selection.clear()
                                },
                            ) {
                                Icon(
                                    imageVector = SonoraIcons.DownloadForOffline,
                                    contentDescription = "Download selected",
                                    tint = Color.White,
                                )
                            }
                            IconButton(
                                onClick = {
                                    viewModel.onUIEvent(
                                        PlaylistUIEvent.LikeTracks(selection.selected.toList()),
                                    )
                                    selection.clear()
                                },
                            ) {
                                Icon(
                                    imageVector = SonoraIcons.Favorite,
                                    contentDescription = "Like selected",
                                    tint = Color.White,
                                )
                            }
                        },
                        colors =
                            TopAppBarDefaults.topAppBarColors(
                                containerColor = mutedPaletteBg,
                            ),
                    )
                }
            }

            is PlaylistUIState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CenterLoadingBox(
                        modifier = Modifier.size(80.dp),
                    )
                }
            }

            is PlaylistUIState.Error -> {
                viewModel.makeToast("Error: ${state.message}")
                navController.navigateUp()
            }
        }
    }
}