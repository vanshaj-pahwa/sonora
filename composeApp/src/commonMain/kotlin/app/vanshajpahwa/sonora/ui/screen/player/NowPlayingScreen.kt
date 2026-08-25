@file:OptIn(ExperimentalMaterial3Api::class)

package app.vanshajpahwa.sonora.ui.screen.player

import androidx.compose.animation.Animatable
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import app.vanshajpahwa.sonora.ui.component.titleMarquee
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.vanshajpahwa.sonora.expect.isMotionReduced
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
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
import app.vanshajpahwa.common.Config.MAIN_PLAYER
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.domain.mediaservice.handler.MediaPlayerHandler
import app.vanshajpahwa.domain.mediaservice.handler.RepeatState
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.Platform
import app.vanshajpahwa.sonora.expect.toggleMiniPlayer
import app.vanshajpahwa.sonora.expect.ui.MediaPlayerView
import app.vanshajpahwa.sonora.expect.ui.MediaPlayerViewWithSubtitle
import app.vanshajpahwa.sonora.expect.ui.layerBackdrop
import app.vanshajpahwa.sonora.expect.ui.rememberBackdrop
import app.vanshajpahwa.sonora.expect.ui.toImageBitmap
import app.vanshajpahwa.sonora.extension.GradientAngle
import app.vanshajpahwa.sonora.extension.GradientOffset
import app.vanshajpahwa.sonora.extension.KeepScreenOn
import app.vanshajpahwa.sonora.extension.formatDuration
import app.vanshajpahwa.sonora.extension.getColorFromPalette
import app.vanshajpahwa.sonora.extension.getScreenSizeInfo
import app.vanshajpahwa.sonora.extension.hsvToColor
import app.vanshajpahwa.sonora.extension.isElementVisible
import app.vanshajpahwa.sonora.extension.parseTimestampToMilliseconds
import app.vanshajpahwa.sonora.extension.rememberIsInPipMode
import app.vanshajpahwa.sonora.extension.smoothScrimBrush
import app.vanshajpahwa.sonora.getPlatform
import app.vanshajpahwa.sonora.ui.component.SonoraSpinner
import app.vanshajpahwa.sonora.ui.component.AIBadge
import app.vanshajpahwa.sonora.ui.component.AddToPlaylistModalBottomSheet
import app.vanshajpahwa.sonora.ui.component.PlayerArtworkFrame
import app.vanshajpahwa.sonora.ui.component.WaveformTrack
import app.vanshajpahwa.sonora.ui.component.UpNextPeekBar
import app.vanshajpahwa.sonora.ui.component.DescriptionView
import app.vanshajpahwa.sonora.ui.component.ExplicitBadge
import app.vanshajpahwa.sonora.ui.component.FullscreenLyricsSheet
import app.vanshajpahwa.sonora.ui.component.HeartCheckBox
import app.vanshajpahwa.sonora.ui.component.OutputDeviceButton
import app.vanshajpahwa.sonora.ui.component.InfoPlayerBottomSheet
import app.vanshajpahwa.sonora.ui.component.LiquidGlassIconButton
import app.vanshajpahwa.sonora.ui.component.LyricsView
import app.vanshajpahwa.sonora.ui.component.NowPlayingBottomSheet
import app.vanshajpahwa.sonora.ui.component.PlayPauseButton
import app.vanshajpahwa.sonora.ui.component.PlayerControlLayout
import app.vanshajpahwa.sonora.ui.component.QueueBottomSheet
import app.vanshajpahwa.sonora.ui.component.VoteLyricsDialog
import app.vanshajpahwa.sonora.ui.component.liquidGlass
import app.vanshajpahwa.sonora.ui.component.rememberHolderPainter
import app.vanshajpahwa.sonora.ui.icon.Forward5
import app.vanshajpahwa.sonora.ui.icon.Fullscreen
import app.vanshajpahwa.sonora.ui.icon.Info
import app.vanshajpahwa.sonora.ui.icon.KeyboardArrowDown
import app.vanshajpahwa.sonora.ui.icon.MoreVert
import app.vanshajpahwa.sonora.ui.icon.PlaylistAdd
import app.vanshajpahwa.sonora.ui.icon.QueueMusic
import app.vanshajpahwa.sonora.ui.icon.Replay5
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.icon.Subtitles
import app.vanshajpahwa.sonora.ui.icon.SubtitlesOff
import app.vanshajpahwa.sonora.ui.icon.ThumbsUpDown
import app.vanshajpahwa.sonora.ui.navigation.destination.list.ArtistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.player.FullscreenDestination
import app.vanshajpahwa.sonora.ui.theme.blackMoreOverlay
import app.vanshajpahwa.sonora.ui.theme.overlay
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.LyricsProvider
import app.vanshajpahwa.sonora.viewModel.NowPlayingBottomSheetUIEvent
import app.vanshajpahwa.sonora.viewModel.NowPlayingBottomSheetViewModel
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import app.vanshajpahwa.sonora.viewModel.UIEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.artists
import app.vanshajpahwa.sonora.resources.crossfading
import app.vanshajpahwa.sonora.resources.description
import app.vanshajpahwa.sonora.resources.like_and_dislike
import app.vanshajpahwa.sonora.resources.line_synced
import app.vanshajpahwa.sonora.resources.hide
import app.vanshajpahwa.sonora.resources.lyrics
import app.vanshajpahwa.sonora.resources.lyrics_provider_betterlyrics
import app.vanshajpahwa.sonora.resources.lyrics_provider_lrc
import app.vanshajpahwa.sonora.resources.lyrics_provider_sonora
import app.vanshajpahwa.sonora.resources.lyrics_provider_youtube
import app.vanshajpahwa.sonora.resources.now_playing_upper
import app.vanshajpahwa.sonora.resources.offline_mode
import app.vanshajpahwa.sonora.resources.published_at
import app.vanshajpahwa.sonora.resources.rate_lyrics
import app.vanshajpahwa.sonora.resources.rich_synced
import app.vanshajpahwa.sonora.resources.show
import app.vanshajpahwa.sonora.resources.spotify_lyrics_provider
import app.vanshajpahwa.sonora.resources.unsynced
import app.vanshajpahwa.sonora.resources.view_count
import kotlin.math.roundToLong

/** Upper bound on the computed gap between artwork and the info block. */
// Cap on the gap either side of the artwork.
/** Extra room below the square artwork, which has no ring inset of its own to breathe into. */
private val SQUARE_ARTWORK_BOTTOM_GAP = 20.dp

private const val MAX_ARTWORK_GAP_DP = 28

// The same cap on the liquid-glass layout, which retunes the vertical rhythm: at 28 the title read
// as stranded well below the art while the waveform and transport below it were packed together, and
// the glass capsule sharpened that by giving the transport a visible edge. The 8dp taken back here
// is handed to the spacers under the title. The default layout keeps 28 and is not touched.
//
// Do not drop this below the line height of typo().labelSmall (14sp) — the current lyric line is
// centred in this gap and would be clipped.
private const val MAX_ARTWORK_GAP_GLASS_DP = 20

private const val TAG = "NowPlayingScreen"
private val RICH_SYNC_TIMESTAMP_REGEX = Regex("""<\d{2}:\d{2}\.\d{2,3}>\s*""")
private val WHITESPACE_REGEX = Regex("""\s+""")

// Word-by-word lyrics carry a timestamp per word; replace each with a space
// (not ""), then collapse — otherwise the words run together.
private fun String.stripRichSyncTimestamps(): String =
    replace(RICH_SYNC_TIMESTAMP_REGEX, " ")
        .replace(WHITESPACE_REGEX, " ")
        .trim()

