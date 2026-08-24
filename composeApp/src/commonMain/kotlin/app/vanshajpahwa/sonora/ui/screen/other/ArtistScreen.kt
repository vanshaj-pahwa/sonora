package app.vanshajpahwa.sonora.ui.screen.other

import androidx.compose.animation.AnimatedVisibility
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.MarqueeAnimationMode
import app.vanshajpahwa.sonora.ui.component.titleMarquee
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.model.home.Content
import app.vanshajpahwa.domain.data.model.searchResult.songs.Artist
import app.vanshajpahwa.domain.mediaservice.handler.PlaylistType
import app.vanshajpahwa.domain.mediaservice.handler.QueueData
import app.vanshajpahwa.domain.utils.toSongEntity
import app.vanshajpahwa.domain.utils.toTrack
import app.vanshajpahwa.sonora.Platform
import app.vanshajpahwa.sonora.expect.ui.MediaPlayerView
import app.vanshajpahwa.sonora.expect.ui.layerBackdrop
import app.vanshajpahwa.sonora.expect.ui.rememberBackdrop
import app.vanshajpahwa.sonora.expect.ui.toImageBitmap
import app.vanshajpahwa.sonora.extension.artworkScrimBrush
import app.vanshajpahwa.sonora.extension.getColorFromPalette
import app.vanshajpahwa.sonora.extension.getScreenSizeInfo
import app.vanshajpahwa.sonora.extension.getStringBlocking
import app.vanshajpahwa.sonora.extension.rgbFactor
import app.vanshajpahwa.sonora.extension.toImmersiveBackground
import app.vanshajpahwa.sonora.extension.toSquareThumbnailUrl
import app.vanshajpahwa.sonora.getPlatform
import app.vanshajpahwa.sonora.ui.component.CenterLoadingBox
import app.vanshajpahwa.sonora.ui.component.CollapsingToolbarParallaxEffect
import app.vanshajpahwa.sonora.ui.component.DetailCircleButton
import app.vanshajpahwa.sonora.ui.component.DetailActionRow
import app.vanshajpahwa.sonora.ui.component.rememberHolderPainter
import app.vanshajpahwa.sonora.ui.component.DescriptionView
import app.vanshajpahwa.sonora.ui.component.EndOfPage
import app.vanshajpahwa.sonora.ui.component.HomeItemArtist
import app.vanshajpahwa.sonora.ui.component.HomeItemContentPlaylist
import app.vanshajpahwa.sonora.ui.component.HomeItemVideo
import app.vanshajpahwa.sonora.ui.component.LimitedBorderAnimationView
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.sonora.ui.component.LiquidGlassIconButton
import app.vanshajpahwa.sonora.ui.component.NowPlayingBottomSheet
import app.vanshajpahwa.sonora.ui.component.SongFullWidthItems
import app.vanshajpahwa.sonora.ui.icon.ArrowBackIosNew
import app.vanshajpahwa.sonora.ui.icon.Check
import app.vanshajpahwa.sonora.ui.icon.PersonAdd
import app.vanshajpahwa.sonora.ui.icon.Sensors
import app.vanshajpahwa.sonora.ui.icon.Shuffle
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.navigation.destination.list.AlbumDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.ArtistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.MoreAlbumsDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.PlaylistDestination
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.ArtistScreenState
import app.vanshajpahwa.sonora.viewModel.ArtistViewModel
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.albums
import app.vanshajpahwa.sonora.resources.description
import app.vanshajpahwa.sonora.resources.error
import app.vanshajpahwa.sonora.resources.featured_inArtist
import app.vanshajpahwa.sonora.resources.follow
import app.vanshajpahwa.sonora.resources.followed
import app.vanshajpahwa.sonora.resources.no_description
import app.vanshajpahwa.sonora.resources.popular
import app.vanshajpahwa.sonora.resources.see_all
import app.vanshajpahwa.sonora.resources.related_artists
import app.vanshajpahwa.sonora.resources.singles
import app.vanshajpahwa.sonora.resources.start_radio
import app.vanshajpahwa.sonora.resources.unknown
import app.vanshajpahwa.sonora.resources.videos

