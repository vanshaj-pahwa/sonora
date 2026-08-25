package app.vanshajpahwa.sonora.ui.component

import androidx.compose.animation.AnimatedVisibility
import app.vanshajpahwa.sonora.extension.toShortViewCount
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import app.vanshajpahwa.common.Config
import app.vanshajpahwa.domain.data.entities.AlbumEntity
import app.vanshajpahwa.domain.data.entities.LocalPlaylistEntity
import app.vanshajpahwa.domain.data.entities.PlaylistEntity
import app.vanshajpahwa.domain.data.entities.PodcastsEntity
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.model.browse.artist.ResultAlbum
import app.vanshajpahwa.domain.data.model.browse.artist.ResultPlaylist
import app.vanshajpahwa.domain.data.model.browse.artist.ResultSingle
import app.vanshajpahwa.domain.data.model.home.Content
import app.vanshajpahwa.domain.data.model.home.HomeItem
import app.vanshajpahwa.domain.data.model.home.chart.ItemArtist
import app.vanshajpahwa.domain.data.model.home.chart.ItemVideo
import app.vanshajpahwa.domain.data.model.mood.genre.ItemsPlaylist
import app.vanshajpahwa.domain.data.model.mood.moodmoments.Item
import app.vanshajpahwa.domain.data.model.searchResult.albums.AlbumsResult
import app.vanshajpahwa.domain.data.model.searchResult.playlists.PlaylistsResult
import app.vanshajpahwa.domain.data.model.searchResult.songs.Artist
import app.vanshajpahwa.domain.data.type.ChartItem
import app.vanshajpahwa.domain.data.type.HomeContentType
import app.vanshajpahwa.domain.mediaservice.handler.PlaylistType
import app.vanshajpahwa.domain.mediaservice.handler.QueueData
import app.vanshajpahwa.domain.utils.connectArtists
import app.vanshajpahwa.domain.utils.toListName
import app.vanshajpahwa.domain.utils.toSongEntity
import app.vanshajpahwa.domain.utils.toTrack
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.sonora.Platform
import app.vanshajpahwa.sonora.expect.ui.HorizontalScrollBar
import app.vanshajpahwa.sonora.getPlatform
import app.vanshajpahwa.sonora.ui.navigation.destination.list.AlbumDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.ArtistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.PlaylistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.PodcastDestination
import app.vanshajpahwa.sonora.ui.theme.LocalForceDarkText
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.HomeViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.album
import app.vanshajpahwa.sonora.resources.app_name
import app.vanshajpahwa.sonora.resources.description
import app.vanshajpahwa.sonora.resources.playlist
import app.vanshajpahwa.sonora.resources.subscribers
import app.vanshajpahwa.sonora.resources.you