// Backdrop behind the player. A dark surface rather than pure black: #000000 reads as a hole
// next to the artwork-tinted gradient and cards, which is why Spotify sits its player on a
// near-black surface instead. Used for the gradient's end colour, the fade-to target and the
// area below the gradient so all three match exactly and leave no seam.
private val PlayerBackdropColor = Color(0xFF121212)

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun NowPlayingScreen(
    sharedViewModel: SharedViewModel = koinInject(),
    navController: NavController,
    onDismiss: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

    val hideSheet: () -> Unit = {
        coroutineScope.launch {
            sheetState.hide()
            onDismiss()
        }
    }

    ModalBottomSheet(
        modifier =
            Modifier
                .fillMaxHeight(),
        onDismissRequest = {
            onDismiss()
        },
        containerColor = Color.Black,
        dragHandle = {},
        scrimColor = Color.Black.copy(alpha = .5f),
        sheetState = sheetState,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        shape = RectangleShape,
    ) {
        NowPlayingScreenContent(
            sharedViewModel = sharedViewModel,
            navController = navController,
            isExpanded = sheetState.currentValue == SheetValue.Expanded,
            dismissIcon = SonoraIcons.KeyboardArrowDown,
            onDismiss = {
                hideSheet()
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NowPlayingScreenContent(
    sharedViewModel: SharedViewModel = koinInject(),
    mediaPlayerHandler: MediaPlayerHandler = koinInject(),
    navController: NavController,
    isExpanded: Boolean,
    dismissIcon: ImageVector,
    onDismiss: () -> Unit = {},
) {
    val screenInfo = getScreenSizeInfo()

    val localDensity = LocalDensity.current
    val uriHandler = LocalUriHandler.current

    // ViewModel State
    val controllerState by sharedViewModel.controllerState.collectAsStateWithLifecycle()
    val screenDataState by sharedViewModel.nowPlayingScreenData.collectAsStateWithLifecycle()
    val blurredBackdrop by sharedViewModel.getBlurredPlayerBackdrop().collectAsStateWithLifecycle(false)
    val seekBarStyle by sharedViewModel.getSeekBarStyle().collectAsStateWithLifecycle(DataStoreManager.SEEK_BAR_WAVES)
    val spinArtwork by
        sharedViewModel
            .getSpinArtwork()
            .collectAsStateWithLifecycle(DataStoreManager.FALSE)
    val motionReduced = isMotionReduced()
    val artworkStyle by
        sharedViewModel
            .getPlayerArtworkStyle()
            .collectAsStateWithLifecycle(DataStoreManager.ARTWORK_STYLE_RING)

    // Liquid glass. The setting is offered on Android only (SettingScreen gates the row the same
    // way) and the JVM `liquidGlass` is a no-op clip, so the platform check keeps Desktop off the
    // whole code path rather than relying on that fallback.
    val liquidGlassSetting by sharedViewModel.getEnableLiquidGlass().collectAsStateWithLifecycle(DataStoreManager.FALSE)
    val useGlass = liquidGlassSetting == DataStoreManager.TRUE && getPlatform() == Platform.Android

    // Backdrop the glass surfaces refract.
    //
    // The source is the artwork pager (see `layerBackdrop` on it below), NOT this screen's root:
    // a glass surface nested inside its own backdrop source is a render-feedback loop that crashes
    // the RuntimeShader. The top bar and the whole control column are siblings of the pager, so
    // they can all read from it. Same arrangement as the Album/Playlist/Artist headers.
    val playerBackdrop = rememberBackdrop(PlayerBackdropColor)
    val timelineState by sharedViewModel.timeline.collectAsStateWithLifecycle()
    val likeStatus by sharedViewModel.likeStatus.collectAsStateWithLifecycle()

    val shouldShowVideo by sharedViewModel.getVideo.collectAsStateWithLifecycle()
    val translatedVoteState by sharedViewModel.translatedVoteState.collectAsStateWithLifecycle()
    val lyricsVoteState by sharedViewModel.lyricsVoteState.collectAsStateWithLifecycle()

    // Artwork Pager state — Spotify-style horizontal swipe between queue tracks.
    // The pager wraps the Canvas + Thumbnail layers. Controller layout below stays fixed.
    val nowPlayingState by sharedViewModel.nowPlayingState.collectAsStateWithLifecycle()
    val queueDataState by sharedViewModel.getQueueDataState().collectAsStateWithLifecycle()
    val artworkQueue by remember {
        derivedStateOf { queueDataState?.data?.listTracks ?: emptyList() }
    }
    // ⚠️ Use track.videoId (already prefix-stripped at MediaServiceHandlerImpl.kt:386).
    // Do NOT use mediaItem.mediaId — it carries the "Video" prefix for video items.
    val nowPlayingVideoId: String? = nowPlayingState?.track?.videoId
    val currentOrderIndex by remember(artworkQueue, nowPlayingVideoId) {
        derivedStateOf { deriveOrderIndex(artworkQueue, nowPlayingVideoId) }
    }
    val isRepeatOne = controllerState.repeatState is RepeatState.One
    // Single PagerState — the unified ArtworkPager renders BOTH the fullscreen canvas
    // background and the centered square thumbnail in each page, so we don't need two
    // pagers + state mirroring.
    val artworkPagerState =
        rememberPagerState(
            initialPage = currentOrderIndex.coerceAtLeast(0),
            pageCount = { artworkQueue.size.coerceAtLeast(1) },
        )
    var isAnimatingFromPlayer by remember { mutableStateOf(false) }
    var isUserDraggingActive by remember { mutableStateOf(false) }

    // Drag detection — `isScrollInProgress` is `true` for both user drags (forwarded
    // by the outer Modifier.scrollable on the Column) and programmatic
    // `animateScrollToPage`. We disambiguate via `isAnimatingFromPlayer`, which we
    // set explicitly around the player → pager animation (try/finally).
    LaunchedEffect(artworkPagerState) {
        snapshotFlow {
            artworkPagerState.isScrollInProgress to isAnimatingFromPlayer
        }.collect { (scrolling, animating) ->
            isUserDraggingActive = scrolling && !animating
        }
    }

    // ① Player → Pager: animate to new track when player advances.
    LaunchedEffect(currentOrderIndex, artworkQueue.size) {
        val target = currentOrderIndex
        if (!isUserDraggingActive &&
            artworkQueue.isNotEmpty() &&
            target in 0 until artworkQueue.size &&
            target != artworkPagerState.currentPage
        ) {
            isAnimatingFromPlayer = true
            try {
                artworkPagerState.animateScrollToPage(target)
            } finally {
                isAnimatingFromPlayer = false
            }
        }
    }

    // ② Pager → Player: seek when user settles on a different page.
    // Adjacent (±1) → Next/Previous (preserves crossfade flow on Android).
    // Far skip → playMediaItemInMediaSource (handles unshuffling internally).
    LaunchedEffect(artworkPagerState, currentOrderIndex, artworkQueue.size) {
        snapshotFlow { artworkPagerState.settledPage }
            .distinctUntilChanged()
            .collect { settled ->
                if (isAnimatingFromPlayer) return@collect
                if (artworkQueue.isEmpty()) return@collect
                if (settled !in 0 until artworkQueue.size) return@collect
                if (settled == currentOrderIndex) return@collect

                runCatching {
                    when (val action = computeSeekAction(settled, currentOrderIndex)) {
                        ArtworkSeekAction.Next -> {
                            sharedViewModel.onUIEvent(UIEvent.Next)
                        }
                        // Use SkipToPrevious so a swipe always goes to the previous track —
                        // UIEvent.Previous would seek to 0 of the current track once the
                        // playhead has passed the 3-second mark.
                        ArtworkSeekAction.Previous -> {
                            sharedViewModel.onUIEvent(UIEvent.SkipToPrevious)
                        }
                        is ArtworkSeekAction.Skip -> {
                            mediaPlayerHandler.playMediaItemInMediaSource(action.index)
                        }
                        ArtworkSeekAction.NoOp -> {
                            Unit
                        }
                    }
                }.onFailure { error ->
                    Logger.w(TAG, "ArtworkPager seek failed: ${error.message}")
                }
            }
    }

    // ③ Queue mutation guard — when queue shrinks below currentPage, scroll to last index
    // to avoid IndexOutOfBoundsException during recomposition.
    LaunchedEffect(artworkQueue.size) {
        if (artworkQueue.isNotEmpty() && artworkPagerState.currentPage >= artworkQueue.size) {
            runCatching { artworkPagerState.scrollToPage(artworkQueue.lastIndex) }
        }
    }

    // State
    val isInPipMode = rememberIsInPipMode()

    val mainScrollState = rememberScrollState()

    var showHideMiddleLayout by rememberSaveable {
        mutableStateOf(true)
    }

    var showSheet by rememberSaveable {
        mutableStateOf(false)
    }

    var showFullscreenLyrics by rememberSaveable {
        mutableStateOf(false)
    }

    var showQueueBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    var showInfoBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    var showVoteDialog by rememberSaveable {
        mutableStateOf(false)
    }

    // NEW: Add to Playlist state
    var showAddToPlaylistDirectly by rememberSaveable {
        mutableStateOf(false)
    }

    var shouldShowToolbar by remember {
        mutableStateOf(false)
    }

    // Palette state
    val paletteState = rememberPaletteState()

    val startColor =
        remember {
            Animatable(Color.Black)
        }
    val endColor =
        remember {
            Animatable(Color.Black)
        }
    val gradientOffset by remember {
        mutableStateOf(GradientOffset(GradientAngle.CW135))
    }

    var spotShadowColor by remember {
        mutableStateOf(Color.White)
    }

    LaunchedEffect(screenDataState) {
        Logger.d(TAG, "ScreenDataState: $screenDataState")
        showHideMiddleLayout = screenDataState.canvasData == null
        snapshotFlow { screenDataState.bitmap }.collectLatest {
            if (it != null) {
                paletteState.generate(it)
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { paletteState.palette }
            .distinctUntilChanged()
            .collectLatest {
                spotShadowColor = it.getColorFromPalette()
                startColor.animateTo(it.getColorFromPalette())
                // Lands on the same backdrop colour the fade and the area below the gradient
                // use, so the palette ramp resolves into the surface instead of a black patch.
                endColor.animateTo(PlayerBackdropColor)
            }
    }

    LaunchedEffect(spotShadowColor) {
        Logger.d(TAG, "spotShadowColor: $spotShadowColor")
    }
    // Height
    var topAppBarHeightDp by rememberSaveable {
        mutableIntStateOf(0)
    }
    var middleLayoutHeightDp by rememberSaveable {
        mutableIntStateOf(0)
    }
    var infoLayoutHeightDp by rememberSaveable {
        mutableIntStateOf(0)
    }
    var middleLayoutPaddingDp by rememberSaveable {
        mutableIntStateOf(0)
    }
    val minimumPaddingDp by rememberSaveable {
        mutableIntStateOf(
            30,
        )
    }
    LaunchedEffect(
        topAppBarHeightDp,
        screenInfo,
        infoLayoutHeightDp,
        minimumPaddingDp,
        // Keyed so the gap is recomputed when the glass setting is toggled while the player is open,
        // since the two layouts cap it differently.
        useGlass,
    ) {
        if (topAppBarHeightDp > 0 && middleLayoutHeightDp > 0 && infoLayoutHeightDp > 0 && screenInfo.hDP > 0) {
            val result = (screenInfo.hDP - topAppBarHeightDp - middleLayoutHeightDp - infoLayoutHeightDp - minimumPaddingDp) / 2
            middleLayoutPaddingDp =
                if (result > minimumPaddingDp) {
                    // Capped. This gap is whatever vertical space is left over, halved, so it grows
                    // to absorb anything removed below it: dropping the separate slider container
                    // and timestamp row shrank infoLayoutHeightDp and the gap swallowed the
                    // difference, leaving the title stranded far under the artwork.
                    result.coerceAtMost(if (useGlass) MAX_ARTWORK_GAP_GLASS_DP else MAX_ARTWORK_GAP_DP)
                } else {
                    minimumPaddingDp
                }
        }
    }

    var isSliding by rememberSaveable {
        mutableStateOf(false)
    }
    var sliderValue by rememberSaveable {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(key1 = timelineState, key2 = isSliding) {
        if (!isSliding) {
            sliderValue =
                if (timelineState.total > 0L) {
                    timelineState.current.toFloat() * 100 / timelineState.total.toFloat()
                } else {
                    0f
                }
        }
    }

    // Crossfade: RGB rainbow color cycling when transitioning between tracks
    val infiniteTransition = rememberInfiniteTransition(label = "crossfadeRainbow")
    val rainbowHue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "rainbowHue",
    )
    val rainbowColor = hsvToColor(rainbowHue, 1f, 1f)
    val sliderTrackColor by animateColorAsState(
        targetValue = if (timelineState.isCrossfading) rainbowColor else Color.White,
        animationSpec = tween(300),
        label = "sliderCrossfadeColor",
    )

    // Show ControlLayout Or Show Artist Badge
    var showHideControlLayout by rememberSaveable {
        mutableStateOf(true)
    }
    val controlLayoutAlpha: Float by animateFloatAsState(
        targetValue = if (showHideControlLayout) 1f else 0f,
        animationSpec =
            tween(
                durationMillis = 500,
                easing = LinearEasing,
            ),
        label = "ControlLayoutAlpha",
    )

    var showHideJob by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = showHideJob) {
        if (!showHideJob) {
            delay(5000)
            if (mainScrollState.value == 0) showHideControlLayout = false
            showHideJob = true
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            screenDataState
        }.distinctUntilChangedBy {
            it.canvasData?.url
        }.collectLatest {
            if (it.canvasData != null && mainScrollState.value == 0) {
                showHideJob = false
            } else {
                showHideJob = true
                showHideControlLayout = true
            }
        }
    }

    LaunchedEffect(key1 = showHideControlLayout) {
        if (showHideControlLayout && screenDataState.canvasData != null && mainScrollState.value == 0) {
            showHideJob = false
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { mainScrollState.value }
            .distinctUntilChanged()
            .collect {
                if (it > 0 && !showHideControlLayout && screenDataState.canvasData != null) {
                    showHideJob = true
                    showHideControlLayout = true
                } else if (showHideControlLayout && it == 0 && screenDataState.canvasData != null) {
                    showHideJob = false
                }
            }
    }

    // Fullscreen overlay
    var showHideFullscreenOverlay by rememberSaveable {
        mutableStateOf(false)
    }

    var currentLyricLineIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(key1 = showHideFullscreenOverlay) {
        if (showHideFullscreenOverlay) {
            delay(3000)
            showHideFullscreenOverlay = false
        }
    }

    // Canvas subtitle sync
    LaunchedEffect(timelineState, screenDataState.lyricsData?.lyrics) {
        val lyrics = screenDataState.lyricsData?.lyrics
        if (lyrics == null || lyrics.syncType == "UNSYNCED" || lyrics.syncType == null) {
            currentLyricLineIndex = -1
            return@LaunchedEffect
        }
        val lines = lyrics.lines ?: return@LaunchedEffect
        val translatedLines =
            screenDataState.lyricsData
                ?.translatedLyrics
                ?.first
                ?.lines
        if (timelineState.current > 0L) {
            lines.indices.forEach { i ->
                val startTimeMs = lines[i].startTimeMs.toLongOrNull() ?: 0L
                val endTimeMs =
                    if (i < lines.size - 1) {
                        lines[i + 1].startTimeMs.toLongOrNull() ?: 0L
                    } else {
                        startTimeMs + 60000
                    }
                if (timelineState.current in startTimeMs..endTimeMs) {
                    currentLyricLineIndex = i
                }
            }
            if (lines.isNotEmpty() &&
                timelineState.current in 0..(lines.getOrNull(0)?.startTimeMs?.toLongOrNull() ?: 0L)
            ) {
                currentLyricLineIndex = -1
            }
        } else {
            currentLyricLineIndex = -1
        }
    }

    if (showSheet) {
        NowPlayingBottomSheet(
            onDismiss = {
                showSheet = false
            },
            navController = navController,
            onNavigateToOtherScreen = {
                onDismiss()
            },
            song = null, // Auto set now playing
            setSleepTimerEnable = true,
            changeMainLyricsProviderEnable = true,
        )
    }

    if (showFullscreenLyrics) {
        FullscreenLyricsSheet(
            sharedViewModel = sharedViewModel,
            navController = navController, // <-- ADD THIS LINE
            color = startColor.value,
        ) {
            showFullscreenLyrics = false
        }
    }

    if (showQueueBottomSheet) {
        QueueBottomSheet(
            onDismiss = {
                showQueueBottomSheet = false
            },
        )
    }

    if (showInfoBottomSheet) {
        InfoPlayerBottomSheet(
            onDismiss = {
                showInfoBottomSheet = false
            },
        )
    }

    // NEW: Add to Playlist Bottom Sheet
    if (showAddToPlaylistDirectly) {
        val viewModel: NowPlayingBottomSheetViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.resetPlaylists()
            viewModel.setSongEntity(null) // Uses current playing song
        }

        AddToPlaylistModalBottomSheet(
            isBottomSheetVisible = true,
            listLocalPlaylist = uiState.listLocalPlaylist,
            listYouTubePlaylist = uiState.listYouTubePlaylist,
            onDismiss = { showAddToPlaylistDirectly = false },
            onClick = { playlist ->
                viewModel.onUIEvent(NowPlayingBottomSheetUIEvent.AddToPlaylist(playlist.id))
                showAddToPlaylistDirectly = false
            },
            onYTPlaylistClick = { playlist ->
                viewModel.onUIEvent(NowPlayingBottomSheetUIEvent.AddToYouTubePlaylist(playlist.browseId))
                showAddToPlaylistDirectly = false
            },
            videoId = uiState.songUIState.videoId,
        )
    }

    // Vote Dialog
    if (showVoteDialog) {
        val canVoteLyrics =
            screenDataState.lyricsData?.lyricsProvider == LyricsProvider.SONORA &&
                screenDataState.lyricsData
                    ?.lyrics
                    ?.sonoraLyrics != null
        val canVoteTranslatedLyrics =
            screenDataState.lyricsData?.translatedLyrics?.second == LyricsProvider.SONORA &&
                screenDataState.lyricsData
                    ?.translatedLyrics
                    ?.first
                    ?.sonoraLyrics != null

        VoteLyricsDialog(
            canVoteLyrics = canVoteLyrics,
            canVoteTranslatedLyrics = canVoteTranslatedLyrics,
            lyricsVoteState = lyricsVoteState,
            translatedLyricsVoteState = translatedVoteState,
            onVoteLyrics = { upvote ->
                sharedViewModel.voteLyrics(upvote)
            },
            onVoteTranslatedLyrics = { upvote ->
                sharedViewModel.voteTranslatedLyrics(upvote)
            },
            onDismiss = {
                showVoteDialog = false
            },
        )
    }

    if (screenDataState.lyricsData != null && controllerState.isPlaying) {
        KeepScreenOn()
    }
    // The palette gradient that fills the first screen height, lifted out of the scrolling Column so
    // the backdrop source can paint it too. Glass sampling only sees the source subtree, and without
    // this the panels would refract a flat colour and read as plain dark rectangles.
    //
    // Drawing it in both places is not a double exposure: the palette gradient underneath is fully
    // opaque and both nodes share an origin and a size, so the second pass lands on identical
    // pixels. Two drawRects, and only while glass is on.
    val paletteGradient =
        Modifier.drawBehind {
            val gradientHeight = screenInfo.hPX.toFloat()
            val area = Size(size.width, gradientHeight)
            // Palette gradient, keeping its diagonal angle.
            drawRect(
                brush =
                    Brush.linearGradient(
                        colors =
                            listOf(
                                startColor.value,
                                endColor.value,
                            ),
                        start = gradientOffset.start,
                        end = gradientOffset.end,
                    ),
                size = area,
            )
            // Vertical fade to the backdrop colour, fully opaque from 90% down, so the bottom edge
            // meets the area underneath seamlessly across the whole width. Adding the same colour as
            // a stop to the diagonal gradient above could not do that — it would only arrive in one
            // corner and leave a visible diagonal seam.
            drawRect(
                brush =
                    smoothScrimBrush(
                        from = PlayerBackdropColor.copy(alpha = 0f),
                        to = PlayerBackdropColor,
                        startY = 0f,
                        // Reaches full opacity at 95% and is held there by Clamp, same as the old
                        // `0.95f to PlayerBackdropColor` stop.
                        endY = gradientHeight * 0.95f,
                    ),
                size = area,
            )
        }

    Box(modifier = Modifier.background(PlayerBackdropColor)) {
        // Blurred-artwork backdrop, behind everything, only for the first screen height.
        //
        // Modifier.blur needs API 31; below that it is a no-op, so the heavy scrim below is what
        // keeps this looking deliberate rather than broken on older devices: an over-scaled, very
        // dark wash of the art rather than a sharp photo behind the controls.
        if (blurredBackdrop) {
            screenDataState.bitmap?.let { art ->
                Image(
                    bitmap = art,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(screenInfo.hDP.dp)
                            .blur(56.dp),
                )
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(screenInfo.hDP.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        PlayerBackdropColor.copy(alpha = 0.35f),
                                        PlayerBackdropColor.copy(alpha = 0.65f),
                                        PlayerBackdropColor,
                                    ),
                                ),
                            ),
                )
            }
        }
        Column(
            Modifier
                .verticalScroll(
                    mainScrollState,
                    enabled = isExpanded,
                )
                // Horizontal swipe is handled by the unified ArtworkPager below.
                // Spacers in this Column have no pointer input and don't block hits, so
                // drags fall through to the Pager.
                .then(
                    if (showHideMiddleLayout && !blurredBackdrop) {
                        Modifier
                            // The backdrop fills the whole scrollable content, then the gradient
                            // is drawn over just the first screen height. Using background() for
                            // the gradient instead would stretch it across the entire content,
                            // which is what made it run on forever while scrolling.
                            .background(PlayerBackdropColor)
                            .then(paletteGradient)
                    } else if (blurredBackdrop) {
                        // Transparent: the blurred art behind this Column is the backdrop now.
                        Modifier
                    } else {
                        Modifier.background(Color.Black)
                    },
                ),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // === Unified ArtworkPager (Spotify-style swipe) ===
                // ONE HorizontalPager wraps both the fullscreen canvas backdrop AND the
                // centered square thumbnail. Both layers slide together as a single page
                // so when the user swipes during canvas mode, they see the next track's
                // thumbnail enter and the canvas exit in lockstep.
                HorizontalPager(
                    state = artworkPagerState,
                    modifier =
                        Modifier
                            .height(screenInfo.hDP.dp)
                            .fillMaxWidth()
                            .then(
                                if (useGlass) {
                                    // Backdrop SOURCE for every glass surface on this screen.
                                    //
                                    // layerBackdrop comes first so the gradient chained after it is
                                    // recorded too; the artwork alone would leave the panels below
                                    // the art refracting nothing. Nothing carrying glass may be
                                    // placed inside this subtree.
                                    Modifier
                                        .layerBackdrop(playerBackdrop)
                                        .then(
                                            if (showHideMiddleLayout && !blurredBackdrop) {
                                                paletteGradient
                                            } else {
                                                // Blurred-artwork mode paints its backdrop in the
                                                // screen root, outside this source. Recreating it
                                                // here would mean a second 56.dp blur pass every
                                                // frame, so the glass falls back to the flat
                                                // backdrop colour instead. Still legible, just
                                                // flatter than over the gradient.
                                                Modifier
                                            },
                                        )
                                } else {
                                    Modifier
                                },
                            ),
                    beyondViewportPageCount = 1,
                    userScrollEnabled = !isRepeatOne && artworkQueue.isNotEmpty(),
                    key = { idx ->
                        val vid = artworkQueue.getOrNull(idx)?.videoId.orEmpty()
                        "artwork_${vid}_$idx"
                    },
                ) { page ->
                    val pageTrack = artworkQueue.getOrNull(page)
                    val isCurrentArtworkPage = page == currentOrderIndex
                    val pageHasCanvas = isCurrentArtworkPage && screenDataState.canvasData != null

                    // Per-page palette state for the gradient backdrop.
                    // The bitmap is fed in by Layer 2's adjacent-thumbnail AsyncImage
                    // (onSuccess), so we use the SAME bitmap that's painted on screen —
                    // matches the outer Column's palette extraction characteristics.
                    val pagePaletteState = rememberPaletteState()
                    val pageStartColor =
                        remember(pageTrack?.videoId) {
                            Animatable(Color.Black)
                        }
                    LaunchedEffect(pagePaletteState, pageTrack?.videoId) {
                        snapshotFlow { pagePaletteState.palette }
                            .distinctUntilChanged()
                            .collectLatest { palette ->
                                pageStartColor.animateTo(
                                    palette.getColorFromPalette(),
                                )
                            }
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                // Prevent the canvas video (9:16 aspect, can be wider than the
                                // page) and any other content from bleeding into adjacent pages.
                                .clipToBounds()
                                // Tap toggles controls only when the canvas is covering this page;
                                // otherwise no-op (matches the legacy behaviour where the touch
                                // overlay only appeared in canvas mode).
                                .clickable(
                                    enabled = pageHasCanvas,
                                    onClick = {
                                        if (mainScrollState.value == 0) {
                                            showHideJob = true
                                            showHideControlLayout = !showHideControlLayout
                                        }
                                    },
                                    indication = null,
                                    interactionSource =
                                        remember {
                                            MutableInteractionSource()
                                        },
                                ),
                    ) {
                        // ── Layer 0: per-page backdrop (adjacent pages only) ──
                        // Palette gradient (startColor → endColor) so the adjacent page never
                        // falls back to a flat dark void during a swipe.
                        // The CURRENT page deliberately skips this layer so the existing
                        // gradient / canvas on the Column stays visible.
                        if (!isCurrentArtworkPage && pageTrack != null) {
                            // Palette is fed by Layer 2's adjacent-thumbnail AsyncImage
                            // (see below) so the gradient color stays consistent with
                            // the bitmap actually painted for that page.
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                colors =
                                                    listOf(
                                                        pageStartColor.value,
                                                        Color.Black,
                                                    ),
                                                start = gradientOffset.start,
                                                end = gradientOffset.end,
                                            ),
                                        ),
                            )
                        }

                        // ── Layer 1: fullscreen canvas backdrop (current track + canvas data) ──
                        if (pageHasCanvas) {
                            Crossfade(targetState = screenDataState.canvasData?.isVideo) { isVideo ->
                                if (isVideo == true) {
                                    screenDataState.canvasData?.url?.let { url ->
                                        MediaPlayerView(
                                            url = url,
                                            modifier =
                                                Modifier
                                                    .fillMaxHeight()
                                                    .then(
                                                        if (getPlatform() == Platform.Desktop) {
                                                            Modifier
                                                        } else {
                                                            Modifier
                                                                .wrapContentWidth(unbounded = true, align = Alignment.CenterHorizontally)
                                                                .align(Alignment.Center)
                                                        },
                                                    ),
                                        )
                                    }
                                } else if (isVideo == false) {
                                    AsyncImage(
                                        model =
                                            ImageRequest
                                                .Builder(LocalPlatformContext.current)
                                                .data(screenDataState.canvasData?.url)
                                                .diskCachePolicy(CachePolicy.ENABLED)
                                                .diskCacheKey(screenDataState.canvasData?.url)
                                                .crossfade(550)
                                                .build(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }
                            }
                            // Bottom gradient overlay — different intensity per state:
                            // - Focus: original full-height heavy gradient (controls readability)
                            // - Unfocus: 50% height + 50% lighter colors (just enough for metadata,
                            //   lets more canvas show through)
                            Crossfade(
                                targetState = showHideControlLayout,
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .align(Alignment.BottomCenter),
                            ) { focused ->
                                if (focused) {
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .background(
                                                    smoothScrimBrush(
                                                        from = overlay,
                                                        to = Color.Black,
                                                        startFraction = 0.2f,
                                                    ),
                                                ),
                                    )
                                } else {
                                    // Box fullscreen — gradient stops control where darkening starts.
                                    // Note: pager content can extend past visible viewport bottom due
                                    // to parent offsets. We span the FULL pager height (instead of a
                                    // fixed 120.dp BottomCenter Box) so the colorStops are anchored
                                    // to pager height — guaranteeing the visible viewport bottom
                                    // always falls inside the held-Black region (>=0.85f).
                                    // Unfocused gradient — compact dark coverage at the very bottom only:
                                    //   - 0%-92%: fully Transparent (canvas clear)
                                    //   - 92%-97%: quick fade to Black
                                    //   - 97%-100%: held solid Black (avoids canvas bleed-through
                                    //     at visible viewport bottom — alpha must reach 0xFF before
                                    //     the visible bottom, which sits at ~94-95% of pager).
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .background(
                                                    smoothScrimBrush(
                                                        from = Color.Black.copy(alpha = 0f),
                                                        to = Color.Black,
                                                        startFraction = 0.92f,
                                                        endFraction = 0.97f,
                                                    ),
                                                ),
                                    )
                                }
                            }
                        }

                        // ── Layer 2: centered square thumbnail ──
                        // Positioned at the same Y as the original middle layout
                        // (TopAppBar height + middleLayoutPaddingDp from the top of the page).
                        // alpha=0 when the canvas is covering this page; otherwise visible so
                        // adjacent pages always show the upcoming/previous track artwork.
                        Column(modifier = Modifier.fillMaxSize()) {
                            Spacer(modifier = Modifier.height(topAppBarHeightDp.dp))
                            Spacer(
                                modifier =
                                    Modifier
                                        .animateContentSize()
                                        .height(middleLayoutPaddingDp.dp)
                                        .fillMaxWidth(),
                            )
                            // Artwork shape follows the user's setting: a circle inside a seek ring,
                            // where the arc around the art is itself the progress control, or the
                            // plain rounded square, where the bar under the title is the only way to
                            // seek. Both occupy the same square, so nothing below shifts.
                            //
                            // Only the current page gets the ring. Adjacent pager pages are just
                            // previews and a seek ring on them would be a control for a track that
                            // is not playing.
                            PlayerArtworkFrame(
                                useRing = artworkStyle == DataStoreManager.ARTWORK_STYLE_RING,
                                // Four conditions, all required: the user asked for it, the ring
                                // shape is selected (the square has no disc), the track is actually
                                // playing, and the system is not asking for less motion. The last
                                // one covers battery saver and the accessibility setting, which a
                                // continuous animation is exactly what they exist to suppress.
                                spinning =
                                    spinArtwork == DataStoreManager.TRUE &&
                                        artworkStyle == DataStoreManager.ARTWORK_STYLE_RING &&
                                        controllerState.isPlaying &&
                                        !motionReduced,
                                isVideo = screenDataState.isVideo,
                                progress = sliderValue / 100f,
                                enabled = isCurrentArtworkPage,
                                activeColor = sliderTrackColor,
                                onSeek = { isSliding = true; sliderValue = it * 100f },
                                onSeekFinished = {
                                    isSliding = false
                                    sharedViewModel.onUIEvent(UIEvent.UpdateProgress(it * 100f))
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .alpha(if (pageHasCanvas) 0f else 1f),
                            ) {
                                if (isCurrentArtworkPage) {
                                    // Live artwork (drives palette extraction via setBitmap).
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier =
                                            Modifier
                                                .align(Alignment.Center)
                                                .background(Color.Transparent)
                                                .shadow(
                                                    elevation = 3.dp,
                                                    shape = SonoraShapes.Container,
                                                    spotColor =
                                                        spotShadowColor.copy(
                                                            alpha = 0.6f,
                                                        ),
                                                    ambientColor = Color.Transparent,
                                                ),
                                    ) {
                                        AsyncImage(
                                            model =
                                                ImageRequest
                                                    .Builder(LocalPlatformContext.current)
                                                    .data(screenDataState.thumbnailURL)
                                                    .diskCachePolicy(CachePolicy.ENABLED)
                                                    .diskCacheKey(screenDataState.thumbnailURL + "BIGGER")
                                                    .crossfade(550)
                                                    .build(),
                                            contentDescription = "",
                                            onSuccess = {
                                                sharedViewModel.setBitmap(
                                                    it.result.image.toImageBitmap(),
                                                )
                                            },
                                            contentScale = ContentScale.Crop,
                                            placeholder = rememberHolderPainter(),
                                            error = rememberHolderPainter(),
                                            modifier =
                                                Modifier
                                                    .align(Alignment.Center)
                                                    .padding(3.dp)
                                                    .fillMaxWidth()
                                                    .background(Color.Transparent)
                                                    .aspectRatio(
                                                        if (!screenDataState.isVideo) 1f else 16f / 9,
                                                    ).clip(
                                                        SonoraShapes.Container,
                                                    ).alpha(
                                                        if (!screenDataState.isVideo || !shouldShowVideo) 1f else 0f,
                                                    ),
                                        )
                                    }

                                    // Inline video player (current page + isVideo + shouldShowVideo).
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = screenDataState.isVideo && shouldShowVideo,
                                        modifier = Modifier.align(Alignment.Center),
                                    ) {
                                        var internalShowSubtitle by rememberSaveable {
                                            mutableStateOf(true)
                                        }
                                        Box(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(16f / 9)
                                                    .clip(SonoraShapes.Container)
                                                    .background(Color.Black),
                                        ) {
                                            Box(Modifier.fillMaxSize()) {
                                                MediaPlayerViewWithSubtitle(
                                                    playerName = MAIN_PLAYER,
                                                    modifier = Modifier.align(Alignment.Center),
                                                    shouldShowSubtitle = internalShowSubtitle,
                                                    shouldPip = false,
                                                    shouldScaleDownSubtitle = true,
                                                    timelineState = timelineState,
                                                    lyricsData = screenDataState.lyricsData?.lyrics,
                                                    translatedLyricsData = screenDataState.lyricsData?.translatedLyrics?.first,
                                                    isInPipMode = isInPipMode,
                                                    mainTextStyle = typo().bodyLarge,
                                                    translatedTextStyle = typo().bodyMedium,
                                                )
                                            }
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .fillMaxSize()
                                                        .clickable(
                                                            onClick = { showHideFullscreenOverlay = !showHideFullscreenOverlay },
                                                            indication = null,
                                                            interactionSource =
                                                                remember {
                                                                    MutableInteractionSource()
                                                                },
                                                        ),
                                            ) {
                                                Crossfade(targetState = showHideFullscreenOverlay) {
                                                    if (it) {
                                                        Box(
                                                            modifier =
                                                                Modifier
                                                                    .fillMaxSize()
                                                                    .background(
                                                                        // The old middle stop (0.15f to overlay)
                                                                        // hand-approximated a convex falloff;
                                                                        // smoothstep does that on its own.
                                                                        smoothScrimBrush(
                                                                            from = blackMoreOverlay,
                                                                            to = overlay.copy(alpha = 0f),
                                                                            startFraction = 0.03f,
                                                                            endFraction = 0.8f,
                                                                        ),
                                                                    ),
                                                        ) {
                                                            IconButton(
                                                                onClick = {
                                                                    onDismiss()
                                                                    navController.navigate(FullscreenDestination)
                                                                },
                                                                Modifier.align(Alignment.TopEnd),
                                                            ) {
                                                                Icon(
                                                                    imageVector = SonoraIcons.Fullscreen,
                                                                    contentDescription = "",
                                                                    tint = Color.White,
                                                                )
                                                            }
                                                            Row(
                                                                Modifier
                                                                    .align(Alignment.Center)
                                                                    .fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceEvenly,
                                                            ) {
                                                                FilledTonalIconButton(
                                                                    colors =
                                                                        IconButtonDefaults.iconButtonColors().copy(
                                                                            containerColor = Color.Transparent,
                                                                        ),
                                                                    modifier =
                                                                        Modifier
                                                                            .size(48.dp)
                                                                            .aspectRatio(1f)
                                                                            .clip(CircleShape),
                                                                    onClick = {
                                                                        sharedViewModel.onUIEvent(UIEvent.Backward)
                                                                    },
                                                                ) {
                                                                    Icon(
                                                                        imageVector = SonoraIcons.Replay5,
                                                                        tint = Color.White,
                                                                        contentDescription = "",
                                                                        modifier =
                                                                            Modifier
                                                                                .size(36.dp)
                                                                                .alpha(0.8f),
                                                                    )
                                                                }
                                                                FilledTonalIconButton(
                                                                    colors =
                                                                        IconButtonDefaults.iconButtonColors().copy(
                                                                            containerColor = Color.Transparent,
                                                                        ),
                                                                    modifier =
                                                                        Modifier
                                                                            .size(48.dp)
                                                                            .aspectRatio(1f)
                                                                            .clip(CircleShape),
                                                                    onClick = {
                                                                        sharedViewModel.onUIEvent(UIEvent.Forward)
                                                                    },
                                                                ) {
                                                                    Icon(
                                                                        imageVector = SonoraIcons.Forward5,
                                                                        tint = Color.White,
                                                                        contentDescription = "",
                                                                        modifier =
                                                                            Modifier
                                                                                .size(36.dp)
                                                                                .alpha(0.8f),
                                                                    )
                                                                }
                                                            }
                                                            if (screenDataState.lyricsData != null) {
                                                                // Was a bare Subtitles icon, which is
                                                                // why nobody found the lyrics: the
                                                                // feature is fully built (synced,
                                                                // word-level, translated) and had no
                                                                // label anywhere on the screen.
                                                                // Same control, same 48.dp target,
                                                                // same toggle. Only now it says so.
                                                                Row(
                                                                    modifier =
                                                                        Modifier
                                                                            .align(Alignment.BottomEnd)
                                                                            .padding(12.dp)
                                                                            .clip(CircleShape)
                                                                            .background(Color.Black.copy(alpha = 0.45f))
                                                                            .clickable {
                                                                                internalShowSubtitle = !internalShowSubtitle
                                                                            }.padding(horizontal = 14.dp, vertical = 10.dp),
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                ) {
                                                                    Icon(
                                                                        imageVector =
                                                                            if (internalShowSubtitle) {
                                                                                SonoraIcons.SubtitlesOff
                                                                            } else {
                                                                                SonoraIcons.Subtitles
                                                                            },
                                                                        contentDescription = null,
                                                                        tint = Color.White,
                                                                        modifier = Modifier.size(18.dp),
                                                                    )
                                                                    Spacer(Modifier.width(6.dp))
                                                                    Text(
                                                                        text =
                                                                            stringResource(
                                                                                if (internalShowSubtitle) {
                                                                                    Res.string.hide
                                                                                } else {
                                                                                    Res.string.lyrics
                                                                                },
                                                                            ),
                                                                        style = typo().bodySmall,
                                                                        color = Color.White,
                                                                        maxLines = 1,
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else if (pageTrack != null) {
                                    // Adjacent page — static thumbnail from Track.thumbnails.
                                    val staticThumb =
                                        pageTrack.thumbnails
                                            ?.maxByOrNull { it.width * it.height }
                                            ?.url
                                    val palettePageScope = rememberCoroutineScope()
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier =
                                            Modifier
                                                .align(Alignment.Center)
                                                .background(Color.Transparent)
                                                .shadow(
                                                    elevation = 3.dp,
                                                    shape = SonoraShapes.Media,
                                                    spotColor = Color.Black.copy(alpha = 0.4f),
                                                    ambientColor = Color.Transparent,
                                                ),
                                    ) {
                                        AsyncImage(
                                            model =
                                                ImageRequest
                                                    .Builder(LocalPlatformContext.current)
                                                    .data(staticThumb)
                                                    .diskCachePolicy(CachePolicy.ENABLED)
                                                    .diskCacheKey(staticThumb)
                                                    .crossfade(300)
                                                    .build(),
                                            contentDescription = pageTrack.title,
                                            contentScale = ContentScale.Crop,
                                            placeholder = rememberHolderPainter(),
                                            error = rememberHolderPainter(),
                                            // Feed the per-page palette using the SAME bitmap
                                            // we just rendered so the Layer 0 gradient backdrop
                                            // matches what the user sees on screen.
                                            onSuccess = { state ->
                                                palettePageScope.launch {
                                                    pagePaletteState.generate(
                                                        state.result.image.toImageBitmap(),
                                                    )
                                                }
                                            },
                                            modifier =
                                                Modifier
                                                    .align(Alignment.Center)
                                                    .padding(3.dp)
                                                    .fillMaxWidth()
                                                    .aspectRatio(1f)
                                                    .clip(SonoraShapes.Container),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                CenterAlignedTopAppBar(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .onGloballyPositioned {
                                topAppBarHeightDp =
                                    with(localDensity) {
                                        it.size.height
                                            .toDp()
                                            .value
                                            .toInt()
                                    }
                            }.padding(
                                top = with(localDensity) { WindowInsets.statusBars.getTop(localDensity).toDp() },
                            ),
                    colors =
                        TopAppBarDefaults.topAppBarColors().copy(
                            containerColor = Color.Transparent,
                        ),
                    // Position-aware insets shrink per frame while the sheet is dragged (pinned
                    // bar + layout jitter) — status-bar space is static padding on the modifier.
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    title = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = stringResource(Res.string.now_playing_upper),
                                style = typo().bodyMedium,
                                color = Color.White,
                            )
                            Text(
                                text = screenDataState.playlistName,
                                style = typo().labelMedium,
                                color = Color.White,
                                textAlign = TextAlign.Center,
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
                        }
                    },
                    navigationIcon = {
                        // 40.dp rather than the 48.dp default: a glass disc is a visible object, and
                        // at 48 it sits hard against the screen edge. The IconButton it replaces drew
                        // nothing, so its size only ever read as a touch target.
                        //
                        // The 16.dp is on top of the 4.dp the top bar already puts in front of this
                        // slot, landing the disc on the same 20.dp gutter as the title, the waveform
                        // and the control capsule. Without it the disc sat 4.dp off the screen edge
                        // and was the only thing on the screen that did.
                        if (useGlass) {
                            LiquidGlassIconButton(
                                backdrop = playerBackdrop,
                                imageVector = dismissIcon,
                                modifier = Modifier.padding(start = 16.dp).size(40.dp),
                                onClick = { onDismiss() },
                            )
                        } else {
                            IconButton(onClick = {
                                onDismiss()
                            }) {
                                Icon(
                                    imageVector = dismissIcon,
                                    contentDescription = "",
                                    tint = Color.White,
                                )
                            }
                        }
                    },
                    actions = {
                        if (useGlass) {
                            LiquidGlassIconButton(
                                backdrop = playerBackdrop,
                                imageVector = SonoraIcons.MoreVert,
                                modifier = Modifier.padding(end = 16.dp).size(40.dp),
                                onClick = { showSheet = true },
                            )
                        } else {
                            IconButton(onClick = {
                                showSheet = true
                            }) {
                                Icon(
                                    imageVector = SonoraIcons.MoreVert,
                                    contentDescription = "",
                                    tint = Color.White,
                                )
                            }
                        }
                    },
                )
                Column {
                    Spacer(
                        modifier =
                            Modifier.height(
                                topAppBarHeightDp.dp,
                            ),
                    )
                    Box {
                        Column(
                            Modifier
                                .fillMaxWidth(),
                        ) {
                            Spacer(
                                modifier =
                                    Modifier
                                        .animateContentSize()
                                        .height(
                                            middleLayoutPaddingDp.dp,
                                        ).fillMaxWidth(),
                            )

                            // Artwork is rendered by the unified ArtworkPager above (which lives in the
                            // outer Box). Reserve the same vertical space here so the Info Layout below
                            // stays at its original Y position. Spacer has no pointer input so it does
                            // not block the pager swipe gesture beneath it.
                            Spacer(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .onGloballyPositioned { coords ->
                                            middleLayoutHeightDp =
                                                with(localDensity) {
                                                    coords.size.height
                                                        .toDp()
                                                        .value
                                                        .toInt()
                                                }
                                        }.aspectRatio(1f),
                            )

                            // Breathing room under the square only. The square fills the frame while
                            // the ring keeps its artwork 18dp inside one, so the square sits 36dp
                            // wider and leaves the lyric line, title and controls fighting for what
                            // is left. This pushes the stack down rather than shrinking the art,
                            // and the ring path adds nothing so its layout is untouched.
                            if (artworkStyle == DataStoreManager.ARTWORK_STYLE_SQUARE) {
                                Spacer(Modifier.height(SQUARE_ARTWORK_BOTTOM_GAP))
                            }

                            // Spotify-style current lyric line — vertically centered in the gap
                            // between the artwork and the info layout below. This Box replaces the
                            // plain gap Spacer at the same middleLayoutPaddingDp height, so the
                            // info layout position never moves; the line just lives inside the gap.
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier =
                                    Modifier
                                        .animateContentSize()
                                        .height(
                                            middleLayoutPaddingDp.dp,
                                        ).fillMaxWidth(),
                            ) {
                                val inlineLyrics = screenDataState.lyricsData?.lyrics
                                val hasSyncedLyrics =
                                    inlineLyrics != null &&
                                        inlineLyrics.syncType != null &&
                                        inlineLyrics.syncType != "UNSYNCED" &&
                                        inlineLyrics.lines != null
                                // Canvas mode has its own subtitle overlay — never show both.
                                val currentLyricLineText =
                                    if (!hasSyncedLyrics ||
                                        screenDataState.canvasData != null ||
                                        currentLyricLineIndex < 0
                                    ) {
                                        ""
                                    } else {
                                        inlineLyrics
                                            ?.lines
                                            ?.getOrNull(currentLyricLineIndex)
                                            ?.words
                                            ?.stripRichSyncTimestamps()
                                            .orEmpty()
                                    }
                                Crossfade(
                                    targetState = currentLyricLineText,
                                    animationSpec = tween(durationMillis = 300),
                                    label = "inlineLyricLine",
                                ) { lineText ->
                                    Text(
                                        text = lineText,
                                        style = typo().labelSmall,
                                        // Opacity, not italics. No bundled font ships a real italic
                                        // or a slant axis, so FontStyle.Italic would be a synthetic
                                        // shear, and sheared Gurmukhi looks broken. Letter spacing
                                        // was the other option and hurts Gurmukhi legibility too.
                                        // Dimming reads correctly on every script.
                                        color = Color.White.copy(alpha = 0.7f),
                                        maxLines = 1,
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                                .titleMarquee(
                                                    iterations = Int.MAX_VALUE,
                                                    animationMode = MarqueeAnimationMode.Immediately,
                                                ).focusable(),
                                    )
                                }
                            }

                            // Info Layout
                            Box {
                                Column(
                                    Modifier
                                        .alpha(controlLayoutAlpha)
                                        .onGloballyPositioned {
                                            infoLayoutHeightDp =
                                                with(localDensity) {
                                                    it.size.height
                                                        .toDp()
                                                        .value
                                                        .toInt()
                                                }
                                        },
                                ) {
                                    Row(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        AnimatedVisibility(screenDataState.canvasData != null) {
                                            AsyncImage(
                                                model =
                                                    ImageRequest
                                                        .Builder(LocalPlatformContext.current)
                                                        .data(screenDataState.thumbnailURL)
                                                        .diskCachePolicy(CachePolicy.ENABLED)
                                                        .diskCacheKey(screenDataState.thumbnailURL + "BIGGER")
                                                        .crossfade(true)
                                                        .build(),
                                                placeholder = rememberHolderPainter(),
                                                error = rememberHolderPainter(),
                                                contentDescription = null,
                                                contentScale = ContentScale.FillWidth,
                                                modifier =
                                                    Modifier
                                                        .heightIn(0.dp, 55.dp)
                                                        .width(55.dp)
                                                        .padding(end = 10.dp)
                                                        .clip(
                                                            SonoraShapes.Container,
                                                        ).align(Alignment.CenterVertically),
                                            )
                                        }

                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                text = screenDataState.nowPlayingTitle,
                                                style = typo().titleMedium,
                                                maxLines = 1,
                                                color = Color.White,
                                                modifier =
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .wrapContentHeight(align = Alignment.CenterVertically)
                                                        .titleMarquee(
                                                            iterations = Int.MAX_VALUE,
                                                            animationMode = MarqueeAnimationMode.Immediately,
                                                        ).focusable(),
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            LazyRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                item(screenDataState.isExplicit) {
                                                    AnimatedVisibility(visible = screenDataState.isExplicit) {
                                                        ExplicitBadge(
                                                            modifier =
                                                                Modifier
                                                                    .size(20.dp)
                                                                    .padding(end = 4.dp)
                                                                    .weight(1f),
                                                        )
                                                    }
                                                }
                                                item(screenDataState.artistName) {
                                                    Text(
                                                        text = screenDataState.artistName,
                                                        style = typo().bodyMedium,
                                                        maxLines = 1,
                                                        modifier =
                                                            Modifier
                                                                .fillMaxWidth()
                                                                .wrapContentHeight(align = Alignment.CenterVertically)
                                                                .titleMarquee(
                                                                    iterations = Int.MAX_VALUE,
                                                                    animationMode = MarqueeAnimationMode.Immediately,
                                                                ).focusable()
                                                                .clickable {
                                                                    val song = sharedViewModel.nowPlayingState.value?.songEntity
                                                                    (
                                                                        song?.artistId?.firstOrNull()?.takeIf { it.isNotEmpty() }
                                                                            ?: screenDataState.songInfoData?.authorId
                                                                    )?.let { channelId ->
                                                                        onDismiss()
                                                                        navController.navigate(
                                                                            ArtistDestination(
                                                                                channelId = channelId,
                                                                            ),
                                                                        )
                                                                    }
                                                                },
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.size(12.dp))
                                        HeartCheckBox(checked = controllerState.isLiked, size = 32) {
                                            sharedViewModel.onUIEvent(UIEvent.ToggleLike)
                                        }
                                        Spacer(modifier = Modifier.size(8.dp))
                                        OutputDeviceButton(size = 24, box = 32)
                                    }
                                    if (getPlatform() == Platform.Android) {
                                        Spacer(modifier = Modifier.height(if (useGlass) 20.dp else 14.dp))
                                        // Waves between the two timestamps, replacing the linear bar. The Slider is kept
                                        // purely for its drag, keyboard and accessibility handling; only what it draws changes.
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = formatDuration((timelineState.total * (sliderValue / 100f)).roundToLong()),
                                                style = typo().bodySmall,
                                            )
                                            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                                                Slider(
                                                    value = sliderValue / 100f,
                                                    onValueChangeFinished = {
                                                        isSliding = false
                                                        sharedViewModel.onUIEvent(UIEvent.UpdateProgress(sliderValue))
                                                    },
                                                    onValueChange = {
                                                        isSliding = true
                                                        sliderValue = it * 100f
                                                    },
                                                    modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
                                                    track = { sliderState ->
                                                        if (seekBarStyle == DataStoreManager.SEEK_BAR_LINE) {
                                                            SliderDefaults.Track(
                                                                modifier = Modifier.height(5.dp),
                                                                enabled = true,
                                                                sliderState = sliderState,
                                                                colors =
                                                                    SliderDefaults.colors().copy(
                                                                        thumbColor = sliderTrackColor,
                                                                        activeTrackColor = sliderTrackColor,
                                                                        inactiveTrackColor = sliderTrackColor.copy(alpha = 0.26f),
                                                                    ),
                                                                thumbTrackGapSize = 0.dp,
                                                                drawTick = { _, _ -> },
                                                                drawStopIndicator = null,
                                                            )
                                                        } else {
                                                            WaveformTrack(
                                                                progress = sliderValue / 100f,
                                                                seed = timelineState.total,
                                                                modifier = Modifier.fillMaxWidth().height(28.dp),
                                                                playedColor = sliderTrackColor,
                                                                unplayedColor = sliderTrackColor.copy(alpha = 0.26f),
                                                            )
                                                        }
                                                    },
                                                    // Waves carry their own played/unplayed split so a
                                                    // thumb would be a second marker; the line style
                                                    // needs one to be grabbable.
                                                    thumb = {
                                                        if (seekBarStyle == DataStoreManager.SEEK_BAR_LINE) {
                                                            SliderDefaults.Thumb(
                                                                modifier = Modifier.height(18.dp).width(8.dp).padding(vertical = 4.dp),
                                                                thumbSize = DpSize(8.dp, 8.dp),
                                                                interactionSource = remember { MutableInteractionSource() },
                                                                colors =
                                                                    SliderDefaults.colors().copy(
                                                                        thumbColor = sliderTrackColor,
                                                                        activeTrackColor = sliderTrackColor,
                                                                        inactiveTrackColor = Color.Transparent,
                                                                    ),
                                                                enabled = true,
                                                            )
                                                        }
                                                    },
                                                )
                                            }
                                            Text(
                                                text = formatDuration(timelineState.total),
                                                style = typo().bodySmall,
                                            )
                                        }

                                        // 18 rather than 5 on the glass layout: the waveform and the
                                        // transport row were effectively touching, which the capsule
                                        // made obvious by giving the transport a visible edge.
                                        Spacer(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(if (useGlass) 18.dp else 5.dp),
                                        )
                                        // Control Button Layout
                                        //
                                        // interactive = false on purpose. The press response scales
                                        // the surface by 1.12 and blooms a glow under the finger,
                                        // which suits a single button; on a capsule this wide it
                                        // lurches, and it would fire on all five transport taps.
                                        // The buttons keep their own feedback either way, since the
                                        // glass gesture never consumes events.
                                        if (useGlass) {
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .padding(horizontal = 16.dp)
                                                        .fillMaxWidth()
                                                        .liquidGlass(
                                                            backdrop = playerBackdrop,
                                                            shape = SonoraShapes.Container,
                                                            interactive = false,
                                                        ),
                                            ) {
                                                PlayerControlLayout(
                                                    controllerState,
                                                ) {
                                                    sharedViewModel.onUIEvent(it)
                                                }
                                            }
                                        } else {
                                            PlayerControlLayout(
                                                controllerState,
                                            ) {
                                                sharedViewModel.onUIEvent(it)
                                            }
                                        }
                                    } else {
                                        Spacer(Modifier.height(16.dp))
                                    }
                                    // The row of secondary buttons that used to sit here is gone
                                    // entirely. It had been reduced to the Cast button and its
                                    // "playing on <device>" label, and that button is removed at the
                                    // owner's request; an empty row only added a dead band above the
                                    // up-next strip. Add to playlist lives in the overflow sheet
                                    // behind the top-bar kebab, and UpNextPeekBar below is the queue
                                    // entry point.
                                    UpNextPeekBar(
                                        modifier =
                                            Modifier
                                                .padding(horizontal = 16.dp)
                                                // Bottom padding clears the gesture bar. Without it
                                                // the artist line is cut by the screen edge.
                                                .padding(top = 12.dp, bottom = 24.dp),
                                        backdrop = playerBackdrop.takeIf { useGlass },
                                        onOpenQueue = { showQueueBottomSheet = true },
                                    )
                                }
                                this@Column.AnimatedVisibility(
                                    visible = !showHideControlLayout,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                ) {
                                    Box(
                                        modifier =
                                            Modifier
                                                .height(
                                                    infoLayoutHeightDp.dp,
                                                ).fillMaxWidth()
                                                .clickable(
                                                    onClick = {
                                                        if (mainScrollState.value == 0) {
                                                            showHideJob = true
                                                            showHideControlLayout = !showHideControlLayout
                                                        }
                                                    },
                                                    indication = null,
                                                    interactionSource =
                                                        remember {
                                                            MutableInteractionSource()
                                                        },
                                                ),
                                        contentAlignment = Alignment.BottomStart,
                                    ) {
                                        // Gradient backdrop — transparent at top so Canvas shows
                                        // through, fading to dark at the bottom for a Spotify-like
                                        // backdrop under the metadata row.
                                        Box(
                                            modifier =
                                                Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        smoothScrimBrush(
                                                            from = Color.Black.copy(alpha = 0f),
                                                            to = Color.Black.copy(alpha = 0.85f),
                                                        ),
                                                    ),
                                        )

                                        Column(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .animateContentSize(),
                                        ) {
                                            this@Column.AnimatedVisibility(
                                                visible = currentLyricLineIndex > -1,
                                                enter = fadeIn() + expandVertically(),
                                                exit = fadeOut() + shrinkVertically(),
                                            ) {
                                                // Canvas subtitle - Spotify-style: lyrics line above metadata row
                                                val lineText =
                                                    screenDataState.lyricsData
                                                        ?.lyrics
                                                        ?.lines
                                                        ?.getOrNull(currentLyricLineIndex)
                                                        ?.words
                                                        ?.stripRichSyncTimestamps()
                                                if (!lineText.isNullOrBlank()) {
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth(),
                                                    ) {
                                                        Text(
                                                            modifier =
                                                                Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(horizontal = 16.dp)
                                                                    .padding(bottom = 4.dp)
                                                                    .titleMarquee(
                                                                        iterations = Int.MAX_VALUE,
                                                                        animationMode = MarqueeAnimationMode.Immediately,
                                                                    ).focusable(),
                                                            text = lineText,
                                                            style = typo().bodyMedium,
                                                            color = Color.White,
                                                            maxLines = 1,
                                                        )
                                                        val translatedLineText =
                                                            screenDataState.lyricsData
                                                                ?.translatedLyrics
                                                                ?.first
                                                                ?.lines
                                                                ?.getOrNull(currentLyricLineIndex)
                                                                ?.words
                                                                ?.stripRichSyncTimestamps()
                                                        if (!translatedLineText.isNullOrBlank()) {
                                                            Text(
                                                                modifier =
                                                                    Modifier
                                                                        .fillMaxWidth()
                                                                        .padding(horizontal = 16.dp)
                                                                        .padding(bottom = 8.dp)
                                                                        .titleMarquee(
                                                                            iterations = Int.MAX_VALUE,
                                                                            animationMode = MarqueeAnimationMode.Immediately,
                                                                        ).focusable(),
                                                                text = translatedLineText,
                                                                style = typo().bodyMedium,
                                                                color = Color.Yellow,
                                                                maxLines = 1,
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Row(
                                                modifier =
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 16.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                AnimatedVisibility(screenDataState.canvasData != null) {
                                                    AsyncImage(
                                                        model =
                                                            ImageRequest
                                                                .Builder(LocalPlatformContext.current)
                                                                .data(screenDataState.thumbnailURL)
                                                                .diskCachePolicy(CachePolicy.ENABLED)
                                                                .diskCacheKey(screenDataState.thumbnailURL + "BIGGER")
                                                                .crossfade(true)
                                                                .build(),
                                                        placeholder = rememberHolderPainter(),
                                                        error = rememberHolderPainter(),
                                                        contentDescription = null,
                                                        contentScale = ContentScale.FillWidth,
                                                        modifier =
                                                            Modifier
                                                                .heightIn(0.dp, 55.dp)
                                                                .width(55.dp)
                                                                .padding(end = 10.dp)
                                                                .clip(
                                                                    SonoraShapes.Container,
                                                                ).align(Alignment.CenterVertically),
                                                    )
                                                }

                                                Column(Modifier.weight(1f)) {
                                                    Text(
                                                        text = screenDataState.nowPlayingTitle,
                                                        style = typo().titleMedium,
                                                        maxLines = 1,
                                                        color = Color.White,
                                                        modifier =
                                                            Modifier
                                                                .fillMaxWidth()
                                                                .wrapContentHeight(align = Alignment.CenterVertically)
                                                                .titleMarquee(
                                                                    iterations = Int.MAX_VALUE,
                                                                    animationMode = MarqueeAnimationMode.Immediately,
                                                                ).focusable(),
                                                    )
                                                    Spacer(modifier = Modifier.height(3.dp))
                                                    LazyRow(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                    ) {
                                                        item(screenDataState.isExplicit) {
                                                            AnimatedVisibility(visible = screenDataState.isExplicit) {
                                                                ExplicitBadge(
                                                                    modifier =
                                                                        Modifier
                                                                            .size(20.dp)
                                                                            .padding(end = 4.dp)
                                                                            .weight(1f),
                                                                )
                                                            }
                                                        }
                                                        item(screenDataState.artistName) {
                                                            Text(
                                                                text = screenDataState.artistName,
                                                                style = typo().bodyMedium,
                                                                maxLines = 1,
                                                                modifier =
                                                                    Modifier
                                                                        .fillMaxWidth()
                                                                        .wrapContentHeight(align = Alignment.CenterVertically)
                                                                        .titleMarquee(
                                                                            iterations = Int.MAX_VALUE,
                                                                            animationMode = MarqueeAnimationMode.Immediately,
                                                                        ).focusable()
                                                                        .clickable {
                                                                            val song = sharedViewModel.nowPlayingState.value?.songEntity
                                                                            (
                                                                                song?.artistId?.firstOrNull()?.takeIf { it.isNotEmpty() }
                                                                                    ?: screenDataState.songInfoData?.authorId
                                                                            )?.let { channelId ->
                                                                                onDismiss()
                                                                                navController.navigate(
                                                                                    ArtistDestination(
                                                                                        channelId = channelId,
                                                                                    ),
                                                                                )
                                                                            }
                                                                        },
                                                            )
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.size(12.dp))
                                                HeartCheckBox(checked = controllerState.isLiked, size = 32) {
                                                    sharedViewModel.onUIEvent(UIEvent.ToggleLike)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        // The original Touch Area overlay was removed: tap-to-toggle is now
                        // wired directly onto each ArtworkPager page (canvas + middle), so
                        // drag gestures reach HorizontalPager without competing with a
                        // sibling clickable.
                    }
                    Column(Modifier.padding(horizontal = 16.dp)) {
                        // Lyrics Layout
                        AnimatedVisibility(
                            visible = screenDataState.lyricsData != null,
                            modifier = Modifier.padding(top = 10.dp),
                        ) {
                            ElevatedCard(
                                onClick = {},
                                shape = SonoraShapes.Container,
                                colors =
                                    CardDefaults.elevatedCardColors().copy(
                                        containerColor = startColor.value,
                                    ),
                            ) {
                                Column(modifier = Modifier.padding(15.dp)) {
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = stringResource(Res.string.lyrics),
                                            style = typo().labelMedium,
                                            color = Color.White,
                                        )
                                        if (screenDataState.lyricsData?.translatedLyrics?.second == LyricsProvider.AI) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            AIBadge()
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        // Vote button - only show if lyrics or translated lyrics from Sonora
                                        val canVoteLyrics =
                                            screenDataState.lyricsData?.lyricsProvider == LyricsProvider.SONORA &&
                                                screenDataState.lyricsData
                                                    ?.lyrics
                                                    ?.sonoraLyrics != null
                                        val canVoteTranslatedLyrics =
                                            screenDataState.lyricsData?.translatedLyrics?.second == LyricsProvider.SONORA &&
                                                screenDataState.lyricsData
                                                    ?.translatedLyrics
                                                    ?.first
                                                    ?.sonoraLyrics != null
                                        if (canVoteLyrics || canVoteTranslatedLyrics) {
                                            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                                                IconButton(
                                                    onClick = {
                                                        showVoteDialog = true
                                                    },
                                                ) {
                                                    Icon(
                                                        imageVector = SonoraIcons.ThumbsUpDown,
                                                        contentDescription = stringResource(Res.string.rate_lyrics),
                                                        tint = Color.White,
                                                        modifier = Modifier.size(16.dp),
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                                            TextButton(
                                                onClick = {
                                                    showFullscreenLyrics = true
                                                },
                                                contentPadding = PaddingValues(0.dp),
                                                modifier =
                                                    Modifier
                                                        .height(20.dp)
                                                        .wrapContentWidth(),
                                            ) {
                                                Text(text = stringResource(Res.string.show), color = Color.White)
                                            }
                                        }
                                    }
                                    // Lyrics Layout
                                    Spacer(modifier = Modifier.height(18.dp))
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .height(300.dp),
                                    ) {
                                        screenDataState.lyricsData?.let {
                                            LyricsView(
                                                lyricsData = it,
                                                timeLine = sharedViewModel.timeline,
                                                onLineClick = { f ->
                                                    sharedViewModel.onUIEvent(UIEvent.UpdateProgress(f))
                                                },
                                            )
                                        }
                                    }

                                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                                        Text(
                                            text =
                                                when (screenDataState.lyricsData?.lyrics?.syncType) {
                                                    "LINE_SYNCED" -> stringResource(Res.string.line_synced)
                                                    "RICH_SYNCED" -> stringResource(Res.string.rich_synced)
                                                    else -> stringResource(Res.string.unsynced)
                                                },
                                            style = typo().bodySmall,
                                            textAlign = TextAlign.End,
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 10.dp),
                                        )
                                        Text(
                                            text =
                                                when (screenDataState.lyricsData?.lyricsProvider) {
                                                    LyricsProvider.SONORA -> {
                                                        stringResource(Res.string.lyrics_provider_sonora)
                                                    }

                                                    LyricsProvider.LRCLIB -> {
                                                        stringResource(Res.string.lyrics_provider_lrc)
                                                    }

                                                    LyricsProvider.YOUTUBE -> {
                                                        stringResource(Res.string.lyrics_provider_youtube)
                                                    }

                                                    LyricsProvider.SPOTIFY -> {
                                                        stringResource(Res.string.spotify_lyrics_provider)
                                                    }

                                                    LyricsProvider.OFFLINE -> {
                                                        stringResource(Res.string.offline_mode)
                                                    }

                                                    LyricsProvider.BETTER_LYRICS -> {
                                                        stringResource(Res.string.lyrics_provider_betterlyrics)
                                                    }

                                                    else -> {
                                                        ""
                                                    }
                                                },
                                            style = typo().bodySmall,
                                            textAlign = TextAlign.End,
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth(),
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        // YouTube returns song info for videos with the stats and description filled
                        // in but no channel author or avatar. That used to leave this card rendering a
                        // 250.dp placeholder over two blank lines, which read as the artists section
                        // being broken. Everything below falls back to what the track already carries,
                        // and the card only appears when there is a name to show.
                        val authorName =
                            screenDataState.songInfoData
                                ?.author
                                ?.takeIf { it.isNotBlank() }
                                ?: screenDataState.artistName.takeIf { it.isNotBlank() }
                        val authorThumb =
                            screenDataState.songInfoData
                                ?.authorThumbnail
                                ?.takeIf { it.isNotBlank() }
                        val authorSubscribers =
                            screenDataState.songInfoData
                                ?.subscribers
                                ?.takeIf { it.isNotBlank() }
                        AnimatedVisibility(visible = authorName != null) {
                            ElevatedCard(
                                onClick = {
                                    val song = sharedViewModel.nowPlayingState.value?.songEntity
                                    (
                                        song?.artistId?.firstOrNull()?.takeIf { it.isNotEmpty() }
                                            ?: screenDataState.songInfoData?.authorId
                                    )?.let { channelId ->
                                        onDismiss()
                                        navController.navigate(
                                            ArtistDestination(
                                                channelId = channelId,
                                            ),
                                        )
                                    }
                                },
                                shape = SonoraShapes.Media,
                                colors =
                                    CardDefaults.elevatedCardColors().copy(
                                        containerColor = Color(0xFF212121),
                                    ),
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Artwork occupies the top of the card; only the section
                                    // label sits on top of it. Name and subscriber count moved
                                    // below onto the solid card surface so they stay readable
                                    // regardless of how bright the artist photo is.
                                    // Only reserve the tall image block when there is a real photo
                                    // for it. With none, the card collapses to its label and name
                                    // rather than showing 250.dp of placeholder.
                                    if (authorThumb != null) {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(250.dp),
                                        ) {
                                        val thumb = authorThumb
                                        AsyncImage(
                                            model =
                                                ImageRequest
                                                    .Builder(LocalPlatformContext.current)
                                                    .data(thumb)
                                                    .diskCachePolicy(CachePolicy.ENABLED)
                                                    .diskCacheKey(thumb)
                                                    .crossfade(550)
                                                    .build(),
                                            placeholder = rememberHolderPainter(isVideo = true),
                                            error = rememberHolderPainter(isVideo = true),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            // No explicit clip: the ElevatedCard already clips to
                                            // its 8.dp shape, so only the card's top corners round
                                            // and the image meets the panel below flush.
                                            modifier = Modifier.fillMaxSize(),
                                        )
                                        // Scrim behind the label: artist photos are often bright
                                        // at the top, which swallowed the white text.
                                        Box(
                                            modifier =
                                                Modifier
                                                    .matchParentSize()
                                                    .background(
                                                        smoothScrimBrush(
                                                            from = Color.Black.copy(alpha = 0.6f),
                                                            to = Color.Black.copy(alpha = 0f),
                                                            endFraction = 0.4f,
                                                        ),
                                                    ),
                                        )
                                        Text(
                                            text = stringResource(Res.string.artists),
                                            style = typo().labelMedium,
                                            color = Color.White,
                                            modifier =
                                                Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(15.dp),
                                        )
                                        }
                                    }
                                    Column(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                    ) {
                                        // With no photo the label has nowhere else to live, so it
                                        // moves down here and the card still says what it is.
                                        if (authorThumb == null) {
                                            Text(
                                                text = stringResource(Res.string.artists),
                                                style = typo().labelMedium,
                                                color = Color.White,
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                        Text(
                                            text = authorName.orEmpty(),
                                            style = typo().titleMedium,
                                            color = Color.White,
                                        )
                                        // Omitted rather than rendered blank: an empty line under the
                                        // name is what made the card look half loaded.
                                        if (authorSubscribers != null) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = authorSubscribers,
                                                style = typo().bodySmall,
                                                color = Color.White.copy(alpha = 0.7f),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        AnimatedVisibility(visible = screenDataState.songInfoData != null) {
                            ElevatedCard(
                                onClick = {},
                                shape = SonoraShapes.Container,
                                colors =
                                    CardDefaults.elevatedCardColors().copy(
                                        containerColor = startColor.value,
                                    ),
                            ) {
                                Column(
                                    Modifier
                                        .padding(15.dp)
                                        .fillMaxWidth(),
                                ) {
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = stringResource(Res.string.published_at, screenDataState.songInfoData?.uploadDate ?: ""),
                                        style = typo().labelSmall,
                                        color = Color.White,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text =
                                            stringResource(
                                                Res.string.view_count,
                                                "%,d".format(screenDataState.songInfoData?.viewCount),
                                            ),
                                        style = typo().labelMedium,
                                        color = Color.White,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text =
                                            stringResource(
                                                Res.string.like_and_dislike,
                                                screenDataState.songInfoData?.like ?: 0,
                                                screenDataState.songInfoData?.dislike ?: 0,
                                            ),
                                        style = typo().bodyMedium,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = stringResource(Res.string.description),
                                        style = typo().labelSmall,
                                        color = Color.White,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    DescriptionView(
                                        text = screenDataState.songInfoData?.description ?: "",
                                        onTimeClicked = { raw ->
                                            val timestamp = parseTimestampToMilliseconds(raw)
                                            if (timestamp != 0.0 && timestamp < timelineState.total) {
                                                sharedViewModel.onUIEvent(
                                                    UIEvent.UpdateProgress(
                                                        ((timestamp * 100) / timelineState.total).toFloat(),
                                                    ),
                                                )
                                            }
                                        },
                                        onURLClicked = { url ->
                                            uriHandler.openUri(
                                                url,
                                            )
                                        },
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Spacer(
                            modifier =
                                Modifier.height(
                                    with(localDensity) { WindowInsets.systemBars.getBottom(localDensity).toDp() },
                                ),
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = shouldShowToolbar && isExpanded,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
        ) {
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(10.dp),
                shape = RectangleShape,
                colors =
                    CardDefaults.elevatedCardColors(
                        containerColor =
                            startColor.value
                                .copy(
                                    red = startColor.value.red - 0.05f,
                                    green = startColor.value.green - 0.05f,
                                    blue = startColor.value.blue - 0.05f,
                                ),
                    ),
                modifier =
                    Modifier
                        .clipToBounds()
                        .wrapContentHeight()
                        .fillMaxWidth(),
            ) {
                Box(
                    modifier =
                        Modifier.padding(
                            top = with(localDensity) { WindowInsets.statusBars.getTop(localDensity).toDp() },
                        ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            Modifier
                                .padding(
                                    vertical = 8.dp,
                                    horizontal = 16.dp,
                                ).fillMaxWidth(),
                    ) {
                        Spacer(modifier = Modifier.size(8.dp))
                        Box(modifier = Modifier.weight(1F)) {
                            Column(
                                Modifier
                                    .wrapContentHeight(),
                            ) {
                                Text(
                                    text = screenDataState.nowPlayingTitle,
                                    style = typo().bodyMedium,
                                    color = Color.White,
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
                                LazyRow(verticalAlignment = Alignment.CenterVertically) {
                                    item {
                                        AnimatedVisibility(visible = screenDataState.isExplicit) {
                                            ExplicitBadge(
                                                modifier =
                                                    Modifier
                                                        .size(20.dp)
                                                        .padding(end = 4.dp)
                                                        .weight(1f),
                                            )
                                        }
                                    }
                                    item(
                                        key = screenDataState.artistName,
                                    ) {
                                        Text(
                                            text = screenDataState.artistName,
                                            style = typo().bodySmall,
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
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        HeartCheckBox(checked = controllerState.isLiked, size = 30) {
                            sharedViewModel.onUIEvent(UIEvent.ToggleLike)
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        Crossfade(targetState = timelineState.loading, label = "") {
                            if (it) {
                                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                                    SonoraSpinner(
                                        size = 18.dp,
                                        strokeWidth = 3.dp,
                                        color = Color.LightGray,
                                    )
                                }
                            } else {
                                PlayPauseButton(isPlaying = controllerState.isPlaying, modifier = Modifier.size(48.dp)) {
                                    sharedViewModel.onUIEvent(UIEvent.PlayPause)
                                }
                            }
                        }
                    }
                    Box(
                        modifier =
                            Modifier
                                .wrapContentSize(Alignment.Center)
                                .align(Alignment.BottomCenter),
                    ) {
                        LinearProgressIndicator(
                            progress = { timelineState.current.toFloat() / timelineState.total },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(
                                        color = Color.Transparent,
                                        shape = SonoraShapes.Container,
                                    ),
                            color = Color.White,
                            trackColor = Color.Gray.copy(alpha = 0.4f),
                            strokeCap = StrokeCap.Round,
                            drawStopIndicator = {},
                        )
                    }
                }
            }
        }
    }
}