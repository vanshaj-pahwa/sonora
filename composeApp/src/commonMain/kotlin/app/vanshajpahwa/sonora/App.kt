package app.vanshajpahwa.sonora

import androidx.compose.animation.AnimatedVisibility
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import coil3.toUri
import app.vanshajpahwa.domain.data.player.GenericMediaItem
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.domain.manager.DataStoreManager.Values.TRUE
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.expect.Orientation
import app.vanshajpahwa.sonora.expect.currentOrientation
import app.vanshajpahwa.sonora.expect.openUrl
import app.vanshajpahwa.sonora.expect.ui.layerBackdrop
import app.vanshajpahwa.sonora.expect.ui.rememberBackdrop
import app.vanshajpahwa.sonora.extension.copy
import app.vanshajpahwa.sonora.ui.component.OfflineBanner
import app.vanshajpahwa.sonora.ui.component.AppBottomNavigationBar
import app.vanshajpahwa.sonora.ui.component.AppNavigationRail
import app.vanshajpahwa.sonora.ui.component.LiquidGlassAppBottomNavigationBar
import app.vanshajpahwa.sonora.ui.icon.ArrowForwardIos
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.navigation.destination.home.NotificationDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.AlbumDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.ArtistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.PlaylistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.player.FullscreenDestination
import app.vanshajpahwa.sonora.ui.navigation.graph.AppNavigationGraph
import app.vanshajpahwa.sonora.ui.screen.MiniPlayer
import app.vanshajpahwa.sonora.ui.screen.player.NowPlayingScreen
import app.vanshajpahwa.sonora.ui.screen.player.NowPlayingScreenContent
import app.vanshajpahwa.sonora.ui.theme.AppTheme
import app.vanshajpahwa.sonora.ui.theme.ForceDarkContent
import app.vanshajpahwa.sonora.ui.theme.parseThemeColorHex
import app.vanshajpahwa.sonora.ui.theme.fontFamily
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.utils.VersionManager
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import app.vanshajpahwa.sonora.viewModel.RemoteFeedHealth
import org.koin.compose.koinInject
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.cancel
import app.vanshajpahwa.sonora.resources.do_not_show_again
import app.vanshajpahwa.sonora.resources.download
import app.vanshajpahwa.sonora.resources.good_night
import app.vanshajpahwa.sonora.resources.notification
import app.vanshajpahwa.sonora.resources.sleep_timer_off
import app.vanshajpahwa.sonora.resources.this_app_needs_to_access_your_notification
import app.vanshajpahwa.sonora.resources.this_link_is_not_supported
import app.vanshajpahwa.sonora.resources.unknown
import app.vanshajpahwa.sonora.resources.update_available
import app.vanshajpahwa.sonora.resources.update_message
import app.vanshajpahwa.sonora.resources.version_format
import app.vanshajpahwa.sonora.resources.yes
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class, ExperimentalFoundationApi::class)
@Composable
fun App(
    viewModel: SharedViewModel = koinInject(),
    remoteFeedHealth: RemoteFeedHealth = koinInject(),
) {
    val isRemoteUnreachable by remoteFeedHealth.unreachable.collectAsStateWithLifecycle()
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val navController = rememberNavController()

    val sleepTimerState by viewModel.sleepTimerState.collectAsStateWithLifecycle()
    val nowPlayingData by viewModel.nowPlayingState.collectAsStateWithLifecycle()
    val updateData by viewModel.updateResponse.collectAsStateWithLifecycle()
    val intent by viewModel.intent.collectAsStateWithLifecycle()
    val showNotificationPermissionDialog by viewModel.showNotificationPermissionDialog.collectAsStateWithLifecycle()

    val isTranslucentBottomBar by viewModel.getTranslucentBottomBar().collectAsStateWithLifecycle(DataStoreManager.FALSE)
    val isLiquidGlassEnabled by viewModel.getEnableLiquidGlass().collectAsStateWithLifecycle(DataStoreManager.FALSE)

    val themeMode by viewModel.getThemeMode().collectAsStateWithLifecycle(DataStoreManager.THEME_MODE_DARK)
    val themeColorSource by viewModel.getThemeColorSource().collectAsStateWithLifecycle(DataStoreManager.THEME_COLOR_DEFAULT)
    val customThemeColorHex by viewModel.getCustomThemeColor().collectAsStateWithLifecycle(DataStoreManager.DEFAULT_THEME_COLOR_HEX)
    val fontChoice by viewModel.getFontFamilyChoice().collectAsStateWithLifecycle(DataStoreManager.FONT_MANROPE)
    // MiniPlayer visibility logic.
    //
    // Derived from the now-playing state rather than held as its own flag. It used to start
    // hardcoded `true` and get corrected by a LaunchedEffect a frame later, so with an empty queue
    // the mini player composed and then vanished. Any recomposition of this subtree replayed it,
    // which is why flipping the liquid-glass toggle appeared to "load" a player that should never
    // have existed. Reading the state directly means there is no wrong value to correct.
    val isShowMiniPlayer by remember {
        derivedStateOf {
            val item = nowPlayingData?.mediaItem
            item != null && item != GenericMediaItem.EMPTY
        }
    }

    // Now playing screen
    var isShowNowPlaylistScreen by rememberSaveable {
        mutableStateOf(false)
    }

    // Fullscreen
    var isInFullscreen by rememberSaveable {
        mutableStateOf(false)
    }

    var isNavBarVisible by rememberSaveable {
        mutableStateOf(true)
    }

    var shouldShowUpdateDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val hazeState =
        rememberHazeState(
            blurEnabled = true,
        )

    LaunchedEffect(intent) {
        val intent = intent ?: return@LaunchedEffect
        val data = intent.data
        Logger.d("MainActivity", "onCreate: $data")
        if (data != null) {
            if (data == "sonora://notification".toUri()) {
                viewModel.setIntent(null)
                navController.navigate(
                    NotificationDestination,
                )
            } else if (data.scheme == "sonora") {
                // sonora://watch?v=VIDEO_ID  (host="watch", no path)
                // sonora://playlist?list=PLAYLIST_ID
                // sonora://channel/CHANNEL_ID
                val segments = data.pathSegments
                // The host IS the appPath here, and the path carries only the trailing id.
                val appPath = data.host
                Logger.d("MainActivity", "sonora:// deep link, appPath: $appPath")
                viewModel.setIntent(null)
                when (appPath) {
                    "watch" -> {
                        data.getQueryParameter("v")?.let { videoId ->
                            viewModel.loadSharedMediaItem(videoId)
                        }
                    }

                    "playlist" -> {
                        data.getQueryParameter("list")?.let { playlistId ->
                            if (playlistId.startsWith("OLAK5uy_")) {
                                navController.navigate(AlbumDestination(browseId = playlistId))
                            } else if (playlistId.startsWith("VL")) {
                                navController.navigate(PlaylistDestination(playlistId = playlistId))
                            } else {
                                navController.navigate(PlaylistDestination(playlistId = "VL$playlistId"))
                            }
                        }
                    }

                    "channel", "c" -> {
                        // sonora://channel/UCxxx → segments = ["UCxxx"]
                        val artistId = segments.firstOrNull()
                        artistId?.let {
                            if (it.startsWith("UC")) {
                                navController.navigate(ArtistDestination(channelId = it))
                            } else {
                                viewModel.makeToast(getString(Res.string.this_link_is_not_supported))
                            }
                        }
                    }

                    "album" -> {
                        data.getQueryParameter("id")?.let { albumId ->
                            navController.navigate(AlbumDestination(browseId = albumId))
                        }
                    }

                    else -> {
                        viewModel.makeToast(getString(Res.string.this_link_is_not_supported))
                    }
                }
            } else {
                Logger.d("MainActivity", "onCreate: $data")
                when (val path = data.pathSegments.firstOrNull()) {
                    "playlist" -> {
                        data
                            .getQueryParameter("list")
                            ?.let { playlistId ->
                                viewModel.setIntent(null)
                                if (playlistId.startsWith("OLAK5uy_")) {
                                    navController.navigate(
                                        AlbumDestination(
                                            browseId = playlistId,
                                        ),
                                    )
                                } else if (playlistId.startsWith("VL")) {
                                    navController.navigate(
                                        PlaylistDestination(
                                            playlistId = playlistId,
                                        ),
                                    )
                                } else {
                                    navController.navigate(
                                        PlaylistDestination(
                                            playlistId = "VL$playlistId",
                                        ),
                                    )
                                }
                            }
                    }

                    "channel", "c" -> {
                        data.lastPathSegment?.let { artistId ->
                            if (artistId.startsWith("UC")) {
                                viewModel.setIntent(null)
                                navController.navigate(
                                    ArtistDestination(
                                        channelId = artistId,
                                    ),
                                )
                            } else {
                                viewModel.makeToast(
                                    getString(
                                        Res.string.this_link_is_not_supported,
                                    ),
                                )
                            }
                        }
                    }

                    else -> {
                        when {
                            path == "watch" -> data.getQueryParameter("v")
                            data.host == "youtu.be" -> path
                            else -> null
                        }?.let { videoId ->
                            viewModel.loadSharedMediaItem(videoId)
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(updateData) {
        val response = updateData ?: return@LaunchedEffect
        if (viewModel.showedUpdateDialog &&
            response.tagName != getString(Res.string.version_format, VersionManager.getVersionName())
        ) {
            shouldShowUpdateDialog = true
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        Logger.d("MainActivity", "Current destination: ${navBackStackEntry?.destination?.route}")
        if (navBackStackEntry?.destination?.route?.contains("FullscreenDestination") == true) {
            isShowNowPlaylistScreen = false
        }
        isInFullscreen = navBackStackEntry?.destination?.hierarchy?.any {
            it.hasRoute(FullscreenDestination::class)
        } == true
    }
    var isScrolledToTop by rememberSaveable {
        mutableStateOf(false)
    }
    val titleScrolling by viewModel.getTitleScrolling().collectAsStateWithLifecycle(TRUE)
    val isTablet = windowSize.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isTabletLandscape = isTablet && currentOrientation() == Orientation.LANDSCAPE

    AppTheme(
        themeMode = themeMode,
        themeColorSource = themeColorSource,
        customThemeColor = parseThemeColorHex(customThemeColorHex),
        fontChoice = fontChoice,
        titleScrolling = titleScrolling == TRUE,
    ) {
        // Backdrop base must match the theme: white page → white glass, dark/AMOLED → black glass.
        // Read inside AppTheme so MaterialTheme reflects the resolved scheme (light background is #FFFFFF).
        val backdrop =
            rememberBackdrop(
                if (MaterialTheme.colorScheme.background.luminance() > 0.5f) Color.White else Color.Black,
            )
        Scaffold(
            bottomBar = {
                if (!isTablet) {
                    AnimatedVisibility(
                        isNavBarVisible,
                        enter = fadeIn() + slideInHorizontally(),
                        exit = fadeOut(),
                    ) {
                        Column {
                            if (isLiquidGlassEnabled == TRUE) {
                                LiquidGlassAppBottomNavigationBar(
                                    navController = navController,
                                    backdrop = backdrop,
                                    viewModel = viewModel,
                                    onOpenNowPlaying = { isShowNowPlaylistScreen = true },
                                    isScrolledToTop = isScrolledToTop,
                                ) { klass ->
                                    viewModel.reloadDestination(klass)
                                }
                            } else {
                                AppBottomNavigationBar(
                                    navController = navController,
                                    isTranslucentBackground = isTranslucentBottomBar == TRUE,
                                    // Same signal the glass variant already receives, so both bars
                                    // collapse and expand on the same triggers.
                                    isScrolledToTop = isScrolledToTop,
                                    // Handed to the bar so it can place the player relative to its
                                    // own collapse state. The glass variant already owns both for
                                    // the same reason.
                                    miniPlayer =
                                        if (isShowMiniPlayer) {
                                            { widthModifier ->
                                                MiniPlayer(
                                                    widthModifier
                                                        .height(56.dp)
                                                        .padding(bottom = 4.dp),
                                                    backdrop = backdrop,
                                                    onClick = {
                                                        isShowNowPlaylistScreen = true
                                                    },
                                                    onClose = {
                                                        viewModel.stopPlayer()
                                                        viewModel.isServiceRunning = false
                                                    },
                                                )
                                            }
                                        } else {
                                            null
                                        },
                                ) { klass ->
                                    viewModel.reloadDestination(klass)
                                }
                            }
                            // Under both, so it never displaces the controls. Animated rather than
                            // popped in: the bar appears while the user is mid scroll and a sudden
                            // layout jump under the thumb reads as a glitch.
                            AnimatedVisibility(
                                visible = isRemoteUnreachable,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut(),
                            ) {
                                OfflineBanner(
                                    onRetry = { remoteFeedHealth.requestRetry() },
                                    modifier =
                                        Modifier
                                            .padding(horizontal = 12.dp)
                                            .padding(bottom = 4.dp),
                                )
                            }
                        }
                    }
                }
            },
            content = { innerPadding ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .then(
                            if (isLiquidGlassEnabled == TRUE && !isTablet) {
                                Modifier.layerBackdrop(backdrop)
                            } else {
                                Modifier
                            },
                        ),
                ) {
                    Row(
                        Modifier.fillMaxSize(),
                    ) {
                        if (isTablet && !isInFullscreen) {
                            AppNavigationRail(
                                navController = navController,
                            ) { klass ->
                                viewModel.reloadDestination(klass)
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxSize()
                                .weight(1f),
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .then(
                                        if (isLiquidGlassEnabled == TRUE && isTablet && !isInFullscreen) {
                                            Modifier.layerBackdrop(backdrop)
                                        } else {
                                            Modifier
                                        },
                                    ).hazeSource(hazeState),
                            ) {
                                AppNavigationGraph(
                                    innerPadding = innerPadding,
                                    navController = navController,
                                    hideNavBar = {
                                        isNavBarVisible = false
                                    },
                                    showNavBar = {
                                        isNavBarVisible = true
                                    },
                                    showNowPlayingSheet = {
                                        isShowNowPlaylistScreen = true
                                    },
                                    onScrolling = {
                                        isScrolledToTop = it
                                    },
                                )
                            }
                            this@Row.AnimatedVisibility(
                                modifier =
                                    Modifier
                                        .padding(innerPadding)
                                        .align(Alignment.BottomCenter),
                                visible = isShowMiniPlayer && isTablet && !isInFullscreen,
                                // Rises from below rather than sliding in from the side. The bar
                                // lives at the bottom of the screen, so entering from off-screen
                                // bottom is the motion its position implies; a horizontal slide
                                // reads as the wrong object arriving. Exit mirrors it so the same
                                // thing appears to leave the way it came.
                                enter =
                                    slideInVertically(
                                        initialOffsetY = { it },
                                        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
                                    ) + fadeIn(animationSpec = tween(220)),
                                exit =
                                    slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = tween(200),
                                    ) + fadeOut(animationSpec = tween(160)),
                            ) {
                                MiniPlayer(
                                    if (getPlatform() == Platform.Android) {
                                        Modifier
                                            .height(56.dp)
                                            .fillMaxWidth(0.8f)
                                            .padding(
                                                horizontal = 12.dp,
                                            ).padding(
                                                bottom = 4.dp,
                                            )
                                    } else {
                                        Modifier
                                            .fillMaxWidth()
                                            .height(84.dp)
                                            .background(Color.Transparent)
                                            .hazeEffect(hazeState, style = HazeMaterials.ultraThin()) {
                                                blurEnabled = true
                                            }
                                    },
                                    backdrop = backdrop,
                                    onClick = {
                                        isShowNowPlaylistScreen = true
                                    },
                                    onClose = {
                                        viewModel.stopPlayer()
                                        viewModel.isServiceRunning = false
                                    },
                                )
                            }
                        }
                        if (isTablet && isTabletLandscape && !isInFullscreen) {
                            AnimatedVisibility(
                                isShowNowPlaylistScreen,
                                enter = expandHorizontally() + fadeIn(),
                                exit = fadeOut() + shrinkHorizontally(),
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(0.35f),
                                ) {
                                    Spacer(Modifier.width(8.dp))
                                    Box(
                                        Modifier
                                            .padding(
                                                innerPadding.copy(
                                                    start = 0.dp,
                                                    top = 0.dp,
                                                    bottom = 0.dp,
                                                ),
                                            ).clip(
                                                SonoraShapes.Container,
                                            ),
                                    ) {
                                        ForceDarkContent {
                                            NowPlayingScreenContent(
                                                navController = navController,
                                                sharedViewModel = viewModel,
                                                isExpanded = true,
                                                dismissIcon = SonoraIcons.ArrowForwardIos,
                                            ) {
                                                isShowNowPlaylistScreen = false
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (isShowNowPlaylistScreen && !isTabletLandscape) {
                    ForceDarkContent {
                        NowPlayingScreen(
                            navController = navController,
                        ) {
                            isShowNowPlaylistScreen = false
                        }
                    }
                }

                if (sleepTimerState.isDone) {
                    Logger.w("MainActivity", "Sleep Timer Done: $sleepTimerState")
                    AlertDialog(
                        properties =
                            DialogProperties(
                                dismissOnBackPress = false,
                                dismissOnClickOutside = false,
                            ),
                        onDismissRequest = {
                            viewModel.stopSleepTimer()
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.stopSleepTimer()
                            }) {
                                Text(
                                    stringResource(Res.string.yes),
                                    style = typo().bodySmall,
                                )
                            }
                        },
                        text = {
                            Text(
                                stringResource(Res.string.sleep_timer_off),
                                style = typo().labelSmall,
                            )
                        },
                        title = {
                            Text(
                                stringResource(Res.string.good_night),
                                style = typo().bodySmall,
                            )
                        },
                    )
                }

                if (shouldShowUpdateDialog) {
                    val response = updateData ?: return@Scaffold
                    AlertDialog(
                        properties =
                            DialogProperties(
                                dismissOnBackPress = false,
                                dismissOnClickOutside = false,
                            ),
                        onDismissRequest = {
                            shouldShowUpdateDialog = false
                            viewModel.showedUpdateDialog = false
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    shouldShowUpdateDialog = false
                                    viewModel.showedUpdateDialog = false
                                    openUrl("https://github.com/vanshaj-pahwa")
                                },
                            ) {
                                Text(
                                    stringResource(Res.string.download),
                                    style = typo().bodySmall,
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    shouldShowUpdateDialog = false
                                    viewModel.showedUpdateDialog = false
                                },
                            ) {
                                Text(
                                    stringResource(Res.string.cancel),
                                    style = typo().bodySmall,
                                )
                            }
                        },
                        title = {
                            Text(
                                stringResource(Res.string.update_available),
                                style = typo().labelSmall,
                            )
                        },
                        text = {
                            val formatted =
                                response.releaseTime?.let { input ->
                                    try {
                                        val instant = kotlin.time.Instant.parse(input)
                                        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                                        dateTime.format(
                                            LocalDateTime.Format {
                                                day()
                                                char(' ')
                                                monthName(MonthNames.ENGLISH_ABBREVIATED)
                                                char(' ')
                                                year()
                                                char(' ')
                                                hour()
                                                char(':')
                                                minute()
                                                char(':')
                                                second()
                                            },
                                        )
                                    } catch (e: Exception) {
                                        stringResource(Res.string.unknown)
                                    }
                                } ?: stringResource(Res.string.unknown)

                            val updateMessage =
                                runBlocking {
                                    getString(
                                        Res.string.update_message,
                                        response.tagName,
                                        formatted,
                                    )
                                }
                            Column(
                                Modifier
                                    .heightIn(
                                        max = 400.dp,
                                    ).verticalScroll(
                                        rememberScrollState(),
                                    ),
                            ) {
                                Text(
                                    text = updateMessage,
                                    style = typo().labelMedium,
                                    modifier =
                                        Modifier.padding(
                                            vertical = 8.dp,
                                        ),
                                )
                                Markdown(
                                    response.body,
                                    typography =
                                        markdownTypography(
                                            h1 = typo().labelLarge,
                                            h2 = typo().labelMedium,
                                            h3 = typo().labelSmall,
                                            text = typo().bodySmall,
                                            bullet = typo().bodySmall,
                                            paragraph = typo().bodySmall,
                                            textLink =
                                                TextLinkStyles(
                                                    SpanStyle(
                                                        
                                                        fontWeight = FontWeight.Normal,
                                                        fontFamily = fontFamily(),
                                                        textDecoration = TextDecoration.Underline,
                                                    ),
                                                ),
                                        ),
                                )
                            }
                        },
                    )
                }

                if (showNotificationPermissionDialog) {
                    var doNotShowAgain by remember { mutableStateOf(false) }
                    AlertDialog(
                        onDismissRequest = {
                            viewModel.dismissNotificationPermissionDialog(doNotShowAgain)
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.dismissNotificationPermissionDialog(doNotShowAgain)
                                },
                            ) {
                                Text(
                                    stringResource(Res.string.yes),
                                    style = typo().bodySmall,
                                )
                            }
                        },
                        title = {
                            Text(
                                stringResource(Res.string.notification),
                                style = typo().labelSmall,
                            )
                        },
                        text = {
                            Column {
                                Text(
                                    stringResource(Res.string.this_app_needs_to_access_your_notification),
                                    style = typo().bodySmall,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier =
                                        Modifier
                                            .clickable { doNotShowAgain = !doNotShowAgain }
                                            .fillMaxWidth(),
                                ) {
                                    Checkbox(
                                        checked = doNotShowAgain,
                                        onCheckedChange = { doNotShowAgain = it },
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        stringResource(Res.string.do_not_show_again),
                                        style = typo().bodySmall,
                                    )
                                }
                            }
                        },
                    )
                }
            },
        )
    }
}