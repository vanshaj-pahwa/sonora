package app.vanshajpahwa.sonora.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import app.vanshajpahwa.domain.mediaservice.handler.MediaPlayerHandler
import app.vanshajpahwa.sonora.expect.ui.PlatformBackdrop
import app.vanshajpahwa.sonora.ui.icon.QueueMusic
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.theme.typo
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.queue
import app.vanshajpahwa.sonora.resources.up_next

/**
 * Persistent "up next" strip for the player.
 *
 * Two jobs in one row, which is why it replaces the old bare queue icon rather than sitting beside
 * it: it answers "what is next" without any gesture, and the whole strip is the entry point to the
 * full queue.
 *
 * Everything here is read from state that already exists. [MediaPlayerHandler.queueData] and
 * [MediaPlayerHandler.currentSongIndex] are the same sources `QueueBottomSheet` uses, and this is
 * injected the same way that sheet injects them, so no new plumbing reaches into the player screen.
 *
 * [currentOrderIndex] rather than the raw index, because under shuffle the next track to play is the
 * next in play order, not the next in the list.
 *
 * When there is no next track (last item, repeat off) the strip does not disappear, because that
 * would take the only queue entry point with it. It falls back to a track count and stays tappable.
 *
 * [backdrop] turns the strip into a liquid-glass surface instead of the flat translucent fill. Pass
 * null (the default) to keep the fill, which is what every caller without the glass setting on gets.
 * The backdrop must belong to a source this strip is NOT inside of, or the shader feeds back on
 * itself and crashes.
 */
@Composable
fun UpNextPeekBar(
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White,
    backdrop: PlatformBackdrop? = null,
    musicServiceHandler: MediaPlayerHandler = koinInject(),
    onOpenQueue: () -> Unit,
) {
    val queueData by musicServiceHandler.queueData.collectAsStateWithLifecycle()

    // Keyed on nowPlaying, NOT currentSongIndex. currentSongIndex looks like the obvious source and
    // is a StateFlow, but MediaServiceHandlerImpl only assigns it inside moveMediaItem, so it emits
    // on queue reorder and never on track transition. nowPlaying is assigned in
    // onMediaItemTransition, so it is the handle that actually fires when the song changes.
    val nowPlayingItem by musicServiceHandler.nowPlaying.collectAsStateWithLifecycle()

    val tracks = queueData?.data?.listTracks.orEmpty()
    val nextTrack =
        remember(tracks, nowPlayingItem) {
            // currentOrderIndex() reads live player state and already handles shuffle, where the
            // next track to play is the next in play order rather than the next in the list.
            val order = musicServiceHandler.currentOrderIndex().takeIf { it > -1 } ?: 0
            tracks.getOrNull(order + 1)
        }

    // Edge cases, in the order they actually bite:
    //
    //  - Queue of 0 or 1: there is no "next" and no list worth opening, so the strip is pure noise
    //    taking 64.dp off the player. Render nothing at all.
    //  - Queue still loading: tracks is empty, caught by the same rule, so no empty strip flashes
    //    on track change.
    //  - Last track of a longer queue: no next, but the queue is still worth reaching, so the strip
    //    stays and falls back to a count. Losing the only queue entry point here would be worse
    //    than showing a slightly duller strip.
    //  - Shuffle on: handled above by currentOrderIndex rather than the raw list position.
    if (tracks.size <= 1) return

    val shape = SonoraShapes.Container
    // interactive = false: the glass press animation scales the surface, and this strip is as wide
    // as the screen. It also already answers a tap by opening the queue sheet.
    val surface =
        if (backdrop != null) {
            Modifier.liquidGlass(backdrop, shape, interactive = false)
        } else {
            Modifier.clip(shape).background(Color.White.copy(alpha = 0.07f))
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(64.dp)
                .then(surface)
                // Clip again after the glass so the ripple stays inside the rounded corners; the
                // glass draws its own clip but does not constrain the click indication.
                .clip(shape)
                .clickable(onClick = onOpenQueue)
                .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Cross faded on the id of what is coming, so the strip changes rather than blinks. It
        // matters most the moment the endless queue arrives: the strip goes from a bare count to a
        // named song, and a hard swap there reads as a glitch rather than as something arriving.
        AnimatedContent(
            targetState = nextTrack,
            transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
            contentKey = { it?.videoId },
            modifier = Modifier.weight(1f),
            label = "upNext",
        ) { next ->
        Row(verticalAlignment = Alignment.CenterVertically) {
        if (next != null) {
            AsyncImage(
                model = next.thumbnails?.lastOrNull()?.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = rememberHolderPainter(),
                error = rememberHolderPainter(),
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(SonoraShapes.Media),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.up_next),
                    style = typo().bodySmall,
                    
                    color = contentColor.copy(alpha = 0.6f),
                    maxLines = 1,
                )
                Text(
                    text = next.title,
                    style = typo().titleSmall,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = next.artists?.joinToString { it.name }.orEmpty(),
                    style = typo().bodySmall,
                    color = contentColor.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(SonoraShapes.Container)
                        .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = SonoraIcons.QueueMusic,
                    contentDescription = null,
                    tint = contentColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.queue),
                    style = typo().titleSmall,
                    color = contentColor,
                    maxLines = 1,
                )
                Text(
                    // Reached only on the last track of a queue longer than one.
                    text = "${tracks.size}",
                    style = typo().bodySmall,
                    color = contentColor.copy(alpha = 0.6f),
                    maxLines = 1,
                )
            }
        }
        }
        }
        Icon(
            imageVector = SonoraIcons.QueueMusic,
            contentDescription = stringResource(Res.string.queue),
            tint = contentColor.copy(alpha = 0.85f),
            modifier = Modifier.size(22.dp),
        )
    }
}