@Composable
fun HomeItem(
    homeViewModel: HomeViewModel = koinViewModel(),
    navController: NavController,
    data: HomeItem,
) {
    var bottomSheetShow by remember { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()
    val snapperFlingBehavior = rememberSnapFlingBehavior(SnapLayoutInfoProvider(lazyListState = lazyListState))

    var track by remember { mutableStateOf<Track?>(null) }

    if (bottomSheetShow) {
        NowPlayingBottomSheet(
            onDismiss = { bottomSheetShow = false },
            song = track?.toSongEntity(),
            navController = navController,
        )
    }

    // A section with no items is just a heading over nothing. The caller cannot easily tell in
    // advance, because YouTube returns the section with an empty content list rather than omitting
    // it: that is exactly how "New release" came back as a bare title with no cards under it. The
    // guard lives here so it covers every section, not only that one.
    if (data.contents.isEmpty()) return

    val channelId = data.channelId
    Column {
        Row(
            modifier =
                if (channelId != null) {
                    Modifier
                        .focusable(true)
                        .clickable {
                            navController.navigate(
                                ArtistDestination(
                                    channelId = channelId,
                                ),
                            )
                        }
                } else {
                    Modifier
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(
                visible = (data.thumbnail?.lastOrNull() != null),
                modifier = Modifier.align(Alignment.CenterVertically),
            ) {
                AsyncImage(
                    model =
                        ImageRequest
                            .Builder(LocalPlatformContext.current)
                            .data(data.thumbnail?.lastOrNull()?.url)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .diskCacheKey(data.thumbnail?.lastOrNull()?.url)
                            .crossfade(550)
                            .build(),
                    contentDescription = "",
                    placeholder = rememberHolderPainter(),
                    error = rememberHolderPainter(),
                    modifier =
                        Modifier
                            .size(36.dp)
                            .clip(
                                CircleShape,
                            ),
                )
            }
            Column(
                Modifier
                    .padding(start = 10.dp),
            ) {
                AnimatedVisibility(visible = (data.subtitle != null && data.subtitle != "")) {
                    Text(
                        text = data.subtitle ?: "",
                        style = typo().bodySmall,
                    )
                }
                Text(
                    text = data.title,
                    style = typo().headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        // The header and the row it belongs to had nothing between them. The gap used to arrive by
        // accident, from the 10.dp padding on each card, and shrinking that padding to kill the dead
        // band under the cards took the breathing room above them with it. An explicit spacer means
        // the title keeps its distance whatever the cards do, and every feed section gets it.
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            state = lazyListState,
            flingBehavior = snapperFlingBehavior,
        ) {
            items(data.contents) { temp ->
                if (temp != null) {
                    val browseId = temp.browseId
                    val playlistId = temp.playlistId
                    if ((playlistId != null && temp.videoId == null) || (playlistId != null && temp.videoId == "")) {
                        if (playlistId.startsWith("UC")) {
                            HomeItemArtist(onClick = {
                                navController.navigate(
                                    ArtistDestination(
                                        channelId = playlistId,
                                    ),
                                )
                            }, data = temp)
                        } else {
                            HomeItemContentPlaylist(onClick = {
                                navController.navigate(
                                    PlaylistDestination(
                                        playlistId = playlistId,
                                    ),
                                )
                            }, data = temp)
                        }
                    } else if ((browseId != null && temp.videoId == null) || (browseId != null && temp.videoId == "")) {
                        if (browseId.startsWith("UC")) {
                            HomeItemArtist(onClick = {
                                navController.navigate(
                                    ArtistDestination(
                                        channelId = browseId,
                                    ),
                                )
                            }, data = temp)
                        } else if (browseId.startsWith("MPSP")) {
                            HomeItemContentPlaylist(onClick = {
                                navController.navigate(
                                    PodcastDestination(
                                        podcastId = browseId,
                                    ),
                                )
                            }, data = temp)
                        } else {
                            HomeItemContentPlaylist(onClick = {
                                navController.navigate(
                                    AlbumDestination(
                                        browseId = browseId,
                                    ),
                                )
                            }, data = temp)
                        }
                    } else if (temp.thumbnails.firstOrNull()?.width != temp.thumbnails.firstOrNull()?.height) {
                        HomeItemVideo(
                            onClick = {
                                val firstQueue: Track = temp.toTrack()
                                homeViewModel.setQueueData(
                                    QueueData.Data(
                                        listTracks = arrayListOf(firstQueue),
                                        firstPlayedTrack = firstQueue,
                                        playlistId = "RDAMVM${temp.videoId}",
                                        playlistName = temp.title,
                                        playlistType = PlaylistType.RADIO,
                                        continuation = null,
                                    ),
                                )
                                homeViewModel.loadMediaItem(
                                    firstQueue,
                                    Config.SONG_CLICK,
                                )
                            },
                            onLongClick = {
                                track = temp.toTrack()
                                bottomSheetShow = true
                            },
                            data = temp,
                        )
                    } else {
                        HomeItemSong(
                            onClick = {
                                val firstQueue: Track = temp.toTrack()
                                homeViewModel.setQueueData(
                                    QueueData.Data(
                                        listTracks = arrayListOf(firstQueue),
                                        firstPlayedTrack = firstQueue,
                                        playlistId = "RDAMVM${temp.videoId}",
                                        playlistName = temp.title,
                                        playlistType = PlaylistType.RADIO,
                                        continuation = null,
                                    ),
                                )
                                homeViewModel.loadMediaItem(
                                    firstQueue,
                                    Config.SONG_CLICK,
                                )
                            },
                            onLongClick = {
                                track = temp.toTrack()
                                bottomSheetShow = true
                            },
                            data = temp,
                        )
                    }
                }
            }
        }
        if (getPlatform() == Platform.Desktop) {
            HorizontalScrollBar(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                scrollState = lazyListState,
            )
        }
    }
}

@Composable
fun HomeItemContentPlaylist(
    onClick: () -> Unit,
    data: HomeContentType,
    thumbSize: Dp = 160.dp,
    forceDark: Boolean = LocalForceDarkText.current,
) {
    val titleColor = if (forceDark) Color.White else MaterialTheme.colorScheme.onSurface
    Box(
        Modifier
            .wrapContentSize()
            .focusable(true)
            .clickable {
                onClick()
            },
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    // Reserves only what the tallest realistic card needs: 144dp of artwork
                    // (thumbSize * 0.9), 8dp above the title, a two line title and one subtitle
                    // line. The old value reserved thumbSize + 76, which is 30dp more than a card
                    // with a single line title actually occupies, and that slack showed up as a
                    // dead band under every shelf in the feed.
                    .heightIn(min = thumbSize + 52.dp),
        ) {
            val thumb =
                when (data) {
                    is Content -> data.thumbnails.lastOrNull()?.url
                    is app.vanshajpahwa.domain.data.model.mood.genre.Content -> data.thumbnail?.lastOrNull()?.url
                    is app.vanshajpahwa.domain.data.model.mood.moodmoments.Content -> data.thumbnails?.lastOrNull()?.url
                    is LocalPlaylistEntity -> data.thumbnail
                    is ChartItem -> null
                    is PlaylistsResult -> data.thumbnails.lastOrNull()?.url
                    is AlbumEntity -> data.thumbnails
                    is PlaylistEntity -> data.thumbnails
                    is ResultSingle -> data.thumbnails.lastOrNull()?.url
                    is ResultAlbum -> data.thumbnails.lastOrNull()?.url
                    is ResultPlaylist -> data.thumbnails.lastOrNull()?.url
                    is PodcastsEntity -> data.thumbnail
                    is AlbumsResult -> data.thumbnails.lastOrNull()?.url
                    else -> null
                }
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalPlatformContext.current)
                        .data(thumb)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .diskCacheKey(thumb)
                        .crossfade(550)
                        .build(),
                placeholder =
                    when (data) {
                        is LocalPlaylistEntity -> {
                            painterPlaylistThumbnail(
                                data.title,
                                style = typo().bodySmall,
                                thumbSize * 0.9f to thumbSize * 0.9f,
                            )
                        }

                        is ChartItem -> {
                            painterPlaylistThumbnail(
                                data.name,
                                style = typo().bodySmall,
                                thumbSize * 0.9f to thumbSize * 0.9f,
                            )
                        }

                        else -> {
                            rememberHolderPainter()
                        }
                    },
                error =
                    when (data) {
                        is LocalPlaylistEntity -> {
                            painterPlaylistThumbnail(
                                data.title,
                                style = typo().bodySmall,
                                thumbSize * 0.9f to thumbSize * 0.9f,
                            )
                        }

                        is ChartItem -> {
                            painterPlaylistThumbnail(
                                data.name,
                                style = typo().bodySmall,
                                thumbSize * 0.9f to thumbSize * 0.9f,
                            )
                        }

                        else -> {
                            rememberHolderPainter()
                        }
                    },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .size(thumbSize)
                        .aspectRatio(1f)
                        .clip(
                            SonoraShapes.Container,
                        ),
            )
            Text(
                text =
                    when (data) {
                        is Content -> data.title
                        is app.vanshajpahwa.domain.data.model.mood.genre.Content -> data.title.title
                        is app.vanshajpahwa.domain.data.model.mood.moodmoments.Content -> data.title
                        is LocalPlaylistEntity -> data.title
                        is ChartItem -> data.name
                        is PlaylistsResult -> data.title
                        is AlbumEntity -> data.title
                        is PlaylistEntity -> data.title
                        is ResultSingle -> data.title
                        is ResultAlbum -> data.title
                        is ResultPlaylist -> data.title
                        is PodcastsEntity -> data.title
                        is AlbumsResult -> data.title
                        else -> ""
                    },
                style = typo().titleSmall,
                color = titleColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .width(thumbSize)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .padding(top = 8.dp),
            )
            Text(
                text =
                    when (data) {
                        is Content -> {
                            data.description
                                ?: if (data.playlistId == null) {
                                    (
                                        if (!data.artists.isNullOrEmpty()) {
                                            data.artists
                                                .toListName()
                                                .connectArtists()
                                        } else {
                                            stringResource(Res.string.album)
                                        }
                                    )
                                } else {
                                    stringResource(Res.string.playlist)
                                }
                        }

                        is app.vanshajpahwa.domain.data.model.mood.genre.Content -> {
                            data.title.subtitle
                        }

                        is app.vanshajpahwa.domain.data.model.mood.moodmoments.Content -> {
                            data.subtitle
                        }

                        is LocalPlaylistEntity -> {
                            stringResource(Res.string.you)
                        }

                        is ChartItem -> {
                            stringResource(Res.string.app_name)
                        }

                        is PlaylistsResult -> {
                            data.author
                        }

                        is AlbumEntity -> {
                            data.artistName?.connectArtists() ?: stringResource(Res.string.album)
                        }

                        is PlaylistEntity -> {
                            data.author ?: stringResource(Res.string.playlist)
                        }

                        is ResultSingle -> {
                            data.year
                        }

                        is ResultAlbum -> {
                            data.year
                        }

                        is ResultPlaylist -> {
                            data.author
                        }

                        is PodcastsEntity -> {
                            data.authorName
                        }

                        is AlbumsResult -> {
                            data.year
                        }

                        else -> {
                            ""
                        }
                    },
                style = typo().bodySmall,
                minLines = 1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .width(thumbSize)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .titleMarquee(
                            initialDelayMillis = 2000,
                            repeatDelayMillis = 2000,
                            velocity = 25.dp,
                        ),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuickPicksItem(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    widthDp: Dp,
    data: Content,
) {
    val tag = "QuickPicksItem"
    Box(
        modifier =
            Modifier
                .wrapContentHeight()
                .width(widthDp - 30.dp)
                .focusable(true)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
        ) {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalPlatformContext.current)
                        .data(data.thumbnails.lastOrNull()?.url)
                        .crossfade(550)
                        .diskCacheKey(data.thumbnails.lastOrNull()?.url)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                placeholder = rememberHolderPainter(),
                contentDescription = stringResource(Res.string.description),
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                        .size(44.dp)
                        .clip(
                            SonoraShapes.Media,
                        ),
            )
            Column(
                Modifier
                    .padding(
                        start = 16.dp,
                    ).align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                // One line + marquee, NOT maxLines = 2: the parent LazyHorizontalGrid uses
                // GridCells.Fixed(4) over a fixed 256.dp, so every cell is exactly 64.dp and a
                // second title line pushes the artist row out of the cell, where the grid clips it.
                Text(
                    text = data.title,
                    style = typo().titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .padding(
                                bottom = 3.dp,
                            ).titleMarquee(
                                iterations = Int.MAX_VALUE,
                                initialDelayMillis = 2000,
                                repeatDelayMillis = 2000,
                                velocity = 25.dp,
                            ),
                )
                LazyRow(verticalAlignment = Alignment.CenterVertically) {
                    item {
                        androidx.compose.animation.AnimatedVisibility(visible = data.isExplicit == true) {
                            ExplicitBadge(
                                modifier =
                                    Modifier
                                        .size(20.dp)
                                        .padding(end = 4.dp)
                                        .weight(1f),
                            )
                        }
                    }
                    item {
                        Text(
                            text = data.artists.toListName().connectArtists(),
                            style = typo().bodySmall,
                            minLines = 1,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically)
                                    .titleMarquee(
                                        initialDelayMillis = 2000,
                                        repeatDelayMillis = 2000,
                                        velocity = 25.dp,
                                    ),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeItemSong(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    data: Content,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .focusable(true)
                .clickable {
                    onClick()
                }.combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    // Same over reservation as the playlist card above, same reasoning.
                    .heightIn(min = 212.dp),
        ) {
            val thumb =
                data.thumbnails.lastOrNull()?.url?.let {
                    if (it.contains("w120")) {
                        Regex("([wh])120").replace(it, "$1544")
                    } else {
                        it
                    }
                }
            Logger.w("AsyncImage", "HomeItemSong: $thumb")
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalPlatformContext.current)
                        .data(thumb)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .diskCacheKey(thumb)
                        .crossfade(550)
                        .build(),
                placeholder = rememberHolderPainter(),
                error = rememberHolderPainter(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(160.dp)
                        .clip(
                            SonoraShapes.Container,
                        ),
            )
            Text(
                text = data.title,
                style = typo().titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .width(160.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .padding(top = 8.dp),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedVisibility(visible = data.isExplicit == true) {
                    ExplicitBadge(
                        modifier =
                            Modifier
                                .size(20.dp)
                                .padding(end = 4.dp)
                                .weight(1f),
                    )
                }
                Text(
                    text =
                        listOfNotNull(
                            data.artists.toListName().connectArtists().takeIf { it.isNotBlank() },
                            data.album?.name?.takeIf { it.isNotBlank() },
                        ).joinToString(" • "),
                    style = typo().bodySmall,
                    minLines = 1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier
                            .width(160.dp)
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .titleMarquee(
                                initialDelayMillis = 2000,
                                repeatDelayMillis = 2000,
                                velocity = 25.dp,
                            )
                            .padding(vertical = 3.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeItemVideo(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    data: Content,
    forceDark: Boolean = LocalForceDarkText.current,
) {
    val titleColor = if (forceDark) Color.White else MaterialTheme.colorScheme.onSurface
    Box(
        Modifier
            .fillMaxSize()
            .focusable(true)
            .clickable {
                onClick()
            }.combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    // Same over reservation as the playlist card above, same reasoning.
                    .heightIn(min = 212.dp),
        ) {
            val thumb = data.thumbnails.lastOrNull()?.url
            Logger.w("AsyncImage", "HomeItemSong: $thumb")
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
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(160.dp)
                        .aspectRatio(16f / 9f)
                        .clip(
                            SonoraShapes.Container,
                        ),
            )
            Text(
                text = data.title,
                style = typo().titleSmall,
                color = titleColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .width(284.5.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .padding(top = 8.dp),
            )
            Text(
                text =
                    listOfNotNull(
                        data.artists.toListName().connectArtists().takeIf { it.isNotBlank() },
                        data.views?.takeIf { it.isNotBlank() }?.toShortViewCount(),
                    ).joinToString(" • "),
                style = typo().bodySmall,
                minLines = 1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .width(284.5.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .titleMarquee(
                            initialDelayMillis = 2000,
                            repeatDelayMillis = 2000,
                            velocity = 25.dp,
                        )
                        .padding(vertical = 2.dp),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeItemArtist(
    onClick: () -> Unit,
    data: Content,
    forceDark: Boolean = LocalForceDarkText.current,
) {
    val titleColor = if (forceDark) Color.White else MaterialTheme.colorScheme.onSurface
    Box(
        Modifier
            .fillMaxSize()
            .focusable(true)
            .clickable {
                onClick()
            },
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    // Same over reservation as the playlist card above, same reasoning.
                    .heightIn(min = 212.dp),
        ) {
            val thumb = data.thumbnails.lastOrNull()?.url
            Logger.w("AsyncImage", "HomeItemSong: $thumb")
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalPlatformContext.current)
                        .data(thumb)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .diskCacheKey(thumb)
                        .crossfade(550)
                        .build(),
                placeholder = rememberHolderPainter(),
                error = rememberHolderPainter(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(160.dp)
                        .clip(
                            CircleShape,
                        ),
            )
            Text(
                text = data.title,
                style = typo().titleSmall,
                color = titleColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .width(160.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .padding(top = 8.dp),
            )
            Text(
                text = data.description?.takeIf { it.isNotBlank() }.orEmpty(),
                style = typo().bodySmall,
                minLines = 1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .width(160.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .titleMarquee(
                            initialDelayMillis = 2000,
                            repeatDelayMillis = 2000,
                            velocity = 25.dp,
                        ),
            )
        }
    }
}


@Composable
fun ItemVideoChart(
    onClick: () -> Unit,
    data: ItemVideo,
    position: Int,
) {
    Box(
        Modifier
            .wrapContentSize()
            .focusable(true)
            .clickable {
                onClick()
            },
    ) {
        Column(
            modifier =
                Modifier
                    .padding(10.dp),
        ) {
            val thumb = data.thumbnails.lastOrNull()?.url
            Logger.w("AsyncImage", "HomeItemSong: $thumb")
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
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(280.dp)
                        .height(160.dp)
                        .clip(
                            SonoraShapes.Media,
                        ),
            )
            Row {
                Text(
                    text = position.toString(),
                    style = typo().titleLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier =
                        Modifier
                            .width(40.dp)
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .align(Alignment.CenterVertically),
                )
                Column(Modifier.padding(start = 10.dp)) {
                    Text(
                        text = data.title,
                        style = typo().titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier =
                            Modifier
                                .width(210.dp)
                                .wrapContentHeight(align = Alignment.CenterVertically)
                                .padding(top = 10.dp),
                    )
                    Text(
                        text =
                            listOfNotNull(
                                data.artists.toListName().connectArtists().takeIf { it.isNotBlank() },
                                data.views.takeIf { it.isNotBlank() }?.toShortViewCount(),
                            ).joinToString(" • "),
                        style = typo().bodyMedium,
                        minLines = 1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier =
                            Modifier
                                .width(210.dp)
                                .wrapContentHeight(align = Alignment.CenterVertically)
                                .titleMarquee(
                                    initialDelayMillis = 2000,
                                    repeatDelayMillis = 2000,
                                    velocity = 25.dp,
                                )
                                .padding(vertical = 3.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun ItemArtistChart(
    onClick: () -> Unit,
    data: ItemArtist,
    widthDp: Dp,
) {
    Box(
        Modifier
            .wrapContentSize()
            .focusable(true)
            .clickable {
                onClick()
            },
    ) {
        Row(
            modifier =
                Modifier
                    .padding(10.dp)
                    .width(widthDp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = data.rank,
                style = typo().titleLarge,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier =
                    Modifier
                        .wrapContentSize(Alignment.Center)
                        .align(Alignment.CenterVertically)
                        .padding(end = 20.dp),
            )
            val thumb = data.thumbnails.lastOrNull()?.url
            Logger.w("AsyncImage", "HomeItemSong: $thumb")
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalPlatformContext.current)
                        .data(thumb)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .diskCacheKey(thumb)
                        .crossfade(550)
                        .build(),
                placeholder = rememberHolderPainter(),
                error = rememberHolderPainter(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                        .size(60.dp)
                        .clip(
                            CircleShape,
                        ),
            )
            Column(
                Modifier
                    .padding(start = 15.dp)
                    .width(160.dp)
                    .align(Alignment.CenterVertically),
            ) {
                Text(
                    text = data.title,
                    style = typo().titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier
                            .wrapContentHeight(align = Alignment.CenterVertically),
                )
                Text(
                    text =
                        if (data.subscribers.contains(
                                stringResource(Res.string.subscribers).replace("%1\$s ", ""),
                            )
                        ) {
                            data.subscribers
                        } else {
                            stringResource(
                                Res.string.subscribers,
                                data.subscribers,
                            )
                        },
                    style = typo().bodySmall,
                    minLines = 1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .titleMarquee(
                                initialDelayMillis = 2000,
                                repeatDelayMillis = 2000,
                                velocity = 25.dp,
                            ),
                )
            }
        }
    }
}

@Composable
fun ItemTrackChart(
    onClick: () -> Unit,
    data: Track,
    position: Int?,
    widthDp: Dp,
) {
    Box(
        modifier =
            Modifier
                .wrapContentSize()
                .focusable(true)
                .clickable {
                    onClick()
                },
    ) {
        Row(
            modifier =
                Modifier
                    .width(widthDp)
                    .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Crossfade(targetState = position != null, label = "Chart Track Position") {
                if (it) {
                    Row {
                        Text(
                            text = position.toString(),
                            style = typo().titleLarge,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            modifier =
                                Modifier
                                    .width(40.dp)
                                    .wrapContentHeight(align = Alignment.CenterVertically)
                                    .align(Alignment.CenterVertically),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            }
            val thumb = data.thumbnails?.lastOrNull()?.url
            Logger.w("AsyncImage", "HomeItemSong: $thumb")
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalPlatformContext.current)
                        .data(thumb)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .diskCacheKey(thumb)
                        .crossfade(550)
                        .build(),
                placeholder = rememberHolderPainter(),
                error = rememberHolderPainter(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                        .size(50.dp)
                        .clip(
                            SonoraShapes.Media,
                        ),
            )
            Column(
                Modifier
                    .padding(
                        start = 20.dp,
                    ).align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    text = data.title,
                    style = typo().titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .padding(
                                bottom = 3.dp,
                            ),
                )

                Text(
                    text = data.artists.toListName().connectArtists(),
                    style = typo().bodySmall,
                    minLines = 1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .titleMarquee(
                                initialDelayMillis = 2000,
                                repeatDelayMillis = 2000,
                                velocity = 25.dp,
                            ),
                )
            }
        }
    }
}

@Composable
fun MoodAndGenresContentItem(
    data: Any?,
    navController: NavController,
    homeViewModel: HomeViewModel = koinViewModel(),
) {
    Column(
        modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically, unbounded = true),
    ) {
        Text(
            text =
                when (data) {
                    is ItemsPlaylist -> (data).header
                    is Item -> (data).header
                    else -> ""
                },
            style = typo().titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier =
                Modifier
                    .padding(top = 8.dp)
                    .padding(
                        horizontal = 16.dp,
                    ).fillMaxWidth(),
        )
        LazyRow(
            modifier =
                Modifier.padding(
                    10.dp,
                ),
        ) {
            val itemList =
                when (data) {
                    is ItemsPlaylist -> (data).contents
                    is Item -> (data).contents
                    else -> listOf()
                }
            items(itemList) { item ->
                HomeItemContentPlaylist(onClick = {
                    // The "Songs" shelf mixes tracks into a list that is otherwise all playlists,
                    // so route by videoId: a track starts its radio, everything else opens a page.
                    val moodSong = item as? app.vanshajpahwa.domain.data.model.mood.moodmoments.Content
                    val songVideoId = moodSong?.videoId
                    if (moodSong != null && songVideoId != null) {
                        val track =
                            Track(
                                album = null,
                                artists = listOf(Artist(id = null, name = moodSong.subtitle)),
                                duration = null,
                                durationSeconds = null,
                                isAvailable = true,
                                isExplicit = false,
                                likeStatus = null,
                                thumbnails = moodSong.thumbnails,
                                title = moodSong.title,
                                videoId = songVideoId,
                                videoType = null,
                                category = null,
                                feedbackTokens = null,
                                resultType = null,
                            )
                        homeViewModel.setQueueData(
                            QueueData.Data(
                                listTracks = arrayListOf(track),
                                firstPlayedTrack = track,
                                playlistId = "RDAMVM$songVideoId",
                                playlistName = "\"${moodSong.title}\" Radio",
                                playlistType = PlaylistType.RADIO,
                                continuation = null,
                            ),
                        )
                        homeViewModel.loadMediaItem(track, type = Config.SONG_CLICK)
                    } else {
                        // Route by the id's prefix, not by the field it arrived in.
                        //
                        // playlistBrowseId is simply whatever browseEndpoint.browseId YouTube
                        // returned, so a genre page's album shelf puts album ids in it. Sending
                        // those to the playlist screen meant a spinner, a failed load and a bounce
                        // straight back to the genre, which is what tapping any album under a
                        // genre used to do.
                        //
                        // The response already says what each card is, in
                        // browseEndpointContextMusicConfig.pageType, so that is what decides. The
                        // parser used to discard it, which left prefix sniffing as the only option
                        // and no way to be sure an album id had been spotted.
                        val genreItem = item as? app.vanshajpahwa.domain.data.model.mood.genre.Content
                        val momentItem = item as? app.vanshajpahwa.domain.data.model.mood.moodmoments.Content
                        val browseId = genreItem?.playlistBrowseId ?: momentItem?.playlistBrowseId.orEmpty()
                        val pageType = genreItem?.pageType ?: momentItem?.pageType
                        navController.navigate(
                            when {
                                // What YouTube said it is, when it said anything.
                                pageType == "MUSIC_PAGE_TYPE_ALBUM" ||
                                    pageType == "MUSIC_PAGE_TYPE_AUDIOBOOK" -> AlbumDestination(browseId = browseId)
                                pageType == "MUSIC_PAGE_TYPE_PLAYLIST" -> PlaylistDestination(playlistId = browseId)
                                pageType == "MUSIC_PAGE_TYPE_ARTIST" ||
                                    pageType == "MUSIC_PAGE_TYPE_USER_CHANNEL" -> ArtistDestination(channelId = browseId)
                                pageType == "MUSIC_PAGE_TYPE_PODCAST" -> PodcastDestination(podcastId = browseId)
                                // Only reached when the response omitted the type. Prefixes are a
                                // guess, which is why they are the fallback and not the rule.
                                browseId.startsWith("VL") -> PlaylistDestination(playlistId = browseId)
                                browseId.startsWith("UC") -> ArtistDestination(channelId = browseId)
                                browseId.startsWith("MPSP") -> PodcastDestination(podcastId = browseId)
                                else -> AlbumDestination(browseId = browseId)
                            },
                        )
                    }
                }, data = item)
            }
        }
    }
}