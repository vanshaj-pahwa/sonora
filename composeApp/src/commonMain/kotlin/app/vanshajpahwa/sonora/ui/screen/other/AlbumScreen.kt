package app.vanshajpahwa.sonora.ui.screen.other

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.domain.data.entities.DownloadState
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.utils.toSongEntity
import app.vanshajpahwa.sonora.Platform
import app.vanshajpahwa.sonora.expect.ui.layerBackdrop
import app.vanshajpahwa.sonora.expect.ui.rememberBackdrop
import app.vanshajpahwa.sonora.expect.ui.toImageBitmap
import app.vanshajpahwa.sonora.extension.angledGradientBackground
import app.vanshajpahwa.sonora.extension.artworkScrimBrush
import app.vanshajpahwa.sonora.extension.getColorFromPalette
import app.vanshajpahwa.sonora.extension.getScreenSizeInfo
import app.vanshajpahwa.sonora.extension.toImmersiveBackground
import app.vanshajpahwa.sonora.getPlatform
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.album
import app.vanshajpahwa.sonora.resources.baseline_downloaded
import app.vanshajpahwa.sonora.resources.downloaded
import app.vanshajpahwa.sonora.resources.downloading
import app.vanshajpahwa.sonora.resources.n_song
import app.vanshajpahwa.sonora.resources.search
import app.vanshajpahwa.sonora.resources.you_might_also_like
import app.vanshajpahwa.sonora.resources.year_and_category
import app.vanshajpahwa.sonora.ui.component.CenterLoadingBox
import app.vanshajpahwa.sonora.ui.component.ArtistChip
import app.vanshajpahwa.sonora.ui.component.DescriptionView
import app.vanshajpahwa.sonora.ui.component.DetailActionRow
import app.vanshajpahwa.sonora.ui.component.DetailCircleButton
import app.vanshajpahwa.sonora.ui.component.EndOfPage
import app.vanshajpahwa.sonora.ui.component.HeartCheckBox
import app.vanshajpahwa.sonora.ui.component.HomeItemContentPlaylist
import app.vanshajpahwa.sonora.ui.component.LiquidGlassIconButton
import app.vanshajpahwa.sonora.ui.component.NowPlayingBottomSheet
import app.vanshajpahwa.sonora.ui.component.PlaylistBottomSheet
import app.vanshajpahwa.sonora.ui.component.RippleIconButton
import app.vanshajpahwa.sonora.ui.component.SongFullWidthItems
import app.vanshajpahwa.sonora.ui.component.liquidGlass
import app.vanshajpahwa.sonora.ui.component.rememberHolderPainter
import app.vanshajpahwa.sonora.ui.component.titleMarquee
import app.vanshajpahwa.sonora.ui.icon.ArrowBackIosNew
import app.vanshajpahwa.sonora.ui.icon.Close
import app.vanshajpahwa.sonora.ui.icon.Search
import app.vanshajpahwa.sonora.ui.icon.DownloadForOffline
import app.vanshajpahwa.sonora.ui.icon.MoreVert
import app.vanshajpahwa.sonora.ui.icon.Pause
import app.vanshajpahwa.sonora.ui.icon.PauseCircle
import app.vanshajpahwa.sonora.ui.icon.PlayArrow
import app.vanshajpahwa.sonora.ui.icon.PlayCircle
import app.vanshajpahwa.sonora.ui.icon.Shuffle
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.navigation.destination.list.AlbumDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.ArtistDestination
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import app.vanshajpahwa.sonora.ui.theme.seed
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.AlbumViewModel
import app.vanshajpahwa.sonora.viewModel.LocalPlaylistState
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import app.vanshajpahwa.sonora.viewModel.UIEvent
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.toBitmap
import com.kmpalette.rememberPaletteState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    browseId: String,
    navController: NavController,
    viewModel: AlbumViewModel = koinViewModel(),
    sharedViewModel: SharedViewModel = koinInject(),
) {
    val uriHandler = LocalUriHandler.current

    val playingVideoId by viewModel.nowPlayingVideoId.collectAsStateWithLifecycle()

    val queueData by sharedViewModel.getQueueDataState().collectAsStateWithLifecycle()
    val playingPlaylistId by remember {
        derivedStateOf {
            queueData?.data?.playlistId
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val artistThumbnail by viewModel.artistThumbnail.collectAsStateWithLifecycle()
    val isLiquidGlassEnabled by sharedViewModel
        .getEnableLiquidGlass()
        .collectAsStateWithLifecycle(DataStoreManager.FALSE)
    val glassOn = isLiquidGlassEnabled == DataStoreManager.TRUE

    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    var albumBottomSheetShow by rememberSaveable { mutableStateOf(false) }
    var showSearchBar by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    val filteredTrack by remember(uiState.listTrack, query, showSearchBar) {
        derivedStateOf {
            if (query.isEmpty() || !showSearchBar) {
                uiState.listTrack
            } else {
                uiState.listTrack.filter {
                    it.title.contains(query, ignoreCase = true) ||
                        it.artists?.joinToString(", ")?.contains(query, ignoreCase = true) == true
                }
            }
        }
    }
    var chosenSong: Track? by remember { mutableStateOf(null) }

    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/downloading_animation.json").decodeToString(),
        )
    }

    LaunchedEffect(browseId) {
        viewModel.updateBrowseId(browseId)
    }

    val lazyState = rememberLazyListState()
    val firstItemVisible by remember {
        derivedStateOf {
            lazyState.firstVisibleItemIndex == 0
        }
    }
    var shouldHideTopBar by rememberSaveable { mutableStateOf(false) }
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

    LaunchedEffect(bitmap) {
        val bm = bitmap
        if (bm != null && paletteGeneratedFor != uiState.thumbnail) {
            paletteState.generate(bm)
            paletteGeneratedFor = uiState.thumbnail
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
    val dominantColor = uiState.colors.firstOrNull() ?: Color.Black
    // Apple Music-style page background from the artwork's dominant tone (see UIExt.toImmersiveBackground).
    val mutedPaletteBg = paletteState.palette.toImmersiveBackground()
    val artworkSizeDp =
        if (isMobilePortrait) {
            (screenInfo.wDP * 0.85f).coerceIn(280f, 380f).toInt()
        } else {
            250
        }

    Crossfade(uiState.loadState) {
        when (it) {
            LocalPlaylistState.PlaylistLoadState.Success -> {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(if (isMobilePortrait) mutedPaletteBg else Color.Black)
                            .hazeSource(hazeState),
                    state = lazyState,
                ) {
                    item(contentType = "header") {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .background(Color.Transparent),
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
                                                ).angledGradientBackground(uiState.colors, 25f),
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
                                    }
                                }
                                Column(
                                    horizontalAlignment = Alignment.Start,
                                ) {
                                    if (isMobilePortrait) {
                                        // Apple Music-style: edge-to-edge artwork (taller than square,
                                        // ~half screen height) with title overlay + liquid glass buttons.
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
                                                            .data(uiState.thumbnail)
                                                            .diskCachePolicy(CachePolicy.ENABLED)
                                                            .memoryCachePolicy(CachePolicy.ENABLED)
                                                            .diskCacheKey(uiState.thumbnail)
                                                            .memoryCacheKey(uiState.thumbnail)
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
                                                // Subtle bottom gradient — keeps artwork visible behind
                                                // the title text and blends artwork edge seamlessly into
                                                // the muted palette page background (Apple Music style).
                                                // Spans 70% of the artwork (not a fixed 200dp): the shorter
                                                // the ramp, the steeper the alpha, and a steep ramp is what
                                                // makes the fade read as an edge.
                                                Box(
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .height((screenInfo.hDP * 0.35f).dp)
                                                            .align(Alignment.BottomCenter)
                                                            .background(artworkScrimBrush(mutedPaletteBg)),
                                                )
                                                // Title/artist/year overlay (centered horizontally like Apple Music)
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
                                                        text = uiState.title,
                                                        style = typo().titleLarge,
                                                        color = Color.White,
                                                        maxLines = 2,
                                                        textAlign = TextAlign.Center,
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    ArtistChip(
                                                        name = uiState.artist.name,
                                                        thumbnail = artistThumbnail,
                                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                                        onClick = {
                                                            uiState.artist.id?.let { channelId ->
                                                                navController.navigate(
                                                                    ArtistDestination(channelId = channelId),
                                                                )
                                                            }
                                                        },
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        text =
                                                            stringResource(
                                                                Res.string.year_and_category,
                                                                uiState.year,
                                                                stringResource(Res.string.album),
                                                            ),
                                                        style = typo().bodyMedium,
                                                        color = Color(0xC4FFFFFF),
                                                    )
                                                }
                                            }
                                            // Back button over the artwork, matching the playlist and artist
                                            // headers. Sibling of the backdrop source, never a child of it.
                                            Row(
                                                modifier =
                                                    Modifier
                                                        .align(Alignment.TopStart)
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                                        .windowInsetsPadding(WindowInsets.statusBars),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                if (glassOn) {
                                                    LiquidGlassIconButton(
                                                        backdrop = artworkBackdrop,
                                                        imageVector = SonoraIcons.ArrowBackIosNew,
                                                        modifier = Modifier.size(48.dp),
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
                                                                // White glyphs on album art need a floor under
                                                                // them, so the non-glass fallback is a scrim
                                                                // rather than nothing.
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
                                                    Box(
                                                        modifier = Modifier.size(48.dp),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        HeartCheckBox(
                                                            size = 28,
                                                            checked = uiState.liked,
                                                            onStateChange = { viewModel.setAlbumLike() },
                                                        )
                                                    }
                                                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                                                        Icon(SonoraIcons.Search, null, tint = Color.White)
                                                    }
                                                    IconButton(onClick = { albumBottomSheetShow = true }) {
                                                        Icon(
                                                            imageVector = SonoraIcons.MoreVert,
                                                            contentDescription = "More",
                                                            tint = Color.White,
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        if (isMobilePortrait) {
                                            // Apple Music-style action row:
                                            // [Shuffle][Play pill][Download] (cluster centered, all 48dp matching size)
                                            val isThisPlaying =
                                                playingVideoId.isNotEmpty() &&
                                                    playingPlaylistId == browseId.replaceFirst("VL", "")
                                            DetailActionRow(
                                                isPlaying = isThisPlaying,
                                                playLabel = if (isThisPlaying) "Pause" else "Play",
                                                onPlayPause = {
                                                    if (isThisPlaying) {
                                                        sharedViewModel.onUIEvent(UIEvent.PlayPause)
                                                    } else {
                                                        uiState.listTrack.firstOrNull()?.let {
                                                            viewModel.playTrack(it)
                                                        }
                                                    }
                                                },
                                                leading = {
                                                    DetailCircleButton(
                                                        icon = SonoraIcons.Shuffle,
                                                        contentDescription = "Shuffle",
                                                        onClick = { viewModel.shuffle() },
                                                    )
                                                },
                                                trailing = {
                                                    Box(
                                                        modifier =
                                                            Modifier
                                                                .size(48.dp)
                                                                .clip(CircleShape)
                                                                .background(Color.White.copy(alpha = 0.12f)),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Crossfade(targetState = uiState.downloadState) { state ->
                                                            when (state) {
                                                                DownloadState.STATE_DOWNLOADED -> {
                                                                    Box(
                                                                        modifier =
                                                                            Modifier
                                                                                .fillMaxSize()
                                                                                .clickable {
                                                                                    viewModel.makeToast(
                                                                                        runBlocking {
                                                                                            getString(Res.string.downloaded)
                                                                                        },
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
                                                                                        runBlocking {
                                                                                            getString(Res.string.downloading)
                                                                                        },
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
                                                                                .clickable { viewModel.downloadFullAlbum() },
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
                                                },
                                            )
                                        } else {
                                            Row(
                                                modifier =
                                                    Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Crossfade(
                                                    playingVideoId.isNotEmpty() &&
                                                        playingPlaylistId == browseId.replaceFirst("VL", ""),
                                                ) { isThisPlaying ->
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
                                                            viewModel.playTrack(uiState.listTrack.firstOrNull() ?: return@RippleIconButton)
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.size(5.dp))
                                                Crossfade(targetState = uiState.downloadState) {
                                                    when (it) {
                                                        DownloadState.STATE_DOWNLOADED -> {
                                                            Box(
                                                                modifier =
                                                                    Modifier
                                                                        .size(36.dp)
                                                                        .clip(
                                                                            CircleShape,
                                                                        ).clickable {
                                                                            viewModel.makeToast(
                                                                                runBlocking {
                                                                                    getString(Res.string.downloaded)
                                                                                },
                                                                            )
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
                                                                            viewModel.makeToast(
                                                                                runBlocking {
                                                                                    getString(Res.string.downloading)
                                                                                },
                                                                            )
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
                                                                viewModel.downloadFullAlbum()
                                                            }
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.size(5.dp))
                                                HeartCheckBox(
                                                    size = 36,
                                                    checked = uiState.liked,
                                                    onStateChange = {
                                                        viewModel.setAlbumLike()
                                                    },
                                                )
                                                Spacer(Modifier.weight(1f))
                                                Spacer(Modifier.size(5.dp))
                                                RippleIconButton(
                                                    modifier =
                                                        Modifier.size(36.dp),
                                                    imageVector = SonoraIcons.Shuffle,
                                                    fillMaxSize = true,
                                                ) {
                                                    viewModel.shuffle()
                                                }
                                            }
                                        }
                                        // Nothing to say means nothing on screen. A row reading
                                        // "No description" spends a line reporting that it has
                                        // nothing to report.
                                        val albumDescription = uiState.description?.takeIf { it.isNotBlank() }
                                        if (albumDescription != null) {
                                        DescriptionView(
                                            text = albumDescription,
                                            onTimeClicked = { raw ->
                                                // Don't handle time click
                                            },
                                            onURLClicked = { url ->
                                                uriHandler.openUri(
                                                    url,
                                                )
                                            },
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        )
                                        }
                                        Text(
                                            text =
                                                pluralStringResource(
                                                    Res.plurals.n_song,
                                                    uiState.trackCount,
                                                    uiState.trackCount,
                                                ) +
                                                    // Only joined when there is something to
                                                    // join to. Passing an empty second value
                                                    // left the line ending in a stray separator.
                                                    uiState.length.takeIf { it.isNotBlank() }?.let { " · $it" }.orEmpty(),
                                            color = Color.White,
                                            style = typo().bodyMedium,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    items(count = filteredTrack.size, key = { index ->
                        val item = filteredTrack.getOrNull(index)
                        item?.videoId + "item_$index"
                    }) { index ->
                        val item = filteredTrack.getOrNull(index)
                        if (item != null) {
                            Column(modifier = Modifier.animateItem()) {
                                SongFullWidthItems(
                                    forceDark = true,
                                    isPlaying = item.videoId == playingVideoId,
                                    index = index,
                                    track = item,
                                    onMoreClickListener = {
                                        chosenSong = item
                                        showBottomSheet = true
                                    },
                                    onClickListener = {
                                        viewModel.playTrack(item)
                                    },
                                    onAddToQueue = {
                                        sharedViewModel.addListToQueue(
                                            arrayListOf(item),
                                        )
                                    },
                                    showDuration = true,
                                    modifier = Modifier,
                                )
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
                    item(contentType = "other_version") {
                        AnimatedVisibility(uiState.otherVersion.isNotEmpty()) {
                            Column {
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    // YouTube's own name for the shelf. It is only sometimes
                                    // other versions of this album, so the response decides.
                                    text =
                                        uiState.otherVersionTitle
                                            ?: stringResource(Res.string.you_might_also_like),
                                    style = typo().labelMedium,
                                    modifier =
                                        Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp,
                                        ),
                                )
                                LazyRow(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                ) {
                                    items(
                                        uiState.otherVersion.distinctBy { it.browseId },
                                        key = { it.browseId },
                                    ) { album ->
                                        HomeItemContentPlaylist(
                                            forceDark = true,
                                            onClick = {
                                                navController.navigate(
                                                    AlbumDestination(
                                                        browseId = album.browseId,
                                                    ),
                                                )
                                            },
                                            data = album,
                                            thumbSize = 180.dp,
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
                AnimatedVisibility(
                    visible = shouldHideTopBar,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically(),
                ) {
                    TopAppBar(
                        title = {
                            Text(
                                text = uiState.title,
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
                                Modifier.angledGradientBackground(uiState.colors, 90f)
                            },
                    )
                }
                AnimatedVisibility(
                    visible = showSearchBar,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically(),
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
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
                                    showSearchBar = false
                                    query = ""
                                },
                            ) {
                                Icon(SonoraIcons.Close, null, tint = Color.White)
                            }
                        }
                    }
                }
                if (showBottomSheet) {
                    NowPlayingBottomSheet(
                        onDismiss = {
                            showBottomSheet = false
                            chosenSong = null
                        },
                        navController = navController,
                        song = chosenSong?.toSongEntity(),
                    )
                }
                if (albumBottomSheetShow) {
                    PlaylistBottomSheet(
                        onDismiss = { albumBottomSheetShow = false },
                        playlistId = browseId,
                        playlistName = uiState.title,
                        isYourYouTubePlaylist = false,
                        onSaveToLocal = {},
                        onAddToQueue = {
                            sharedViewModel.addListToQueue(
                                uiState.listTrack.toCollection(arrayListOf()),
                            )
                        },
                    )
                }
            }

            LocalPlaylistState.PlaylistLoadState.Error -> {
                navController.navigateUp()
            }

            LocalPlaylistState.PlaylistLoadState.Loading -> {
                CenterLoadingBox(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}