@Composable
@ExperimentalMaterial3Api
fun ArtistScreen(
    channelId: String,
    viewModel: ArtistViewModel = koinViewModel(),
    sharedViewModel: SharedViewModel = koinInject(),
    navController: NavController,
) {
    val artistScreenState by viewModel.artistScreenState.collectAsStateWithLifecycle()
    val isFollowed by viewModel.followed.collectAsStateWithLifecycle()
    val canvasUrl by viewModel.canvasUrl.collectAsStateWithLifecycle()
    val artistLogo by viewModel.artistLogo.collectAsStateWithLifecycle()

    val playingTrack by remember {
        sharedViewModel.nowPlayingState.map { it?.track?.videoId }
    }.collectAsState(null)

    // Choosing song to show Bottom sheet
    var choosingTrack by remember {
        mutableStateOf<Track?>(null)
    }
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(channelId) {
        if (channelId != artistScreenState.data.channelId) {
            viewModel.browseArtist(channelId)
        }
    }

    // Apple Music-inspired immersive treatment: gated to mobile portrait so tablets,
    // foldable open state, landscape orientation, and Desktop keep the existing layout.
    val screenInfo = getScreenSizeInfo()
    val isMobilePortrait = getPlatform() == Platform.Android && screenInfo.wDP < screenInfo.hDP

    // Palette extraction from the artist artwork (portrait Apple-style only).
    val paletteState = com.kmpalette.rememberPaletteState()
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var paletteGeneratedFor by remember { mutableStateOf<String?>(null) }
    val currentImageUrl = (artistScreenState as? ArtistScreenState.Success)?.data?.imageUrl

    LaunchedEffect(bitmap) {
        val bm = bitmap
        if (bm != null && currentImageUrl != null && paletteGeneratedFor != currentImageUrl) {
            paletteState.generate(bm)
            paletteGeneratedFor = currentImageUrl
        }
    }

    // Apple Music-style page background from the artwork's dominant tone (see UIExt.toImmersiveBackground).
    val mutedPaletteBg = paletteState.palette.toImmersiveBackground()
    // Tint for the description card, matching the non-portrait CollapsingToolbar color.
    val sectionTint = paletteState.palette.getColorFromPalette()

    // Accent for the action row. Deliberately the app's primary rather than a colour pulled from
    // the artist's name-logo: a per-artist accent means the most important control on the screen
    // is a different colour on every artist, white for one and arbitrary for the next, which is a
    // lottery rather than a design system. This is the same token the nav pill and the now playing
    // rows use, so the artist page reads as the same product as the rest of the app.
    val artistAccent = MaterialTheme.colorScheme.primary

    val isLiquidGlassEnabled by sharedViewModel
        .getEnableLiquidGlass()
        .collectAsStateWithLifecycle(DataStoreManager.FALSE)

    val hazeState = rememberHazeState(blurEnabled = true)
    val lazyState = rememberLazyListState()
    val firstItemVisible by remember {
        derivedStateOf { lazyState.firstVisibleItemIndex == 0 }
    }
    var shouldHideTopBar by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(firstItemVisible) {
        shouldHideTopBar = !firstItemVisible
    }

    Crossfade(artistScreenState) { state ->
        when (state) {
            is ArtistScreenState.Loading -> {
                Box(Modifier.fillMaxSize()) {
                    CenterLoadingBox(
                        Modifier
                            .align(Alignment.Center),
                    )
                }
            }

            is ArtistScreenState.Success -> {
                if (isMobilePortrait) {
                    // ---- Apple Music style (mobile portrait only) ----
                    Box(Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .background(mutedPaletteBg)
                                    .hazeSource(hazeState),
                            state = lazyState,
                        ) {
                            item(contentType = "header") {
                                Column(
                                    // Negative spacing pulls the action row up into the header AND
                                    // shrinks the layout, so there's no leftover gap before "Popular"
                                    // (unlike Modifier.offset, which only moves pixels, not layout).
                                    verticalArrangement = Arrangement.spacedBy((-36).dp),
                                ) {
                                    // Edge-to-edge artwork (canvas plays on top of it when available).
                                    // Glass back button MUST be a sibling of the backdrop source
                                    // (not a child) to avoid render feedback loop / RuntimeShader crash.
                                    val artworkBackdrop = rememberBackdrop(Color.Black)
                                    // Haze state for the bottom progressive-blur fade (source = media layer).
                                    val headerHaze = rememberHazeState(blurEnabled = true)
                                    // Clamp the artist thumbnail URL to a square size (logic from
                                    // commit 5e596c5b) so it fills the square frame with FillWidth.
                                    val headerImageUrl = state.data.imageUrl?.toSquareThumbnailUrl()
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1f),
                                    ) {
                                        // Inner Box — backdrop SOURCE (artwork + canvas + overlays, NO glass)
                                        Box(modifier = Modifier.fillMaxSize().clipToBounds().layerBackdrop(artworkBackdrop)) {
                                            // Media layer (artwork + canvas) — Haze SOURCE for the bottom blur.
                                            Box(modifier = Modifier.fillMaxSize().hazeSource(headerHaze)) {
                                                AsyncImage(
                                                    model =
                                                        ImageRequest
                                                            .Builder(LocalPlatformContext.current)
                                                            .data(headerImageUrl)
                                                            .diskCachePolicy(CachePolicy.ENABLED)
                                                            .memoryCachePolicy(CachePolicy.ENABLED)
                                                            .diskCacheKey(headerImageUrl)
                                                            .memoryCacheKey(headerImageUrl)
                                                            .crossfade(false)
                                                            .build(),
                                                    placeholder = rememberHolderPainter(),
                                                    error = rememberHolderPainter(),
                                                    contentDescription = null,
                                                    contentScale = ContentScale.FillWidth,
                                                    // Always decoded so the page background color can be extracted
                                                    // from the artwork palette, even when a canvas is playing.
                                                    onSuccess = {
                                                        bitmap = it.result.image.toImageBitmap()
                                                    },
                                                    // Hidden (but still decoded above) while a canvas is present —
                                                    // the canvas is shown instead. No canvas -> artwork is shown.
                                                    modifier =
                                                        Modifier
                                                            .fillMaxSize()
                                                            .alpha(if (canvasUrl != null) 0f else 1f),
                                                )
                                                // Canvas (Spotify) plays AS the background when present;
                                                // otherwise the static artwork above is the fallback.
                                                canvasUrl?.let { canvas ->
                                                    // Canvas is a tall/portrait video. cropToBounds center
                                                    // scale-to-covers it into the square frame (ContentScale.Crop):
                                                    // true video aspect ratio, no stretch, overflow clipped.
                                                    MediaPlayerView(
                                                        url = canvas.first,
                                                        modifier = Modifier.fillMaxSize(),
                                                        cropToBounds = true,
                                                    )
                                                }
                                            } // end media layer (Haze source)
                                            // Bottom fade — progressive blur (Haze) over the media layer, so the
                                            // canvas/artwork edge melts into the page bg.
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .height(200.dp)
                                                        .align(Alignment.BottomCenter)
                                                        .hazeEffect(headerHaze) {
                                                            blurRadius = 32.dp
                                                            progressive =
                                                                HazeProgressive.verticalGradient(
                                                                    startIntensity = 0f,
                                                                    endIntensity = 1f,
                                                                )
                                                        },
                                            )
                                            // Color scrim is a SEPARATE, taller box: the blur stays at 200dp so
                                            // its cost doesn't grow, while the color gets 70% of the (square)
                                            // artwork to ramp over. A short ramp means a steep alpha, and a
                                            // steep alpha is what reads as a visible edge.
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .height((screenInfo.wDP * 0.7f).dp)
                                                        .align(Alignment.BottomCenter)
                                                        .background(artworkScrimBrush(mutedPaletteBg)),
                                            )
                                            // Artist name (TEXT for now — logo image is roadmap) + subscriber · view
                                            Column(
                                                modifier =
                                                    Modifier
                                                        .align(Alignment.BottomCenter)
                                                        // Lift the name + subtitle together with the action row.
                                                        .offset(y = (-36).dp)
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 16.dp)
                                                        .padding(bottom = 16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                            ) {
                                                val logo = artistLogo
                                                if (logo != null) {
                                                    // Artist name rendered as a logo image (hidden catalog),
                                                    // in place of the plain-text title.
                                                    AsyncImage(
                                                        model = logo.logoUrl,
                                                        contentDescription = state.data.title,
                                                        contentScale = ContentScale.Fit,
                                                        modifier =
                                                            Modifier
                                                                .fillMaxWidth(0.7f)
                                                                .heightIn(max = 84.dp),
                                                    )
                                                } else {
                                                    Text(
                                                        text = state.data.title ?: stringResource(Res.string.unknown),
                                                        style = typo().titleLarge,
                                                        color = Color.White,
                                                        maxLines = 2,
                                                        textAlign = TextAlign.Center,
                                                    )
                                                }
                                                val meta =
                                                    listOfNotNull(
                                                        state.data.subscribers?.takeIf { it.isNotBlank() },
                                                        state.data.playCount?.takeIf { it.isNotBlank() },
                                                    ).joinToString(" · ")
                                                if (meta.isNotBlank()) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = meta,
                                                        style = typo().bodyMedium,
                                                        color = Color(0xC4FFFFFF),
                                                        textAlign = TextAlign.Center,
                                                    )
                                                }
                                            }
                                        }
                                        // Back button. Glass only when the setting is on: this was
                                        // rendering glass unconditionally, so turning the toggle off
                                        // left one glass control on an otherwise flat screen.
                                        val backModifier =
                                            Modifier
                                                .align(Alignment.TopStart)
                                                .padding(12.dp)
                                                .windowInsetsPadding(WindowInsets.statusBars)
                                                .size(48.dp)
                                        if (isLiquidGlassEnabled == DataStoreManager.TRUE) {
                                            LiquidGlassIconButton(
                                                backdrop = artworkBackdrop,
                                                imageVector = SonoraIcons.ArrowBackIosNew,
                                                modifier = backModifier,
                                            ) {
                                                navController.navigateUp()
                                            }
                                        } else {
                                            IconButton(
                                                onClick = { navController.navigateUp() },
                                                modifier = backModifier,
                                            ) {
                                                Icon(
                                                    imageVector = SonoraIcons.ArrowBackIosNew,
                                                    contentDescription = "Back",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp),
                                                )
                                            }
                                        }
                                    }

                                    // Apple Music-style action row: [Radio][Shuffle pill][Follow] centered.
                                    // In Sonora "play" an artist == shuffle, so the big middle button is Shuffle.
                                    // Same cluster as album and playlist: a primary pill flanked by
                                    // circular secondaries. Playing an artist means shuffling them,
                                    // so Shuffle is the pill here rather than Play. Follow keeps its
                                    // filled state, which is information, while sharing the shape.
                                    DetailActionRow(
                                        isPlaying = false,
                                        playLabel = "Play",
                                        onPlayPause = { viewModel.onPlayPopularClick() },
                                        leading = {
                                            DetailCircleButton(
                                                icon = SonoraIcons.Sensors,
                                                contentDescription = "Radio",
                                                onClick = {
                                                    state.data.radioParam?.let { viewModel.onRadioClick(it) }
                                                },
                                            )
                                        },
                                        trailing = {
                                            DetailCircleButton(
                                                icon = if (isFollowed) SonoraIcons.Check else SonoraIcons.PersonAdd,
                                                contentDescription = if (isFollowed) "Followed" else "Follow",
                                                tint = if (isFollowed) Color.Black else Color.White,
                                                containerColor =
                                                    if (isFollowed) artistAccent else Color.White.copy(alpha = 0.12f),
                                                onClick = {
                                                    viewModel.updateFollowed(
                                                        if (isFollowed) 0 else 1,
                                                        state.data.channelId ?: return@DetailCircleButton,
                                                    )
                                                },
                                            )
                                        },
                                    )
                                }
                            }
                            item(contentType = "sections") {
                                ArtistSections(
                                    state = state,
                                    playingTrack = playingTrack,
                                    descriptionTint = sectionTint,
                                    navController = navController,
                                    viewModel = viewModel,
                                    sharedViewModel = sharedViewModel,
                                    onTrackMore = { track ->
                                        choosingTrack = track
                                        showBottomSheet = true
                                    },
                                )
                            }
                        }

                        // Haze top bar appears once the header scrolls away.
                        AnimatedVisibility(
                            visible = shouldHideTopBar,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically(),
                        ) {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = state.data.title ?: "",
                                        style = typo().titleMedium,
                                        maxLines = 1,
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .wrapContentHeight(align = Alignment.CenterVertically)
                                                .titleMarquee(
                                                    iterations = Int.MAX_VALUE,
                                                    animationMode = MarqueeAnimationMode.Immediately,
                                                ).focusable(),
                                    )
                                },
                                navigationIcon = {
                                    Box(Modifier.padding(horizontal = 5.dp)) {
                                        IconButton(onClick = { navController.navigateUp() }) {
                                            Icon(
                                                imageVector = SonoraIcons.ArrowBackIosNew,
                                                contentDescription = "Back",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp),
                                            )
                                        }
                                    }
                                },
                                // The primary action pins into the collapsed bar. Before this the
                                // bar carried only a title and a back arrow, so scrolling a long
                                // track list put the one control the page exists for out of reach
                                // and made getting back to it a scroll rather than a tap.
                                actions = {
                                    IconButton(
                                        onClick = {
                                            val param = state.data.shuffleParam
                                            if (param != null) viewModel.onShuffleClick(param)
                                        },
                                    ) {
                                        Icon(
                                            imageVector = SonoraIcons.Shuffle,
                                            contentDescription = "Shuffle",
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                },
                                colors =
                                    TopAppBarDefaults.topAppBarColors(
                                        containerColor = Color.Transparent,
                                    ),
                                modifier =
                                    Modifier.hazeEffect(hazeState) {
                                        blurEnabled = true
                                        blurRadius = 24.dp
                                        backgroundColor = mutedPaletteBg
                                        tints = listOf(HazeTint(mutedPaletteBg.copy(alpha = 0.55f)))
                                    },
                            )
                        }
                    }
                } else {
                    // ---- Existing layout (tablet / landscape / Desktop) ----
                    CollapsingToolbarParallaxEffect(
                        modifier = Modifier.fillMaxSize(),
                        title = state.data.title ?: "",
                        imageUrl = state.data.imageUrl,
                        onBack = {
                            navController.navigateUp()
                        },
                    ) { color ->
                        Column {
                            Column(
                                Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 16.dp)
                                    .padding(bottom = 8.dp),
                            ) {
                                Row {
                                    Text(
                                        text = state.data.subscribers ?: stringResource(Res.string.unknown),
                                        style = typo().bodySmall,
                                        color = Color.White,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.weight(1f),
                                    )
                                    Text(
                                        text = state.data.playCount ?: stringResource(Res.string.unknown),
                                        style = typo().bodySmall,
                                        color = Color.White,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    AnimatedVisibility(canvasUrl != null) {
                                        Row {
                                            val canvas = canvasUrl ?: return@Row
                                            LimitedBorderAnimationView(
                                                isAnimated = true,
                                                brush = Brush.sweepGradient(listOf(Color.Transparent, Color.White)),
                                                backgroundColor = Color.Transparent,
                                                contentPadding = 2.dp,
                                                borderWidth = 1.dp,
                                                shape = SonoraShapes.Container,
                                                oneCircleDurationMillis = 3000,
                                                interactionNumber = 1,
                                            ) {
                                                MediaPlayerView(
                                                    url = canvas.first,
                                                    modifier =
                                                        Modifier
                                                            .width(28.dp)
                                                            .height(ButtonDefaults.MinHeight)
                                                            .align(Alignment.CenterVertically)
                                                            .border(
                                                                width = 0.5.dp,
                                                                color =
                                                                    Color.White.copy(
                                                                        alpha = 0.8f,
                                                                    ),
                                                                shape = SonoraShapes.Container,
                                                            ).clip(SonoraShapes.Container)
                                                            .clickable {
                                                                val firstQueue: Track = canvas.second.toTrack()
                                                                viewModel.setQueueData(
                                                                    QueueData.Data(
                                                                        listTracks = arrayListOf(firstQueue),
                                                                        firstPlayedTrack = firstQueue,
                                                                        playlistId = "RDAMVM${firstQueue.videoId}",
                                                                        playlistName = "\"${(state.data.title ?: "")}\" ${
                                                                            getStringBlocking(
                                                                                Res.string.popular,
                                                                            )
                                                                        }",
                                                                        playlistType = PlaylistType.RADIO,
                                                                        continuation = null,
                                                                    ),
                                                                )
                                                                viewModel.loadMediaItem(
                                                                    firstQueue,
                                                                    type = Config.SONG_CLICK,
                                                                )
                                                            },
                                                )
                                            }
                                            Spacer(Modifier.width(12.dp))
                                        }
                                    }
                                    LimitedBorderAnimationView(
                                        isAnimated = !isFollowed,
                                        brush = Brush.sweepGradient(listOf(Color.Gray, Color.White)),
                                        backgroundColor = Color.Transparent,
                                        contentPadding = 0.dp,
                                        borderWidth = 2.dp,
                                        shape = ButtonDefaults.outlinedShape,
                                        oneCircleDurationMillis = 3000,
                                        interactionNumber = 1,
                                    ) {
                                        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                                            OutlinedButton(
                                                onClick = {
                                                    viewModel.updateFollowed(
                                                        if (isFollowed) 0 else 1,
                                                        state.data.channelId ?: return@OutlinedButton,
                                                    )
                                                },
                                                colors =
                                                    ButtonDefaults.outlinedButtonColors().copy(
                                                        contentColor = Color.White,
                                                        containerColor = Color.Transparent,
                                                    ),
                                            ) {
                                                if (isFollowed) {
                                                    Text(text = stringResource(Res.string.followed), color = Color.White)
                                                } else {
                                                    Text(text = stringResource(Res.string.follow), color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                    Spacer(Modifier.width(4.dp))
                                    IconButton(
                                        onClick = {
                                            if (state.data.shuffleParam != null) {
                                                viewModel.onShuffleClick(state.data.shuffleParam)
                                            } else {
                                                viewModel.makeToast(runBlocking { getString(Res.string.error) })
                                            }
                                        },
                                    ) {
                                        Icon(SonoraIcons.Shuffle, "Shuffle")
                                    }
                                    Spacer(Modifier.weight(1f))
                                    TextButton(
                                        onClick = {
                                            if (state.data.radioParam != null) {
                                                viewModel.onRadioClick(state.data.radioParam)
                                            } else {
                                                viewModel.makeToast(runBlocking { getString(Res.string.error) })
                                            }
                                        },
                                        colors =
                                            ButtonDefaults
                                                .textButtonColors()
                                                .copy(
                                                    contentColor = Color.White,
                                                ),
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Icon(SonoraIcons.Sensors, "")
                                            if (canvasUrl == null) {
                                                Spacer(Modifier.width(6.dp))
                                                Text(text = stringResource(Res.string.start_radio))
                                            }
                                        }
                                    }
                                }
                            }

                            ArtistSections(
                                state = state,
                                playingTrack = playingTrack,
                                descriptionTint = color,
                                navController = navController,
                                viewModel = viewModel,
                                sharedViewModel = sharedViewModel,
                                onTrackMore = { track ->
                                    choosingTrack = track
                                    showBottomSheet = true
                                },
                            )
                        }
                    }
                }

                if (showBottomSheet && choosingTrack != null) {
                    NowPlayingBottomSheet(
                        onDismiss = {
                            showBottomSheet = false
                            choosingTrack = null
                        },
                        navController = navController,
                        song = choosingTrack?.toSongEntity(),
                    )
                }
            }

            is ArtistScreenState.Error -> {
                viewModel.makeToast(state.message ?: stringResource(Res.string.error))
                navController.navigateUp()
            }
        }
    }
}

