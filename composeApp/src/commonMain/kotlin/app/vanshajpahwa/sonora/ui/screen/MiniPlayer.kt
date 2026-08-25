package app.vanshajpahwa.sonora.ui.screen

import androidx.compose.animation.Animatable
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import app.vanshajpahwa.sonora.ui.component.titleMarquee
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kmpalette.rememberPaletteState
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.domain.utils.connectArtists
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.Platform
import app.vanshajpahwa.sonora.expect.toggleMiniPlayer
import app.vanshajpahwa.sonora.expect.ui.PlatformBackdrop
import app.vanshajpahwa.sonora.expect.ui.toImageBitmap
import app.vanshajpahwa.sonora.extension.formatDuration
import app.vanshajpahwa.sonora.extension.getColorFromPalette
import app.vanshajpahwa.sonora.extension.hsvToColor
import app.vanshajpahwa.sonora.extension.toResizedBitmap
import app.vanshajpahwa.sonora.getPlatform
import app.vanshajpahwa.sonora.ui.component.SonoraSpinner
import app.vanshajpahwa.sonora.ui.component.ExplicitBadge
import app.vanshajpahwa.sonora.ui.component.HeartCheckBox
import app.vanshajpahwa.sonora.ui.component.PlayPauseButton
import app.vanshajpahwa.sonora.ui.component.PlayerControlLayout
import app.vanshajpahwa.sonora.ui.component.QueueBottomSheet
import app.vanshajpahwa.sonora.ui.component.liquidGlass
import app.vanshajpahwa.sonora.ui.component.rememberHolderPainter
import app.vanshajpahwa.sonora.ui.icon.Close
import app.vanshajpahwa.sonora.ui.icon.OpenInNew
import app.vanshajpahwa.sonora.ui.icon.QueueMusic
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.icon.SkipNext
import app.vanshajpahwa.sonora.ui.icon.VolumeOff
import app.vanshajpahwa.sonora.ui.icon.VolumeUp
import app.vanshajpahwa.sonora.ui.theme.LocalIsDarkTheme
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import app.vanshajpahwa.sonora.viewModel.UIEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.skip_next
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.seconds

