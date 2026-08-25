package app.vanshajpahwa.sonora.ui.component

import androidx.compose.animation.AnimatedVisibility
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import app.vanshajpahwa.domain.data.entities.AlbumEntity
import app.vanshajpahwa.domain.data.entities.ArtistEntity
import app.vanshajpahwa.domain.data.entities.DownloadState
import app.vanshajpahwa.domain.data.entities.LocalPlaylistEntity
import app.vanshajpahwa.domain.data.entities.PlaylistEntity
import app.vanshajpahwa.domain.data.entities.PodcastsEntity
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.model.searchResult.albums.AlbumsResult
import app.vanshajpahwa.domain.data.model.searchResult.artists.ArtistsResult
import app.vanshajpahwa.domain.data.model.searchResult.playlists.PlaylistsResult
import app.vanshajpahwa.domain.data.type.ArtistType
import app.vanshajpahwa.domain.data.type.PlaylistType
import app.vanshajpahwa.domain.repository.SongRepository
import app.vanshajpahwa.domain.utils.connectArtists
import app.vanshajpahwa.domain.utils.toListName
import app.vanshajpahwa.sonora.ui.icon.Add
import app.vanshajpahwa.sonora.ui.icon.DownloadForOffline
import app.vanshajpahwa.sonora.ui.icon.DragHandle
import app.vanshajpahwa.sonora.ui.icon.MoreVert
import app.vanshajpahwa.sonora.ui.icon.PushPin
import app.vanshajpahwa.sonora.ui.icon.QueueMusic
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.icon.Check
import app.vanshajpahwa.sonora.ui.theme.LocalForceDarkText
import app.vanshajpahwa.sonora.ui.theme.typo
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.add_to_queue
import app.vanshajpahwa.sonora.resources.album
import app.vanshajpahwa.sonora.resources.artists
import app.vanshajpahwa.sonora.resources.playlist
import app.vanshajpahwa.sonora.resources.podcasts
import app.vanshajpahwa.sonora.resources.radio
import app.vanshajpahwa.sonora.resources.you
import kotlin.math.roundToInt
import app.vanshajpahwa.sonora.viewModel.DurationBackfill
import androidx.compose.runtime.LaunchedEffect

/**
 * This is the song item in the playlist or other places.
 */