/**
 * Shared artist body (Popular → Description). Used by both the portrait Apple-Music layout
 * and the existing CollapsingToolbar layout, so the sections themselves stay untouched.
 */
@Composable
private fun ArtistSections(
    state: ArtistScreenState.Success,
    playingTrack: String?,
    descriptionTint: Color,
    navController: NavController,
    viewModel: ArtistViewModel,
    sharedViewModel: SharedViewModel,
    onTrackMore: (Track) -> Unit,
) {
    Column {
        // Popular Songs
        AnimatedVisibility(state.data.popularSongs.isNotEmpty()) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.popular),
                        style = typo().labelMedium,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        onClick = {
                            val id = state.data.listSongParam
                            if (id != null) {
                                navController.navigate(PlaylistDestination(id))
                            } else {
                                viewModel.makeToast(runBlocking { getString(Res.string.error) })
                            }
                        },
                        colors =
                            ButtonDefaults
                                .textButtonColors()
                                .copy(
                                    contentColor = Color.White,
                                ),
                    ) {
                        Text(stringResource(Res.string.see_all), style = typo().bodySmall)
                    }
                }
                state.data.popularSongs.forEach { song ->
                    SongFullWidthItems(
                        forceDark = true,
                        track = song,
                        isPlaying = song.videoId == playingTrack,
                        showDuration = true,
                        modifier = Modifier.fillMaxWidth(),
                        onMoreClickListener = {
                            onTrackMore(song)
                        },
                        onClickListener = {
                            val firstQueue: Track = song
                            viewModel.setQueueData(
                                QueueData.Data(
                                    listTracks = arrayListOf(firstQueue),
                                    firstPlayedTrack = firstQueue,
                                    playlistId = "RDAMVM${song.videoId}",
                                    playlistName = "\"${state.data.title ?: ""}\" ${getStringBlocking(Res.string.popular)}",
                                    playlistType = PlaylistType.RADIO,
                                    continuation = null,
                                ),
                            )
                            viewModel.loadMediaItem(
                                firstQueue,
                                type = Config.SONG_CLICK,
                            )
                        },
                        onAddToQueue = {
                            sharedViewModel.addListToQueue(
                                arrayListOf(song),
                            )
                        },
                    )
                }
            }
        }

        // Singles
        AnimatedVisibility(
            state.data.singles != null &&
                state.data.singles!!
                    .results
                    .isNotEmpty(),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.singles),
                        style = typo().labelMedium,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        onClick = {
                            if (state.data.channelId != null) {
                                val id = "MPAD${state.data.channelId}"
                                navController.navigate(
                                    MoreAlbumsDestination(
                                        id = id,
                                        type = MoreAlbumsDestination.SINGLE_TYPE,
                                    ),
                                )
                            } else {
                                viewModel.makeToast(getStringBlocking(Res.string.error))
                            }
                        },
                        colors =
                            ButtonDefaults
                                .textButtonColors()
                                .copy(
                                    contentColor = Color.White,
                                ),
                    ) {
                        Text(stringResource(Res.string.see_all), style = typo().bodySmall)
                    }
                }
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    item {
                        Spacer(Modifier.size(10.dp))
                    }
                    items(state.data.singles?.results ?: emptyList()) { single ->
                        HomeItemContentPlaylist(
                            forceDark = true,                            onClick = {
                                navController.navigate(
                                    AlbumDestination(
                                        single.browseId,
                                    ),
                                )
                            },
                            data = single,
                            thumbSize = 180.dp,
                        )
                    }
                    item {
                        Spacer(Modifier.size(10.dp))
                    }
                }
            }
        }

        // Albums
        AnimatedVisibility(
            state.data.albums != null &&
                state.data.albums!!
                    .results
                    .isNotEmpty(),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.albums),
                        style = typo().labelMedium,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        onClick = {
                            if (state.data.channelId != null) {
                                val id = "MPAD${state.data.channelId}"
                                navController.navigate(
                                    MoreAlbumsDestination(
                                        id = id,
                                        type = MoreAlbumsDestination.ALBUM_TYPE,
                                    ),
                                )
                            } else {
                                viewModel.makeToast(getStringBlocking(Res.string.error))
                            }
                        },
                        colors =
                            ButtonDefaults
                                .textButtonColors()
                                .copy(
                                    contentColor = Color.White,
                                ),
                    ) {
                        Text(stringResource(Res.string.see_all), style = typo().bodySmall)
                    }
                }
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    item {
                        Spacer(Modifier.size(10.dp))
                    }
                    items(state.data.albums?.results ?: emptyList()) { album ->
                        HomeItemContentPlaylist(
                            forceDark = true,                            onClick = {
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
                    item {
                        Spacer(Modifier.size(10.dp))
                    }
                }
            }
        }

        // Videos
        AnimatedVisibility(
            state.data.video != null &&
                state.data.video!!
                    .video
                    .isNotEmpty(),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.videos),
                        style = typo().labelMedium,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        onClick = {
                            val videoListParam = state.data.video?.videoListParam
                            if (videoListParam != null) {
                                navController.navigate(
                                    PlaylistDestination(
                                        videoListParam,
                                    ),
                                )
                            } else {
                                viewModel.makeToast(getStringBlocking(Res.string.error))
                            }
                        },
                        colors =
                            ButtonDefaults
                                .textButtonColors()
                                .copy(
                                    contentColor = Color.White,
                                ),
                    ) {
                        Text(stringResource(Res.string.see_all), style = typo().bodySmall)
                    }
                }
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    item {
                        Spacer(Modifier.size(10.dp))
                    }
                    items(state.data.video?.video ?: emptyList()) { video ->
                        HomeItemVideo(
                            forceDark = true,                            onClick = {
                                val firstQueue: Track = video
                                viewModel.setQueueData(
                                    QueueData.Data(
                                        listTracks = arrayListOf(firstQueue),
                                        firstPlayedTrack = firstQueue,
                                        playlistId = "RDAMVM${video.videoId}",
                                        playlistName = (state.data.title ?: "") + getStringBlocking(Res.string.videos),
                                        playlistType = PlaylistType.RADIO,
                                        continuation = null,
                                    ),
                                )
                                viewModel.loadMediaItem(
                                    firstQueue,
                                    type = Config.VIDEO_CLICK,
                                )
                            },
                            onLongClick = {
                                onTrackMore(video)
                            },
                            data =
                                Content(
                                    album = null,
                                    artists = video.artists,
                                    description = null,
                                    isExplicit = video.isExplicit,
                                    playlistId = null,
                                    browseId = null,
                                    thumbnails = video.thumbnails ?: emptyList(),
                                    title = video.title,
                                    videoId = video.videoId,
                                    views = video.videoType,
                                ),
                        )
                    }
                    item {
                        Spacer(Modifier.size(10.dp))
                    }
                }
            }
        }

        // Feature on
        AnimatedVisibility(state.data.featuredOn.isNotEmpty()) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.featured_inArtist),
                        style = typo().labelMedium,
                        color = Color.White,
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(vertical = 10.dp),
                    )
                }
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    item {
                        Spacer(Modifier.size(10.dp))
                    }
                    items(state.data.featuredOn) { feature ->
                        HomeItemContentPlaylist(
                            forceDark = true,                            onClick = {
                                navController.navigate(
                                    PlaylistDestination(
                                        feature.id,
                                    ),
                                )
                            },
                            data = feature,
                            thumbSize = 180.dp,
                        )
                    }
                    item {
                        Spacer(Modifier.size(10.dp))
                    }
                }
            }
        }

        // Related
        AnimatedVisibility(
            state.data.related != null &&
                state.data.related!!
                    .results
                    .isNotEmpty(),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.related_artists),
                        style = typo().labelMedium,
                        color = Color.White,
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(vertical = 10.dp),
                    )
                }
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    item {
                        Spacer(Modifier.size(10.dp))
                    }
                    items(state.data.related?.results ?: emptyList()) { related ->
                        HomeItemArtist(
                            forceDark = true,                            onClick = {
                                navController.navigate(
                                    ArtistDestination(
                                        channelId = related.browseId,
                                    ),
                                )
                            },
                            data =
                                Content(
                                    album = null,
                                    artists =
                                        listOf(
                                            Artist(
                                                id = related.browseId,
                                                name = related.title,
                                            ),
                                        ),
                                    description = related.subscribers,
                                    isExplicit = null,
                                    playlistId = null,
                                    browseId = related.browseId,
                                    thumbnails = related.thumbnails,
                                    title = related.title,
                                    videoId = null,
                                    views = null,
                                    durationSeconds = null,
                                    radio = null,
                                ),
                        )
                    }
                    item {
                        Spacer(Modifier.size(10.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(Res.string.description),
                style = typo().labelMedium,
                color = Color.White,
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp),
            )
        }
        val urlHandler = LocalUriHandler.current
        ElevatedCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            shape = SonoraShapes.Container,
            colors =
                CardDefaults.elevatedCardColors().copy(
                    containerColor = descriptionTint.rgbFactor(0.5f),
                ),
        ) {
            DescriptionView(
                modifier = Modifier.padding(16.dp),
                text = state.data.description ?: stringResource(Res.string.no_description),
                limitLine = 5,
                onTimeClicked = {},
                onURLClicked = { url ->
                    urlHandler.openUri(url)
                },
            )
        }
        EndOfPage()
    }
}