private const val TAG = "MiniPlayer"

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MiniPlayer(
    modifier: Modifier,
    backdrop: PlatformBackdrop,
    sharedViewModel: SharedViewModel = koinInject(),
    onClose: () -> Unit,
    onClick: () -> Unit,
) {
    val isLiquidGlassEnabled by sharedViewModel.getEnableLiquidGlass().collectAsStateWithLifecycle(DataStoreManager.FALSE)
    val isTranslucentMiniPlayer by sharedViewModel
        .getTranslucentMiniPlayer()
        .collectAsStateWithLifecycle(DataStoreManager.FALSE)
    val controllerState by sharedViewModel.controllerState.collectAsStateWithLifecycle()
    val timelineState by sharedViewModel.timeline.collectAsStateWithLifecycle()

    val layer = rememberGraphicsLayer()
    val luminanceAnimation = remember { Animatable(0f) }

    val isDarkTheme = LocalIsDarkTheme.current
    val textColor by animateColorAsState(
        // With liquid glass the surface follows the theme (light = frosted white → black text);
        // without it, the surface is the artwork colour, so follow the backdrop luminance.
        targetValue =
            if (isLiquidGlassEnabled == DataStoreManager.TRUE) {
                if (isDarkTheme) Color.White else Color.Black
            } else if (luminanceAnimation.value > 0.6f) {
                Color.Black
            } else {
                Color.White
            },
        label = "MiniPlayerTextColor",
        animationSpec = tween(500),
    )

    LaunchedEffect(luminanceAnimation.value) {
        Logger.w("GlassDbg", "luminanceAnimation: ${luminanceAnimation.value}")
    }

    LaunchedEffect(layer, isLiquidGlassEnabled) {
        val buffer = IntArray(25)
        while (isActive && isLiquidGlassEnabled == DataStoreManager.TRUE) {
            try {
                withContext(Dispatchers.Main) {
                    val imageBitmap = layer.toImageBitmap()
                    val thumbnail = imageBitmap.toResizedBitmap(5, 5)
                    thumbnail.readPixels(buffer)
                }
            } catch (e: Exception) {
                Logger.e(TAG, "Error getting pixels from layer: ${e.message}")
            }
            val averageLuminance =
                (0 until 25).sumOf { index ->
                    val color = buffer.get(index)
                    val r = (color shr 16 and 0xFF) / 255f
                    val g = (color shr 8 and 0xFF) / 255f
                    val b = (color and 0xFF) / 255f
                    0.2126 * r + 0.7152 * g + 0.0722 * b
                } / 25
            luminanceAnimation.animateTo(
                averageLuminance.coerceIn(0.3, 0.8).toFloat(),
                tween(500),
            )
            delay(1.seconds)
        }
    }

    val (songEntity, setSongEntity) =
        remember {
            mutableStateOf<SongEntity?>(null)
        }
    val (liked, setLiked) =
        remember {
            mutableStateOf(false)
        }
    val (isPlaying, setIsPlaying) =
        remember {
            mutableStateOf(false)
        }
    val (progress, setProgress) =
        remember {
            mutableFloatStateOf(0f)
        }
    val (isCrossfading, setIsCrossfading) =
        remember {
            mutableStateOf(false)
        }

    val coroutineScope = rememberCoroutineScope()

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "",
    )

    // Palette state
    val paletteState = rememberPaletteState()
    val background =
        remember {
            Animatable(Color.DarkGray)
        }

    val offsetX = remember { Animatable(initialValue = 0f) }
    val offsetY = remember { Animatable(0f) }

    var loading by rememberSaveable {
        mutableStateOf(true)
    }

    var bitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    LaunchedEffect(bitmap) {
        val bm = bitmap
        if (bm != null) {
            paletteState.generate(bm)
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { paletteState.palette }
            .distinctUntilChanged()
            .collectLatest {
                background.animateTo(it.getColorFromPalette())
            }
    }

    LaunchedEffect(key1 = true) {
        val job1 =
            launch {
                sharedViewModel.nowPlayingState.collect { item ->
                    if (item != null) {
                        setSongEntity(item.songEntity)
                    }
                }
            }
        val job2 =
            launch {
                sharedViewModel.controllerState.collectLatest { state ->
                    setLiked(state.isLiked)
                    setIsPlaying(state.isPlaying)
                    setIsCrossfading(state.isCrossfading)
                }
            }
        val job4 =
            launch {
                sharedViewModel.timeline.collect { timeline ->
                    loading = timeline.loading
                    val prog =
                        if (timeline.total > 0L && timeline.current >= 0L) {
                            timeline.current.toFloat() / timeline.total
                        } else {
                            0f
                        }
                    setProgress(prog)
                }
            }
        job1.join()
        job2.join()
        job4.join()
    }

    // Liquid glass owns the surface entirely when it is on, so it wins outright. Otherwise the
    // translucent setting thins the palette colour enough to see content through it, at the same
    // 0.82 the nav bar uses so the two read as one decision rather than two arbitrary numbers.
    val containerColor =
        when {
            isLiquidGlassEnabled == DataStoreManager.TRUE -> Color.Transparent
            isTranslucentMiniPlayer == DataStoreManager.TRUE -> background.value.copy(alpha = 0.82f)
            else -> background.value
        }

    if (getPlatform() == Platform.Android) {
        Card(
            shape = if (isLiquidGlassEnabled == DataStoreManager.TRUE) CircleShape else SonoraShapes.Container,
            colors =
                CardDefaults.cardColors(
                    containerColor = containerColor,
                    disabledContainerColor = containerColor,
                ),
            modifier =
                modifier
                    .then(
                        if (isLiquidGlassEnabled == DataStoreManager.TRUE) {
                            Modifier.liquidGlass(backdrop, layer, luminanceAnimation.value, SonoraShapes.Container)
                        } else {
                            Modifier
                        },
                    ).then(
                        Modifier
                            .clipToBounds()
                            .offset { IntOffset(0, offsetY.value.roundToInt()) }
                            .clickable(
                                onClick = onClick,
                            ).pointerInput(Unit) {
                                detectVerticalDragGestures(
                                    onDragStart = {
                                    },
                                    onVerticalDrag = { change: PointerInputChange, dragAmount: Float ->
                                        if (offsetY.value + dragAmount > 0) {
                                            coroutineScope.launch {
                                                change.consume()
                                                offsetY.animateTo(offsetY.value + 2 * dragAmount)
                                                Logger.w("MiniPlayer", "Dragged ${offsetY.value}")
                                            }
                                        }
                                    },
                                    onDragCancel = {
                                        coroutineScope.launch {
                                            offsetY.animateTo(0f)
                                        }
                                    },
                                    onDragEnd = {
                                        Logger.w("MiniPlayer", "Drag Ended")
                                        coroutineScope.launch {
                                            if (offsetY.value > 70) {
                                                onClose()
                                            }
                                            offsetY.animateTo(0f)
                                        }
                                    },
                                )
                            },
                    ),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Progress is the container itself: the played portion of the mini player is tinted
                // and the rest is not, so the whole bar is the indicator. A separate line competing
                // for space at the bottom edge stops being necessary once the surface can carry the
                // information, and this survives being glanced at, which a 2dp line does not.
                //
                // A white wash rather than a fixed colour, because the base is palette derived from
                // the artwork and changes with every track. A wash lightens whatever is underneath
                // and stays legible on all of them; a fixed accent would clash with half.
                //
                // Skipped under liquid glass, where the container is deliberately transparent and
                // there is no surface to fill.
                if (isLiquidGlassEnabled != DataStoreManager.TRUE) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .background(Color.White.copy(alpha = 0.12f)),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier
                            .fillMaxSize(),
                ) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Box(modifier = Modifier.weight(1F)) {
                        Row(
                            modifier =
                                Modifier
                                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                                    .pointerInput(Unit) {
                                        detectHorizontalDragGestures(
                                            onDragStart = {
                                            },
                                            onHorizontalDrag = {
                                                change: PointerInputChange,
                                                dragAmount: Float,
                                                ->
                                                coroutineScope.launch {
                                                    change.consume()
                                                    offsetX.animateTo(offsetX.value + dragAmount * 2)
                                                    Logger.w("MiniPlayer", "Dragged ${offsetX.value}")
                                                }
                                            },
                                            onDragCancel = {
                                                Logger.w("MiniPlayer", "Drag Cancelled")
                                                coroutineScope.launch {
                                                    if (offsetX.value > 200) {
                                                        sharedViewModel.onUIEvent(UIEvent.Previous)
                                                    } else if (offsetX.value < -120) {
                                                        sharedViewModel.onUIEvent(UIEvent.Next)
                                                    }
                                                    offsetX.animateTo(0f)
                                                }
                                            },
                                            onDragEnd = {
                                                Logger.w("MiniPlayer", "Drag Ended")
                                                coroutineScope.launch {
                                                    if (offsetX.value > 200) {
                                                        sharedViewModel.onUIEvent(UIEvent.Previous)
                                                    } else if (offsetX.value < -120) {
                                                        sharedViewModel.onUIEvent(UIEvent.Next)
                                                    }
                                                    offsetX.animateTo(0f)
                                                }
                                            },
                                        )
                                    },
                        ) {
                            AsyncImage(
                                model =
                                    ImageRequest
                                        .Builder(LocalPlatformContext.current)
                                        .data(songEntity?.thumbnails)
                                        .crossfade(550)
                                        .build(),
                                placeholder = rememberHolderPainter(),
                                error = rememberHolderPainter(),
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                onSuccess = {
                                    bitmap =
                                        it.result.image.toImageBitmap()
                                },
                                modifier =
                                    Modifier
                                        .size(40.dp)
                                        .align(Alignment.CenterVertically)
                                        .clip(
                                            SonoraShapes.Container,
                                        ),
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            AnimatedContent(
                                targetState = songEntity,
                                modifier = Modifier.weight(1F).fillMaxHeight(),
                                contentAlignment = Alignment.CenterStart,
                                transitionSpec = {
                                    // Compare the incoming number with the previous number.
                                    if (targetState != initialState) {
                                        // If the target number is larger, it slides up and fades in
                                        // while the initial (smaller) number slides up and fades out.
                                        (
                                            slideInHorizontally { width ->
                                                width
                                            } + fadeIn()
                                        ).togetherWith(
                                            slideOutHorizontally { width -> +width } + fadeOut(),
                                        )
                                    } else {
                                        // If the target number is smaller, it slides down and fades in
                                        // while the initial number slides down and fades out.
                                        (
                                            slideInHorizontally { width ->
                                                +width
                                            } + fadeIn()
                                        ).togetherWith(
                                            slideOutHorizontally { width -> width } + fadeOut(),
                                        )
                                    }.using(
                                        // Disable clipping since the faded slide-in/out should
                                        // be displayed out of bounds.
                                        SizeTransform(clip = false),
                                    )
                                },
                            ) { target ->
                                if (target != null) {
                                    Column(
                                        Modifier
                                            .wrapContentHeight()
                                            .align(Alignment.CenterVertically),
                                    ) {
                                        Text(
                                            text = (songEntity?.title ?: "").toString(),
                                            style = typo().labelSmall,
                                            color = textColor,
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
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            androidx.compose.animation.AnimatedVisibility(visible = songEntity?.isExplicit == true) {
                                                ExplicitBadge(
                                                    modifier =
                                                        Modifier
                                                            .size(20.dp)
                                                            .padding(end = 4.dp)
                                                            .weight(1f),
                                                )
                                            }
                                            Text(
                                                text = (songEntity?.artistName?.connectArtists() ?: ""),
                                                style = typo().bodySmall,
                                                maxLines = 1,
                                                color = textColor,
                                                modifier =
                                                    Modifier
                                                        .weight(1f)
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
                        }
                    }
                    // Even 8dp between every control, 12dp to separate the cluster from the title.
                    //
                    // Tight on purpose. Each button already carries its own padding inside a 40dp or
                    // 48dp target, so the gaps between them are visual space on top of space that is
                    // already there. Every dp here comes out of the title column, which is the part
                    // that has to shrink first when the bar is narrow, and narrow is now the normal
                    // case: collapsed, this sits beside the nav button rather than across the screen.
                    Spacer(modifier = Modifier.width(4.dp))
                    HeartCheckBox(checked = liked, size = 30, tint = textColor) {
                        sharedViewModel.onUIEvent(UIEvent.ToggleLike)
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    Crossfade(targetState = loading, label = "") {
                        if (it) {
                            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                                SonoraSpinner(
                                    size = 18.dp,
                                    strokeWidth = 3.dp,
                                    color = textColor,
                                )
                            }
                        } else {
                            PlayPauseButton(isPlaying = isPlaying, modifier = Modifier.size(48.dp), tint = textColor) {
                                sharedViewModel.onUIEvent(UIEvent.PlayPause)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    // Skip is the other control worth having without opening Now Playing.
                    Box(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (controllerState.isNextAvailable) {
                                        sharedViewModel.onUIEvent(UIEvent.Next)
                                    }
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = SonoraIcons.SkipNext,
                            tint = if (controllerState.isNextAvailable) textColor else textColor.copy(alpha = 0.4f),
                            contentDescription = stringResource(Res.string.skip_next),
                            modifier = Modifier.size(28.dp),
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                }
                // Liquid glass keeps the line, because the container fill has nothing to fill: the
                // surface is transparent by design in that mode, so tinting a fraction of it would
                // tint a fraction of whatever happens to be scrolling underneath. The two modes end
                // up showing progress differently, which is correct rather than inconsistent, since
                // one of them has a surface to use and the other deliberately does not.
                if (isLiquidGlassEnabled == DataStoreManager.TRUE) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                                .align(Alignment.BottomCenter),
                    ) {
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(2.dp),
                            color = textColor,
                            trackColor = textColor.copy(alpha = 0.2f),
                            strokeCap = StrokeCap.Round,
                            drawStopIndicator = {},
                        )
                    }
                }
            }
        }
    } else {
        // Desktop bottom bar surface follows the theme (haze over content), so text and controls
        // use the theme foreground token instead of the artwork-luminance colour.
        val textColor = MaterialTheme.colorScheme.onBackground

        // Crossfade: RGB rainbow color cycling while transitioning between tracks, mirroring the
        // Now Playing screen so the desktop bar signals a crossfade the same way.
        val crossfadeTransition = rememberInfiniteTransition(label = "miniPlayerCrossfadeRainbow")
        val rainbowHue by crossfadeTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "miniPlayerRainbowHue",
        )
        val progressColor by animateColorAsState(
            targetValue =
                if (timelineState.isCrossfading) {
                    hsvToColor(rainbowHue, 1f, 1f)
                } else {
                    textColor
                },
            animationSpec = tween(300),
            label = "miniPlayerCrossfadeColor",
        )

        var isSliding by rememberSaveable {
            mutableStateOf(false)
        }
        var sliderValue by rememberSaveable {
            mutableFloatStateOf(0f)
        }
        var showQueueBottomSheet by rememberSaveable {
            mutableStateOf(false)
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
        if (showQueueBottomSheet) {
            QueueBottomSheet(
                onDismiss = {
                    showQueueBottomSheet = false
                },
            )
        }
        Box(
            modifier.then(
                Modifier.clickable {
                    onClick()
                },
            ),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Part 1
                Box(modifier = Modifier.weight(1f).padding(vertical = 16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model =
                                ImageRequest
                                    .Builder(LocalPlatformContext.current)
                                    .data(songEntity?.thumbnails)
                                    .crossfade(550)
                                    .build(),
                            placeholder = rememberHolderPainter(),
                            error = rememberHolderPainter(),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            onSuccess = {
                                bitmap =
                                    it.result.image.toImageBitmap()
                            },
                            modifier =
                                Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1f)
                                    .align(Alignment.CenterVertically)
                                    .clip(
                                        SonoraShapes.Container,
                                    ),
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = (songEntity?.title ?: "").toString(),
                                style = typo().labelSmall,
                                color = textColor,
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.animation.AnimatedVisibility(visible = songEntity?.isExplicit == true) {
                                    ExplicitBadge(
                                        modifier =
                                            Modifier
                                                .size(20.dp)
                                                .padding(end = 4.dp)
                                                .weight(1f),
                                    )
                                }
                                Text(
                                    text = (songEntity?.artistName?.connectArtists() ?: ""),
                                    style = typo().bodySmall,
                                    maxLines = 1,
                                    modifier =
                                        Modifier
                                            .weight(1f)
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
                // Part 2
                Box(modifier = Modifier.weight(1f)) {
                    Column(Modifier.width(600.dp).padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            contentAlignment = Alignment.Center,
                        ) {
                            PlayerControlLayout(
                                controllerState,
                                isSmallSize = true,
                                contentColor = textColor,
                            ) {
                                sharedViewModel.onUIEvent(it)
                            }
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = formatDuration((timelineState.total * (sliderValue / 100f)).roundToLong()),
                                style = typo().bodyMedium,
                                textAlign = TextAlign.Left,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(50.dp),
                            )
                            // Real Slider
                            Box(
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(24.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Crossfade(timelineState.loading) {
                                        if (it) {
                                            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                                                LinearProgressIndicator(
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .height(4.dp)
                                                            .padding(
                                                                horizontal = 3.dp,
                                                            ).clip(
                                                                SonoraShapes.Container,
                                                            ),
                                                    color = textColor,
                                                    trackColor = textColor.copy(alpha = 0.3f),
                                                    strokeCap = StrokeCap.Round,
                                                )
                                            }
                                        } else {
                                            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                                                LinearProgressIndicator(
                                                    progress = { timelineState.bufferedPercent.toFloat() / 100 },
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .height(4.dp)
                                                            .padding(
                                                                horizontal = 3.dp,
                                                            ).clip(
                                                                SonoraShapes.Container,
                                                            ),
                                                    // Three levels have to stay apart on one bar: the
                                                    // slider above draws played position solid, so
                                                    // buffered-but-unplayed must be dimmed and the
                                                    // unbuffered remainder dimmer still. At full
                                                    // buffer a solid colour here would blend into the
                                                    // played part and the bar would look uniform.
                                                    color = textColor.copy(alpha = 0.35f),
                                                    trackColor =
                                                        textColor.copy(
                                                            alpha = 0.15f,
                                                        ),
                                                    strokeCap = StrokeCap.Round,
                                                    drawStopIndicator = {},
                                                )
                                            }
                                        }
                                    }
                                }
                                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                                    Slider(
                                        // Fraction, not 0..100 — see the note in NowPlayingScreen:
                                        // material3 alpha25 drops valueRange on its
                                        // binary-compatibility overload.
                                        value = sliderValue / 100f,
                                        onValueChangeFinished = {
                                            isSliding = false
                                            sharedViewModel.onUIEvent(
                                                UIEvent.UpdateProgress(sliderValue),
                                            )
                                        },
                                        onValueChange = {
                                            isSliding = true
                                            sliderValue = it * 100f
                                        },
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(top = 3.dp)
                                                .align(
                                                    Alignment.TopCenter,
                                                ),
                                        track = { sliderState ->
                                            SliderDefaults.Track(
                                                modifier =
                                                    Modifier
                                                        .height(5.dp),
                                                enabled = true,
                                                sliderState = sliderState,
                                                colors =
                                                    SliderDefaults.colors().copy(
                                                        thumbColor = progressColor,
                                                        activeTrackColor = progressColor,
                                                        inactiveTrackColor = Color.Transparent,
                                                    ),
                                                thumbTrackGapSize = 0.dp,
                                                drawTick = { _, _ -> },
                                                drawStopIndicator = null,
                                            )
                                        },
                                        thumb = {
                                            SliderDefaults.Thumb(
                                                modifier =
                                                    Modifier
                                                        .height(18.dp)
                                                        .width(8.dp)
                                                        .padding(
                                                            vertical = 4.dp,
                                                        ),
                                                thumbSize = DpSize(8.dp, 8.dp),
                                                interactionSource =
                                                    remember {
                                                        MutableInteractionSource()
                                                    },
                                                colors =
                                                    SliderDefaults.colors().copy(
                                                        thumbColor = progressColor,
                                                        activeTrackColor = progressColor,
                                                        inactiveTrackColor = Color.Transparent,
                                                    ),
                                                enabled = true,
                                            )
                                        },
                                    )
                                }
                            }
                            Text(
                                text = formatDuration(timelineState.total),
                                style = typo().bodyMedium,
                                textAlign = TextAlign.Right,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(50.dp),
                            )
                        }
                    }
                }
                Box(Modifier.weight(1f)) {
                    Row(Modifier.fillMaxHeight().align(Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) {
                        HeartCheckBox(checked = controllerState.isLiked, size = 30) {
                            sharedViewModel.onUIEvent(UIEvent.ToggleLike)
                        }
                        Spacer(Modifier.width(4.dp))
                        // Queue Button
                        IconButton(
                            onClick = {
                                showQueueBottomSheet = true
                            },
                        ) {
                            Icon(
                                imageVector = SonoraIcons.QueueMusic,
                                tint = textColor,
                                contentDescription = "",
                            )
                        }
                        Spacer(Modifier.width(2.dp))
                        // Desktop mini player button (JVM only)
                        if (getPlatform() == Platform.Desktop) {
                            IconButton(onClick = { toggleMiniPlayer() }) {
                                Icon(
                                    imageVector = SonoraIcons.OpenInNew,
                                    contentDescription = "Mini Player",
                                )
                            }
                        }
                        var isVolumeSliding by rememberSaveable {
                            mutableStateOf(false)
                        }
                        var volumeValue by rememberSaveable {
                            mutableFloatStateOf(0f)
                        }
                        LaunchedEffect(key1 = controllerState, key2 = isVolumeSliding) {
                            if (!isVolumeSliding) {
                                volumeValue = controllerState.volume
                            }
                        }
                        // Remembers the level to come back to when unmuting, so the button restores
                        // what the user was listening at instead of jumping to full volume.
                        // Starting muted leaves nothing to restore, so full volume stays the fallback.
                        var previousVolumeValue by rememberSaveable {
                            mutableFloatStateOf(controllerState.volume.takeIf { it > 0f } ?: 1f)
                        }
                        LaunchedEffect(controllerState.volume) {
                            if (controllerState.volume > 0f) {
                                previousVolumeValue = controllerState.volume
                            }
                        }
                        // The slider only claims space while the pointer is over the volume cluster.
                        // `hoverable` sits on the Row wrapping both the icon and the slider so moving
                        // between them never drops the hover, and an in-progress drag keeps it open
                        // even when the pointer slips outside.
                        val volumeInteractionSource = remember { MutableInteractionSource() }
                        val isVolumeHovered by volumeInteractionSource.collectIsHoveredAsState()
                        Row(
                            modifier = Modifier.hoverable(volumeInteractionSource),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(
                                onClick = {
                                    // Toggle mute/unmute
                                    if (controllerState.volume > 0f) {
                                        sharedViewModel.onUIEvent(UIEvent.UpdateVolume(0f))
                                    } else {
                                        sharedViewModel.onUIEvent(
                                            UIEvent.UpdateVolume(previousVolumeValue.coerceIn(0.1f, 1f)),
                                        )
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector =
                                        if (controllerState.volume > 0f) {
                                            SonoraIcons.VolumeUp
                                        } else {
                                            SonoraIcons.VolumeOff
                                        },
                                    contentDescription = if (controllerState.volume > 0f) "Mute" else "Unmute",
                                )
                            }
                            AnimatedVisibility(
                                visible = isVolumeHovered || isVolumeSliding,
                                enter = expandHorizontally() + fadeIn(),
                                exit = shrinkHorizontally() + fadeOut(),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Spacer(Modifier.width(2.dp))
                                    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                                        Slider(
                                            value = volumeValue,
                                            onValueChangeFinished = {
                                                isVolumeSliding = false
                                                sharedViewModel.onUIEvent(
                                                    UIEvent.UpdateVolume(volumeValue.coerceIn(0f, 1f)),
                                                )
                                            },
                                            onValueChange = {
                                                isVolumeSliding = true
                                                volumeValue = it
                                            },
                                            valueRange = 0f..1f,
                                            modifier =
                                                Modifier
                                                    .padding(top = 3.dp)
                                                    .width(64.dp),
                                            track = { sliderState ->
                                                SliderDefaults.Track(
                                                    modifier =
                                                        Modifier
                                                            .height(5.dp),
                                                    enabled = true,
                                                    sliderState = sliderState,
                                                    colors =
                                                        SliderDefaults.colors().copy(
                                                            thumbColor = textColor,
                                                            activeTrackColor = textColor,
                                                            inactiveTrackColor = textColor.copy(alpha = 0.3f),
                                                        ),
                                                    thumbTrackGapSize = 0.dp,
                                                    drawTick = { _, _ -> },
                                                    drawStopIndicator = null,
                                                )
                                            },
                                            thumb = {
                                                SliderDefaults.Thumb(
                                                    modifier =
                                                        Modifier
                                                            .height(18.dp)
                                                            .width(8.dp)
                                                            .padding(
                                                                vertical = 4.dp,
                                                            ),
                                                    thumbSize = DpSize(8.dp, 8.dp),
                                                    interactionSource =
                                                        remember {
                                                            MutableInteractionSource()
                                                        },
                                                    colors =
                                                        SliderDefaults.colors().copy(
                                                            thumbColor = textColor,
                                                            activeTrackColor = textColor,
                                                            inactiveTrackColor = textColor.copy(alpha = 0.3f),
                                                        ),
                                                    enabled = true,
                                                )
                                            },
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.width(4.dp))
                        IconButton(onClick = { onClose() }) {
                            Icon(SonoraIcons.Close, "")
                        }
                    }
                }
            }
        }
    }
}

/** Roughly a third of the player width, matching the reference proportion. */
private const val PROGRESS_BAR_WIDTH_FRACTION = 0.34f