@Composable
fun SongFullWidthItems(
    track: Track? = null,
    index: Int? = null,
    songEntity: SongEntity? = null,
    isPlaying: Boolean,
    shouldShowDragHandle: Boolean = false,
    onMoreClickListener: ((videoId: String) -> Unit)? = null,
    onClickListener: ((videoId: String) -> Unit)? = null,
    onAddToQueue: ((videoId: String) -> Unit)? = null,
    modifier: Modifier,
    rightView: @Composable (() -> Unit)? = null,
    /**
     * Show the track's running time before [rightView].
     *
     * Lives here rather than being passed in as a rightView by each screen, because three screens
     * wanting the same thing is how the detail pages ended up with three copies of an action row
     * that then drifted apart.
     */
    showDuration: Boolean = false,
    forceDark: Boolean = LocalForceDarkText.current,
    // Multi select. Defaulted off so the four screens that have not adopted it yet compile and
    // behave exactly as before.
    selectionActive: Boolean = false,
    selected: Boolean = false,
    onLongClick: ((videoId: String) -> Unit)? = null,
) {
    val contentColor = if (forceDark) Color.White else MaterialTheme.colorScheme.onSurface
    val subtitleColor = if (forceDark) Color(0xC4FFFFFF) else MaterialTheme.colorScheme.onSurfaceVariant
    // The current track needs to be findable in a list of hundreds. The equalizer bars alone sit in
    // the artwork slot and are easy to miss while scrolling, so the title takes the brand accent and
    // the row takes a wash of it. 8% keeps the row legible on both themes and never fights the text.
    val titleColor = if (isPlaying) MaterialTheme.colorScheme.primary else contentColor
    // A selected row outranks the now playing wash. You can only be looking at one of these at a
    // time, and while picking tracks the thing you need to see is what you have picked. 16% rather
    // than the 8% used for now playing so the two are never mistaken for each other.
    val rowBackground =
        when {
            selected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
            isPlaying -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            else -> Color.Transparent
        }
    val rowVideoId = track?.videoId ?: songEntity?.videoId ?: ""
    val maxOffset = 360f
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val songRepository: SongRepository = koinInject<SongRepository>()
    // Remembered against the row's id, which is the whole point.
    //
    // Unwrapped, this expression built a new Room query flow on every recomposition, and
    // collectAsState responded by cancelling the previous collection and starting a fresh database
    // query. Rows recompose constantly while a list is moving, so scrolling was issuing a stream of
    // new queries per visible row and throwing away the results of the ones it had just started.
    // That is the single largest cost in the shared row, and this row is on every list in the app.
    //
    // distinctUntilChanged because download state changes far more rarely than the row recomposes,
    // and an unchanged value should not cost a frame.
    val downloadState by remember(rowVideoId) {
        songRepository
            .getSongAsFlow(rowVideoId)
            .mapNotNull { it?.downloadState }
            .distinctUntilChanged()
    }.collectAsState(initial = DownloadState.STATE_NOT_DOWNLOADED)
    val offsetX = remember { Animatable(initialValue = 0f) }
    var heightDp by remember { mutableStateOf(0.dp) }

    Box(
        modifier =
        modifier,
    ) {
        Crossfade(
            offsetX.value >= maxOffset / 2,
        ) { shouldShowAddToQueue ->
            if (shouldShowAddToQueue) {
                Box(
                    modifier =
                        Modifier
                            .height(heightDp)
                            .aspectRatio(1f)
                            .padding(start = 15.dp)
                            .align(Alignment.CenterStart),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        tint = contentColor,
                        imageVector = SonoraIcons.QueueMusic,
                        contentDescription = stringResource(Res.string.add_to_queue),
                    )
                }
            }
        }
        Box(
            modifier =
                modifier
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    // Inside the offset so the wash travels with the row on a swipe-to-queue drag,
                    // rather than staying behind and exposing a seam.
                    .background(rowBackground)
                    .combinedClickable(
                        onClick = {
                            // Once any row is selected a tap means "add this one too", not "play".
                            // Playing a track out from under a half finished selection is the kind
                            // of thing that makes people stop trusting the mode.
                            if (selectionActive && onLongClick != null) {
                                onLongClick(rowVideoId)
                            } else {
                                onClickListener?.invoke(rowVideoId)
                            }
                        },
                        onLongClick =
                            if (onLongClick != null) {
                                { onLongClick(rowVideoId) }
                            } else {
                                null
                            },
                    ).animateContentSize()
                    .pointerInput(selectionActive) {
                        // Swipe to queue stands down during selection. A horizontal drag and a drag
                        // to extend a selection are the same gesture to a thumb, and the row is
                        // already carrying a long press.
                        if (!isPlaying && onAddToQueue != null && !selectionActive) {
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { change, dragAmount ->
                                    if (offsetX.value + dragAmount > 0) {
                                        change.consume()
                                        coroutineScope.launch {
                                            offsetX.snapTo(
                                                (offsetX.value + dragAmount).coerceAtMost(maxOffset),
                                            )
                                        }
                                    }
                                },
                                onDragEnd = {
                                    if (offsetX.value == maxOffset) {
                                        onAddToQueue(
                                            track?.videoId ?: songEntity?.videoId ?: "",
                                        )
                                    }
                                    coroutineScope.launch {
                                        offsetX.animateTo(0f)
                                    }
                                },
                            )
                        }
                    }.onGloballyPositioned { coordinates ->
                        with(density) {
                            heightDp = coordinates.size.height.toDp()
                        }
                    },
        ) {
            Row(
                Modifier
                    .padding(vertical = 6.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Crossfade(isPlaying) {
                        if (selectionActive) {
                            // The artwork slot becomes the checkbox rather than the row growing a
                            // new column. Reusing the slot means rows do not reflow when the mode
                            // turns on, so a list of fifty does not jump under the thumb that just
                            // long pressed it.
                            Box(
                                modifier =
                                    Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (selected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                Color.Transparent
                                            },
                                        ).border(
                                            width = 2.dp,
                                            color =
                                                if (selected) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    subtitleColor
                                                },
                                            shape = CircleShape,
                                        ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (selected) {
                                    Icon(
                                        imageVector = SonoraIcons.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        } else if (it) {
                            NowPlayingIndicator()
                        } else if (index == null) {
                            val thumb = track?.thumbnails?.lastOrNull()?.url ?: songEntity?.thumbnails
                            AsyncImage(
                                model =
                                    ImageRequest
                                        .Builder(LocalPlatformContext.current)
                                        .data(thumb)
                                        .diskCachePolicy(CachePolicy.ENABLED)
                                        .diskCacheKey(thumb)
                                        .crossfade(true)
                                        .build(),
                                placeholder = rememberHolderPainter(),
                                error = rememberHolderPainter(),
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .clip(SonoraShapes.Media),
                            )
                        } else {
                            // Number over the artwork, not instead of it.
                            //
                            // The slot is artwork sized whatever goes in it, so a bare number left a
                            // column of empty squares down the page. Drawing the cover underneath
                            // fills the box it was always going to occupy, and the scrim is what
                            // keeps the digit readable on a pale sleeve.
                            val thumb = track?.thumbnails?.lastOrNull()?.url ?: songEntity?.thumbnails
                            AsyncImage(
                                model =
                                    ImageRequest
                                        .Builder(LocalPlatformContext.current)
                                        .data(thumb)
                                        .diskCachePolicy(CachePolicy.ENABLED)
                                        .diskCacheKey(thumb)
                                        .crossfade(true)
                                        .build(),
                                placeholder = rememberHolderPainter(),
                                error = rememberHolderPainter(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .clip(SonoraShapes.Media),
                            )
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .clip(SonoraShapes.Media)
                                        .background(Color.Black.copy(alpha = 0.45f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    color = Color.White,
                                    style = typo().titleMedium,
                                )
                            }
                        }
                    }
                }
                Column(
                    Modifier
                        .weight(1f)
                        .padding(start = 12.dp, end = 10.dp)
                        .align(Alignment.CenterVertically),
                    verticalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text(
                        text = track?.title ?: songEntity?.title ?: "",
                        style = typo().titleSmall,
                        maxLines = 1,
                        color = titleColor,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(align = Alignment.CenterVertically)
                                .titleMarquee(
                                    iterations = Int.MAX_VALUE,
                                    animationMode = MarqueeAnimationMode.Immediately,
                                ).focusable(),
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedVisibility(
                            visible =
                                if (songEntity != null || track != null) {
                                    downloadState == DownloadState.STATE_DOWNLOADED
                                } else {
                                    false
                                },
                        ) {
                            Row {
                                Icon(
                                    imageVector = SonoraIcons.DownloadForOffline,
                                    tint = contentColor,
                                    contentDescription = "",
                                    modifier = Modifier.size(16.dp).padding(2.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                        AnimatedVisibility(
                            visible =
                                songEntity?.isExplicit
                                    ?: (track?.isExplicit ?: false),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ExplicitBadge(
                                    modifier = Modifier.size(20.dp).padding(1.dp),
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                            }
                        }
                        Text(
                            text =
                                (
                                    track?.artists?.toListName()?.connectArtists()
                                        ?: songEntity?.artistName?.connectArtists()
                                ) ?: "",
                            style = typo().bodySmall,
                            maxLines = 1,
                            color = subtitleColor,
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
                }
                if (showDuration) {
                    // YouTube usually sends a display string; durationSeconds covers the rows that
                    // arrive without one.
                    // Only trust the display string when it actually looks like one. Radio and
                    // related tracks arrive with duration set to a bare count of seconds, which was
                    // shown verbatim, so a queue read 160 where every other list read 2:40.
                    val displayDuration = track?.duration?.takeIf { it.isNotBlank() && it.contains(':') }
                    val totalSeconds =
                        (track?.durationSeconds ?: songEntity?.durationSeconds)?.takeIf { it > 0 }
                            ?: track
                                ?.duration
                                ?.takeIf { it.isNotBlank() && !it.contains(':') }
                                ?.trim()
                                ?.toIntOrNull()
                                ?.takeIf { it > 0 }
                    val durationLabel =
                        displayDuration
                            ?: totalSeconds?.let { total ->
                                val mm = total / 60
                                val ss = (total % 60).toString().padStart(2, '0')
                                "$mm:$ss"
                            }
                    // Ask for the ones nobody stored a duration for.
                    //
                    // This sits inside the row on purpose: a lazy list composes only what is on
                    // screen, so scrolling past hundreds of rows asks for the handful actually
                    // seen. DurationBackfill drops repeats and caps how many run at once.
                    val backfill = koinInject<DurationBackfill>()
                    val needsDuration =
                        if (durationLabel == null) track?.videoId ?: songEntity?.videoId else null
                    LaunchedEffect(needsDuration) {
                        needsDuration?.let { backfill.request(it) }
                    }
                    if (durationLabel != null) {
                        Text(
                            text = durationLabel,
                            style = typo().bodySmall,
                            color = subtitleColor,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                    }
                }
                if (rightView != null) {
                    rightView()
                }
                if (onMoreClickListener != null) {
                    RippleIconButton(imageVector = SonoraIcons.MoreVert, fillMaxSize = false, tint = contentColor) {
                        val videoId = track?.videoId ?: songEntity?.videoId
                        videoId?.let { onMoreClickListener.invoke(it) }
                    }
                }
                AnimatedVisibility(
                    shouldShowDragHandle,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally(),
                ) {
                    Icon(
                        SonoraIcons.DragHandle,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun SuggestItems(
    track: Track,
    isPlaying: Boolean,
    onClickListener: (() -> Unit)? = null,
    onAddClickListener: (() -> Unit)? = null,
    forceDark: Boolean = LocalForceDarkText.current,
) {
    val contentColor = if (forceDark) Color.White else MaterialTheme.colorScheme.onSurface
    val subtitleColor = if (forceDark) Color(0xC4FFFFFF) else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier =
            Modifier
                .clickable {
                    if (onClickListener != null) {
                        onClickListener()
                    }
                }.animateContentSize(),
    ) {
        Row(
            Modifier
                .padding(vertical = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(40.dp)) {
                Crossfade(isPlaying) {
                    if (it) {
                        NowPlayingIndicator()
                    } else {
                        val thumb = track.thumbnails?.lastOrNull()?.url
                        AsyncImage(
                            model =
                                ImageRequest
                                    .Builder(LocalPlatformContext.current)
                                    .data(thumb)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .diskCacheKey(thumb)
                                    .crossfade(true)
                                    .build(),
                            placeholder = rememberHolderPainter(),
                            error = rememberHolderPainter(),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier =
                                Modifier
                                    .wrapContentHeight()
                                    .fillMaxWidth()
                                    .clip(SonoraShapes.Media),
                        )
                    }
                }
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 10.dp)
                    .align(Alignment.CenterVertically),
            ) {
                Text(
                    text = track.title,
                    style = typo().titleSmall,
                    maxLines = 1,
                    color = contentColor,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .titleMarquee(
                                iterations = Int.MAX_VALUE,
                                animationMode = MarqueeAnimationMode.Immediately,
                            ).focusable(),
                )
                Text(
                    text =
                        (
                            track.artists?.toListName()?.connectArtists()
                        ) ?: "",
                    style = typo().bodySmall,
                    maxLines = 1,
                    color = subtitleColor,
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
            RippleIconButton(
                imageVector = SonoraIcons.Add,
                fillMaxSize = false,
                onClick =
                    onAddClickListener ?: {
                    },
            )
        }
    }
}

@Composable
fun PlaylistFullWidthItems(
    data: PlaylistType,
    onClickListener: (() -> Unit)? = null,
    rightView: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    forceDark: Boolean = LocalForceDarkText.current,
) {
    val contentColor = if (forceDark) Color.White else MaterialTheme.colorScheme.onSurface
    val subtitleColor = if (forceDark) Color(0xC4FFFFFF) else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier =
            modifier
                .clickable {
                    onClickListener?.invoke()
                }.animateContentSize(),
    ) {
        var title = ""
        var thumb = ""
        var firstSubtitle = ""
        var secondSubtitle = ""
        var thirdRowSubtitle: String? = null

        var shouldPin = false

        firstSubtitle =
            when (data.playlistType()) {
                PlaylistType.Type.YOUTUBE_PLAYLIST -> stringResource(Res.string.playlist)
                PlaylistType.Type.RADIO -> stringResource(Res.string.radio)
                PlaylistType.Type.LOCAL -> stringResource(Res.string.playlist)
                PlaylistType.Type.ALBUM -> stringResource(Res.string.album)
                PlaylistType.Type.PODCAST -> stringResource(Res.string.podcasts)
            }
        when (data) {
            is AlbumEntity -> {
                title = data.title
                thumb = data.thumbnails ?: ""
                secondSubtitle = data.artistName?.connectArtists() ?: ""
                thirdRowSubtitle = data.year
            }

            is PlaylistEntity -> {
                title = data.title
                thumb = data.thumbnails
                secondSubtitle = data.author ?: ""
                if (data.description == "PIN") { // LIKED MUSIC
                    shouldPin = true
                }
            }

            is LocalPlaylistEntity -> {
                title = data.title
                thumb = data.thumbnail ?: ""
                secondSubtitle = stringResource(Res.string.you)
            }

            is PlaylistsResult -> {
                title = data.title
                thumb = data.thumbnails.lastOrNull()?.url ?: ""
                secondSubtitle = data.author
            }

            is AlbumsResult -> {
                title = data.title
                thumb = data.thumbnails.lastOrNull()?.url ?: ""
                secondSubtitle = data.artists.toListName().connectArtists()
                thirdRowSubtitle = data.year
            }

            is PodcastsEntity -> {
                title = data.title
                thumb = data.thumbnail ?: ""
                secondSubtitle = data.authorName
                thirdRowSubtitle = data.description
            }
        }
        Row(
            Modifier
                .padding(vertical = 10.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(48.dp)) {
                AsyncImage(
                    model =
                        ImageRequest
                            .Builder(LocalPlatformContext.current)
                            .data(thumb)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .diskCacheKey(thumb)
                            .crossfade(true)
                            .build(),
                    placeholder = rememberHolderPainter(),
                    error = rememberHolderPainter(),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(SonoraShapes.Media),
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 10.dp)
                    .align(Alignment.CenterVertically),
            ) {
                Text(
                    text = title,
                    style = typo().titleSmall,
                    maxLines = 1,
                    color = contentColor,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .titleMarquee(
                                iterations = Int.MAX_VALUE,
                                animationMode = MarqueeAnimationMode.Immediately,
                            ).focusable(),
                )

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (shouldPin) {
                        Image(
                            imageVector = SonoraIcons.PushPin,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(if (forceDark) Color.Cyan else MaterialTheme.colorScheme.primary),
                            modifier =
                                Modifier
                                    .rotate(30f)
                                    .size(16.dp),
                        )
                    }
                    Text(
                        text = "$firstSubtitle ${if (secondSubtitle.isNotEmpty()) " • $secondSubtitle" else ""}",
                        style = typo().bodySmall,
                        maxLines = 1,
                        color = subtitleColor,
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

                if (thirdRowSubtitle != null) {
                    Text(
                        text = thirdRowSubtitle,
                        style = typo().bodySmall,
                        maxLines = 1,
                        color = subtitleColor,
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
            }
            if (rightView != null) {
                rightView()
            }
        }
    }
}

@Composable
fun ArtistFullWidthItems(
    data: ArtistType,
    onClickListener: (() -> Unit)? = null,
    rightView: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    forceDark: Boolean = LocalForceDarkText.current,
) {
    val contentColor = if (forceDark) Color.White else MaterialTheme.colorScheme.onSurface
    val subtitleColor = if (forceDark) Color(0xC4FFFFFF) else MaterialTheme.colorScheme.onSurfaceVariant
    val (name: String, thumbnails: String?) =
        when (data) {
            is ArtistEntity -> Pair(data.name, data.thumbnails)
            is ArtistsResult -> Pair(data.artist, data.thumbnails.lastOrNull()?.url)
            else -> Pair("", null)
        }
    Box(
        modifier
            .clickable {
                onClickListener?.invoke()
            },
    ) {
        Row(
            Modifier
                .padding(vertical = 10.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(48.dp)) {
                AsyncImage(
                    model =
                        ImageRequest
                            .Builder(LocalPlatformContext.current)
                            .data(thumbnails)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .diskCacheKey(thumbnails)
                            .crossfade(true)
                            .build(),
                    placeholder = rememberHolderPainter(),
                    error = rememberHolderPainter(),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 10.dp)
                    .align(Alignment.CenterVertically),
            ) {
                Text(
                    text = name,
                    style = typo().titleSmall,
                    maxLines = 1,
                    color = contentColor,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .titleMarquee(
                                iterations = Int.MAX_VALUE,
                                animationMode = MarqueeAnimationMode.Immediately,
                            ).focusable(),
                )

                Text(
                    text = stringResource(Res.string.artists),
                    style = typo().bodySmall,
                    maxLines = 1,
                    color = subtitleColor,
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
            if (rightView != null) {
                rightView()
            }
        }
    }
}