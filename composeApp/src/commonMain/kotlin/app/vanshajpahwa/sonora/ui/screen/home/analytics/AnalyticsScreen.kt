package app.vanshajpahwa.sonora.ui.screen.home.analytics

import androidx.compose.foundation.background
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.domain.mediaservice.handler.PlaylistType
import app.vanshajpahwa.domain.mediaservice.handler.QueueData
import app.vanshajpahwa.domain.utils.LocalResource
import app.vanshajpahwa.domain.utils.connectArtists
import app.vanshajpahwa.domain.utils.toArrayListTrack
import app.vanshajpahwa.domain.utils.toTrack
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.extension.getScreenSizeInfo
import app.vanshajpahwa.sonora.extension.getStringBlocking
import app.vanshajpahwa.sonora.extension.smoothScrimBrush
import app.vanshajpahwa.sonora.ui.component.CenterLoadingBox
import app.vanshajpahwa.sonora.ui.component.EndOfPage
import app.vanshajpahwa.sonora.ui.component.FiveImagesComponent
import app.vanshajpahwa.sonora.ui.component.ImageData
import app.vanshajpahwa.sonora.ui.component.NowPlayingBottomSheet
import app.vanshajpahwa.sonora.ui.component.SongFullWidthItems
import app.vanshajpahwa.sonora.ui.icon.ArrowBackIosNew
import app.vanshajpahwa.sonora.ui.icon.CalendarToday
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.navigation.destination.home.RecentlySongsDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.library.LibraryDynamicPlaylistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.AlbumDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.ArtistDestination
import app.vanshajpahwa.sonora.ui.screen.library.LibraryDynamicPlaylistType
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.AnalyticsUiState
import app.vanshajpahwa.sonora.viewModel.AnalyticsViewModel
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.artists
import app.vanshajpahwa.sonora.resources.date_range
import app.vanshajpahwa.sonora.resources.last_30_days
import app.vanshajpahwa.sonora.resources.last_7_days
import app.vanshajpahwa.sonora.resources.last_90_days
import app.vanshajpahwa.sonora.resources.lower_plays
import app.vanshajpahwa.sonora.resources.more
import app.vanshajpahwa.sonora.resources.no_data_analytics
import app.vanshajpahwa.sonora.resources.analytics_tracking_off
import app.vanshajpahwa.sonora.resources.seconds
import app.vanshajpahwa.sonora.resources.songs_played
import app.vanshajpahwa.sonora.resources.this_year
import app.vanshajpahwa.sonora.resources.top_song
import app.vanshajpahwa.sonora.resources.total_listened_time
import app.vanshajpahwa.sonora.resources.your_recently_played
import app.vanshajpahwa.sonora.resources.your_top_albums
import app.vanshajpahwa.sonora.resources.your_top_artists
import app.vanshajpahwa.sonora.resources.your_top_tracks

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun AnalyticsScreen(
    innerPadding: PaddingValues,
    navController: NavController,
    analyticsViewModel: AnalyticsViewModel = koinViewModel(),
    sharedViewModel: SharedViewModel = koinInject(),
) {
    val localTrackingEnabled by analyticsViewModel.localTrackingEnabled.collectAsStateWithLifecycle(initialValue = true)
    val density = LocalDensity.current
    val screenSizeInfo = getScreenSizeInfo()
    val uiState by analyticsViewModel.analyticsUIState.collectAsStateWithLifecycle()
    val playingTrack by sharedViewModel.nowPlayingState.map { it?.track?.videoId }.collectAsState(null)

    var currentItem by remember {
        mutableStateOf<SongEntity?>(null)
    }
    var itemBottomSheetShow by remember {
        mutableStateOf(false)
    }

    val onItemMoreClick: (song: SongEntity) -> Unit = {
        currentItem = it
        itemBottomSheetShow = true
    }

    val onArtistClick: (channelId: String) -> Unit = {
        navController.navigate(
            ArtistDestination(
                channelId = it,
            ),
        )
    }

    val onAlbumClick: (browseId: String) -> Unit = {
        navController.navigate(
            AlbumDestination(
                browseId = it,
            ),
        )
    }

    if (itemBottomSheetShow && currentItem != null) {
        val track = currentItem ?: return
        NowPlayingBottomSheet(
            onDismiss = {
                itemBottomSheetShow = false
                currentItem = null
            },
            navController = navController,
            song = track,
        )
    }

    LaunchedEffect(uiState) {
        Logger.d(
            "AnalyticsScreen",
            "UI State updated: ${uiState.scrobblesCount.data}, ${uiState.artistCount.data}, ${uiState.totalListenTimeInSeconds.data}",
        )
        Logger.d("AnalyticsScreen", "Top Tracks: ${uiState.topTracks.data?.joinToString { it.second.title }}")
        Logger.d("AnalyticsScreen", "Top Artists: ${uiState.topArtists.data?.joinToString { it.second.name }}")
        Logger.d("AnalyticsScreen", "Top Albums: ${uiState.topAlbums.data?.joinToString { it.second.title }}")
        Logger.d("AnalyticsScreen", "Recently Played: ${uiState.recentlyRecord.data?.joinToString { it.second.title }}")
        Logger.d("AnalyticsScreen", "Scrobbles line chart data: ${uiState.scrobblesLineChart.data}")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            // Header item
            item {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height((screenSizeInfo.hDP / 2.5).dp),
                ) {
                    when (val topTracks = uiState.topTracks) {
                        is LocalResource.Success if (!topTracks.data.isNullOrEmpty()) -> {
                            val topTrack = topTracks.data?.firstOrNull() ?: return@Box
                            AsyncImage(
                                model =
                                    ImageRequest
                                        .Builder(LocalPlatformContext.current)
                                        .data(topTrack.second.thumbnails)
                                        .diskCachePolicy(CachePolicy.ENABLED)
                                        .diskCacheKey(topTrack.second.thumbnails)
                                        .crossfade(550)
                                        .build(),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier =
                                    Modifier
                                        .align(Alignment.Center)
                                        .fillMaxSize(),
                            )
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush =
                                            smoothScrimBrush(
                                                from = MaterialTheme.colorScheme.background.copy(alpha = 0f),
                                                to = MaterialTheme.colorScheme.background,
                                                // The header is hDP/2.5 tall, so starting at 3/4 of
                                                // that left only a quarter of it to fade in — short
                                                // enough that the edge still read as a hard cut.
                                                // Half the header gives the ramp room to disappear.
                                                startY = (screenSizeInfo.hPX / 2.5f) / 2,
                                            ),
                                    ),
                            )
                            Column(
                                modifier =
                                    Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = stringResource(Res.string.top_song),
                                    style = typo().titleLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column(
                                        // Takes the leftover width and stops there. Without a
                                        // weight the column sizes to the song title, so a long
                                        // name simply grew across the "Listened time" column.
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Text(
                                            topTrack.second.title,
                                            style = typo().labelMedium,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Text(
                                            topTrack.second.artistName?.connectArtists() ?: "",
                                            style = typo().bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(
                                        modifier = Modifier.wrapContentWidth(),
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Text(
                                            "Listened time",
                                            style = typo().bodyMedium,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            maxLines = 1,
                                        )
                                        Text(
                                            "${topTrack.first.totalListeningTime} seconds",
                                            style = typo().bodyLarge,
                                            maxLines = 1,
                                        )
                                    }
                                }
                            }
                        }

                        is LocalResource.Loading -> {
                            CenterLoadingBox(
                                modifier = Modifier.fillMaxSize(),
                            )
                        }

                        else -> {
                            Box(Modifier.fillMaxSize()) {
                                // Two different empty states, because they have two different
                                // remedies. "Play some music" is actively wrong advice when tracking
                                // is off: no amount of playing will ever fill this screen, and the
                                // user is left thinking the feature is broken rather than disabled.
                                Text(
                                    if (localTrackingEnabled) {
                                        stringResource(Res.string.no_data_analytics)
                                    } else {
                                        stringResource(Res.string.analytics_tracking_off)
                                    },
                                    modifier =
                                        Modifier
                                            .align(Alignment.Center)
                                            .padding(horizontal = 32.dp),
                                    textAlign = TextAlign.Center,
                                    style = typo().bodyLarge,
                                )
                            }
                        }
                    }
                }
            }

            // Range selector lives in the content, not behind an icon in the app bar. It used to be
            // a calendar glyph with the range printed inside it at 8sp, which is smaller than the
            // minimum legible size and hid the current selection behind a tap.
            item {
                DayRangeChips(
                    selected = uiState.dayRange,
                    onSelect = { analyticsViewModel.setDayRange(it) },
                )
            }

            item {
                Row(
                    // IntrinsicSize.Max measures the tallest card and gives that height to all
                    // three. Without it the cards size themselves independently and the one whose
                    // label wraps to two lines ("Total listened time") stands taller than its
                    // neighbours, which reads as three unrelated boxes rather than one row of
                    // equal figures. Widths were already equal through weight(1f).
                    Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    val scrobbles = (uiState.scrobblesCount as? LocalResource.Success)?.data ?: 0
                    val artists = (uiState.artistCount as? LocalResource.Success)?.data ?: 0
                    val seconds = (uiState.totalListenTimeInSeconds as? LocalResource.Success)?.data ?: 0
                    AnalyticsStatCard(
                        value = "$scrobbles",
                        label = stringResource(Res.string.songs_played),
                        modifier = Modifier.weight(1f),
                    )
                    AnalyticsStatCard(
                        value = "$artists",
                        label = stringResource(Res.string.artists),
                        modifier = Modifier.weight(1f),
                    )
                    AnalyticsStatCard(
                        value = humanDuration(seconds.toLong()),
                        label = stringResource(Res.string.total_listened_time),
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                when (val recentlyRecord = uiState.recentlyRecord) {
                    is LocalResource.Success if (!recentlyRecord.data.isNullOrEmpty()) -> {
                        val records = recentlyRecord.data ?: return@item
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 12.dp),
                            ) {
                                Text(
                                    text = stringResource(Res.string.your_recently_played),
                                    style = typo().labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f),
                                )
                                TextButton(
                                    onClick = {
                                        navController.navigate(RecentlySongsDestination)
                                    },
                                    colors =
                                        ButtonDefaults
                                            .textButtonColors()
                                            .copy(
                                                contentColor = MaterialTheme.colorScheme.onSurface,
                                            ),
                                ) {
                                    Text(stringResource(Res.string.more), style = typo().bodySmall)
                                }
                            }

                            records.forEach { pair ->
                                val song = pair.second.toTrack()
                                SongFullWidthItems(
                                    track = song,
                                    isPlaying = song.videoId == playingTrack,
                                    modifier = Modifier.fillMaxWidth(),
                                    onMoreClickListener = {
                                        onItemMoreClick(pair.second)
                                    },
                                    onClickListener = {
                                        val targetList = records.map { it.second }
                                        val playTrack = pair.second
                                        with(sharedViewModel) {
                                            setQueueData(
                                                QueueData.Data(
                                                    listTracks = targetList.toArrayListTrack(),
                                                    firstPlayedTrack = playTrack.toTrack(),
                                                    playlistId = null,
                                                    playlistName = getStringBlocking(Res.string.your_recently_played),
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
                                            arrayListOf(song),
                                        )
                                    },
                                    rightView = {
                                        Text(
                                            text =
                                                pair.first.timestamp.format(
                                                    LocalDateTime.Format {
                                                        hour()
                                                        char(':')
                                                        minute()
                                                        chars(" - ")
                                                        day()
                                                        char(' ')
                                                        monthName(
                                                            MonthNames.ENGLISH_FULL,
                                                        )
                                                        char(' ')
                                                        year()
                                                    },
                                                ),
                                            style = typo().bodySmall,
                                        )
                                    },
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }

            item {
                // Top artists
                when (uiState.topArtists) {
                    is LocalResource.Success if (!uiState.topArtists.data.isNullOrEmpty()) -> {
                        val artists =
                            uiState.topArtists.data?.let {
                                if (it.size > 5) it.subList(0, 5) else it
                            } ?: return@item
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 12.dp),
                            ) {
                                Text(
                                    text = stringResource(Res.string.your_top_artists),
                                    style = typo().labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f),
                                )
                                TextButton(
                                    onClick = {
                                        navController.navigate(
                                            LibraryDynamicPlaylistDestination(
                                                type = LibraryDynamicPlaylistType.TopArtists.toStringParams(),
                                            ),
                                        )
                                    },
                                    colors =
                                        ButtonDefaults
                                            .textButtonColors()
                                            .copy(
                                                contentColor = MaterialTheme.colorScheme.onSurface,
                                            ),
                                ) {
                                    Text(stringResource(Res.string.more), style = typo().bodySmall)
                                }
                            }
                            FiveImagesComponent(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                images =
                                    artists.map {
                                        ImageData(
                                            imageUrl = it.second.thumbnails ?: "",
                                            title = it.second.name,
                                            subtitle = "${it.first.playCount} ${stringResource(Res.string.lower_plays)}",
                                            onClick = {
                                                onArtistClick(it.second.channelId)
                                            },
                                        )
                                    },
                            )
                        }
                    }

                    else -> {}
                }
            }

            item {
                // Top albums
                when (uiState.topAlbums) {
                    is LocalResource.Success if (!uiState.topAlbums.data.isNullOrEmpty()) -> {
                        val albums =
                            uiState.topAlbums.data?.let {
                                if (it.size > 5) it.subList(0, 5) else it
                            } ?: return@item
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 12.dp),
                            ) {
                                Text(
                                    text = stringResource(Res.string.your_top_albums),
                                    style = typo().labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f),
                                )
                                TextButton(
                                    onClick = {
                                        navController.navigate(
                                            LibraryDynamicPlaylistDestination(
                                                type = LibraryDynamicPlaylistType.TopAlbums.toStringParams(),
                                            ),
                                        )
                                    },
                                    colors =
                                        ButtonDefaults
                                            .textButtonColors()
                                            .copy(
                                                contentColor = MaterialTheme.colorScheme.onSurface,
                                            ),
                                ) {
                                    Text(stringResource(Res.string.more), style = typo().bodySmall)
                                }
                            }
                            FiveImagesComponent(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                images =
                                    albums.map {
                                        ImageData(
                                            imageUrl = it.second.thumbnails ?: "",
                                            title = it.second.title,
                                            subtitle = it.second.artistName?.connectArtists() ?: "",
                                            thirdTitle = "${it.first.playCount} ${stringResource(Res.string.lower_plays)}",
                                            onClick = {
                                                onAlbumClick(it.second.browseId)
                                            },
                                        )
                                    },
                            )
                        }
                    }

                    else -> {}
                }
            }

            item {
                // Top tracks
                when (val topTracks = uiState.topTracks) {
                    is LocalResource.Success if (!topTracks.data.isNullOrEmpty()) -> {
                        val tracks =
                            topTracks.data?.let {
                                if (it.size > 5) it.subList(0, 5) else it
                            } ?: return@item
                        val maxPlays = tracks.maxOf { it.first.playCount }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 12.dp),
                            ) {
                                Text(
                                    text = stringResource(Res.string.your_top_tracks),
                                    style = typo().labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f),
                                )
                                TextButton(
                                    onClick = {
                                        navController.navigate(
                                            LibraryDynamicPlaylistDestination(
                                                type = LibraryDynamicPlaylistType.TopTracks.toStringParams(),
                                            ),
                                        )
                                    },
                                    colors =
                                        ButtonDefaults
                                            .textButtonColors()
                                            .copy(
                                                contentColor = MaterialTheme.colorScheme.onSurface,
                                            ),
                                ) {
                                    Text(stringResource(Res.string.more), style = typo().bodySmall)
                                }
                            }

                            tracks.forEach { pair ->
                                val song = pair.second.toTrack()
                                SongFullWidthItems(
                                    track = song,
                                    isPlaying = song.videoId == playingTrack,
                                    modifier = Modifier.fillMaxWidth(),
                                    onMoreClickListener = {
                                        onItemMoreClick(pair.second)
                                    },
                                    onClickListener = {
                                        val targetList = tracks.map { it.second }
                                        val playTrack = pair.second
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
                                            arrayListOf(song),
                                        )
                                    },
                                    rightView = {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(0.4f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                        ) {
                                            Text(
                                                text = "${pair.first.totalListeningTime} ${stringResource(Res.string.seconds)}",
                                                style = typo().bodySmall,
                                            )
                                            Box(Modifier.fillMaxWidth()) {
                                                Box(
                                                    modifier =
                                                        Modifier
                                                            .wrapContentHeight()
                                                            .fillMaxWidth(pair.first.playCount.toFloat() / maxPlays)
                                                            .clip(CircleShape)
                                                            .background(MaterialTheme.colorScheme.onSurfaceVariant),
                                                ) {
                                                    Text(
                                                        text = "",
                                                        style = typo().bodySmall,
                                                        modifier =
                                                            Modifier
                                                                .align(Alignment.CenterStart)
                                                                .padding(horizontal = 4.dp),
                                                    )
                                                }
                                                Text(
                                                    text = "${pair.first.playCount} ${stringResource(Res.string.lower_plays)}",
                                                    style = typo().bodySmall,
                                                    color = MaterialTheme.colorScheme.surface,
                                                    modifier =
                                                        Modifier
                                                            .align(Alignment.CenterStart)
                                                            .padding(horizontal = 4.dp),
                                                )
                                            }
                                        }
                                    },
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }

            item {
                when (uiState.scrobblesLineChart) {
                    is LocalResource.Success if (!uiState.scrobblesLineChart.data.isNullOrEmpty()) -> {
                        val data = uiState.scrobblesLineChart.data ?: return@item
                        val maxPlays = data.maxOf { it.second }
                        Column(
                            modifier =
                                Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 12.dp),
                        ) {
                            Text(
                                text = stringResource(Res.string.date_range),
                                style = typo().labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Row(
                                modifier = Modifier.padding(top = 12.dp),
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    data.map { it.first }.forEach { chartType ->
                                        Box(
                                            modifier = Modifier.height(32.dp),
                                            contentAlignment = Alignment.CenterStart,
                                        ) {
                                            when (chartType) {
                                                is AnalyticsUiState.ChartType.Day -> {
                                                    Text(
                                                        text =
                                                            chartType.day.format(
                                                                LocalDate.Format {
                                                                    day()
                                                                    char(' ')
                                                                    monthName(MonthNames.ENGLISH_ABBREVIATED)
                                                                    char(' ')
                                                                    year()
                                                                },
                                                            ),
                                                        style = typo().bodyMedium,
                                                        modifier = Modifier.padding(horizontal = 8.dp),
                                                    )
                                                }

                                                is AnalyticsUiState.ChartType.Month -> {
                                                    Text(
                                                        text = "${chartType.month} - ${chartType.year}",
                                                        style = typo().bodySmall,
                                                        modifier = Modifier.padding(horizontal = 8.dp),
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    data.map { it.second }.forEach { playCount ->
                                        Box(
                                            modifier = Modifier.height(32.dp),
                                        ) {
                                            Box(Modifier.fillMaxWidth()) {
                                                Box(
                                                    modifier =
                                                        Modifier
                                                            .fillMaxHeight()
                                                            .fillMaxWidth(playCount.toFloat() / maxPlays)
                                                            .padding(vertical = 4.dp)
                                                            .clip(CircleShape)
                                                            .background(MaterialTheme.colorScheme.onSurfaceVariant),
                                                ) {
                                                    Text(
                                                        text = "",
                                                        style = typo().bodySmall,
                                                        modifier =
                                                            Modifier
                                                                .align(Alignment.CenterStart)
                                                                .padding(horizontal = 4.dp),
                                                    )
                                                }
                                                Text(
                                                    text = "$playCount ${stringResource(Res.string.lower_plays)}",
                                                    style = typo().bodySmall,
                                                    color = MaterialTheme.colorScheme.surface,
                                                    modifier =
                                                        Modifier
                                                            .align(Alignment.CenterStart)
                                                            .padding(horizontal = 8.dp),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }

            item {
                EndOfPage()
            }
        }

        // Top App Bar with haze effect
        TopAppBar(
            modifier =
                Modifier
                    .align(Alignment.TopCenter),
            title = {},
            navigationIcon = {
                Box(
                    modifier =
                        Modifier
                            .clip(CircleShape)
                            .wrapContentSize()
                            .align(Alignment.TopStart)
                            .padding(
                                12.dp,
                            ),
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        colors =
                            IconButtonDefaults.iconButtonColors().copy(
                                containerColor =
                                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(
                                        alpha = 0.8f,
                                    ),
                                contentColor =
                                    MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.6f,
                                    ),
                            ),
                    ) {
                        Icon(SonoraIcons.ArrowBackIosNew, "Back")
                    }
                }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
        )
    }
}

/**
 * One headline number with its label underneath.
 *
 * The value is the largest thing in the card and the label is quiet beneath it. The previous layout
 * had this the wrong way round: the label was underlined body text and the number was rendered
 * smaller than it, so the eye landed on the word "Artists" rather than on how many there were.
 * Underline also reads as a link, which these never were.
 */
@Composable
private fun AnalyticsStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                // Stretches to the row's intrinsic height so all three cards match.
                .fillMaxHeight()
                .clip(SonoraShapes.Container)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = value,
            style = typo().titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
        )
        Text(
            text = label,
            style = typo().bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
        )
    }
}

/**
 * The four ranges as chips, with the current one filled.
 *
 * A row of four is small enough to show every option at once, which means the selected range is
 * readable without tapping anything and switching costs one tap instead of two.
 */
@Composable
private fun DayRangeChips(
    selected: AnalyticsUiState.DayRange,
    onSelect: (AnalyticsUiState.DayRange) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ranges =
        listOf(
            AnalyticsUiState.DayRange.LAST_7_DAYS to stringResource(Res.string.last_7_days),
            AnalyticsUiState.DayRange.LAST_30_DAYS to stringResource(Res.string.last_30_days),
            AnalyticsUiState.DayRange.LAST_90_DAYS to stringResource(Res.string.last_90_days),
            AnalyticsUiState.DayRange.THIS_YEAR to stringResource(Res.string.this_year),
        )
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ranges.forEach { (range, label) ->
            val isSelected = range == selected
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(SonoraShapes.Capsule)
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceContainer
                            },
                        ).clickable { onSelect(range) }
                        .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = typo().bodySmall,
                    // Black on the brand blue, matching the nav pill, rather than onPrimary which
                    // flips light in dark mode and loses contrast against the accent.
                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
    }
}

/**
 * Listening time as something a person would say out loud.
 *
 * The screen used to print the raw second count with the word "seconds" after it, so a normal
 * month of listening rendered as a six digit number that nobody can read at a glance. The existing
 * formatDuration is mm:ss, which is right for one track and wrong for a total that reaches hours.
 */
private fun humanDuration(totalSeconds: Long): String {
    if (totalSeconds <= 0L) return "0m"
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
        hours > 0 -> "${hours}h"
        minutes > 0 -> "${minutes}m"
        else -> "${totalSeconds}s"
    }
}
