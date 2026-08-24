package app.vanshajpahwa.media3.extension

import android.content.Context
import android.os.Bundle
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.ContentMetadata
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import app.vanshajpahwa.common.MEDIA_CUSTOM_COMMAND
import app.vanshajpahwa.common.MERGING_DATA_TYPE
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.player.GenericCommandButton
import app.vanshajpahwa.domain.mediaservice.handler.RepeatState
import app.vanshajpahwa.domain.utils.connectArtists
import app.vanshajpahwa.domain.utils.toListName
import app.vanshajpahwa.media3.R

fun MediaItem?.toSongEntity(): SongEntity? =
    if (this != null) {
        SongEntity(
            videoId = this.mediaId,
            albumId = null,
            albumName = this.mediaMetadata.albumTitle.toString(),
            artistId = null,
            artistName = listOf(this.mediaMetadata.artist.toString()),
            duration = "",
            durationSeconds = 0,
            isAvailable = true,
            isExplicit = false,
            likeStatus = "INDIFFERENT",
            thumbnails = this.mediaMetadata.artworkUri.toString(),
            title = this.mediaMetadata.title.toString(),
            videoType = "",
            category = "",
            resultType = "",
            liked = false,
            totalPlayTime = 0,
            downloadState = 0,
        )
    } else {
        null
    }

@JvmName("MediaItemtoSongEntity")
@UnstableApi
fun SongEntity.toMediaItem(): MediaItem {
    val isSong = (this.thumbnails?.contains("w544") == true && this.thumbnails?.contains("h544") == true)
    return MediaItem
        .Builder()
        .setMediaId(this.videoId)
        .setUri(this.videoId)
        .setCustomCacheKey(this.videoId)
        .setMediaMetadata(
            MediaMetadata
                .Builder()
                .setTitle(this.title)
                .setArtist(this.artistName?.connectArtists())
                .setArtworkUri(this.thumbnails?.toUri())
                .setAlbumTitle(this.albumName)
                .setDescription(
                    if (isSong) MERGING_DATA_TYPE.SONG else MERGING_DATA_TYPE.VIDEO,
                ).build(),
        ).build()
}

@JvmName("TracktoMediaItem")
@UnstableApi
fun Track.toMediaItem(): MediaItem {
    var thumbUrl =
        this.thumbnails?.last()?.url
            ?: "http://i.ytimg.com/vi/${this.videoId}/maxresdefault.jpg"
    if (thumbUrl.contains("w120")) {
        thumbUrl = Regex("([wh])120").replace(thumbUrl, "$1544")
    }
    val artistName: String = this.artists.toListName().connectArtists()
    val isSong =
        (
            this.thumbnails?.last()?.height != 0 &&
                this.thumbnails?.last()?.height == this.thumbnails?.last()?.width &&
                this.thumbnails?.last()?.height != null
        ) &&
            (!thumbUrl.contains("hq720") && !thumbUrl.contains("maxresdefault"))
    return MediaItem
        .Builder()
        .setMediaId(this.videoId)
        .setUri(this.videoId)
        .setCustomCacheKey(this.videoId)
        .setMediaMetadata(
            MediaMetadata
                .Builder()
                .setTitle(this.title)
                .setArtist(this.artists.toListName().connectArtists())
                .setArtworkUri(thumbUrl.toUri())
                .setAlbumTitle(this.album?.name)
                .setDescription(
                    if (isSong) MERGING_DATA_TYPE.SONG else MERGING_DATA_TYPE.VIDEO,
                ).build(),
        ).build()
}

@androidx.annotation.OptIn(UnstableApi::class)
fun List<Track>.toMediaItems(): List<MediaItem> {
    val listMediaItem = mutableListOf<MediaItem>()
    for (item in this) {
        listMediaItem.add(item.toMediaItem())
    }
    return listMediaItem
}

@UnstableApi
fun MediaItem.isSong(): Boolean = this.mediaMetadata.description?.contains(MERGING_DATA_TYPE.SONG) == true

@UnstableApi
fun MediaItem.isVideo(): Boolean = this.mediaMetadata.description?.contains(MERGING_DATA_TYPE.VIDEO) == true

/**
 * Slot preferences decide what the compact notification shows.
 *
 * Worth being clear about what is and is not ours here. The notification is a standard MediaStyle
 * one built by DefaultMediaNotificationProvider, and from Android 13 the system draws media
 * controls in the shade itself. The colour behind it is extracted from the album art by the system,
 * so the loud tint is not something the app sets or can override, and no translucent or glass
 * treatment is possible on this surface at all.
 *
 * What the app does control is which buttons it asks for and where. Everything used to be offered
 * without a slot preference, so like, shuffle, repeat and radio all competed for the compact row
 * alongside transport, which is how five controls ended up crammed together.
 *
 * Every custom button now asks for overflow only, which leaves the three collapsed slots to
 * transport: SLOT_BACK, SLOT_CENTRAL and SLOT_FORWARD are filled by media3 from the player's own
 * commands, so previous, play/pause and next need no button of ours. Like used to claim
 * SLOT_FORWARD, and a custom button in that slot displaces the default occupant, which is why the
 * notification had a favourite and no skip. Skipping is the more used control on the surface with
 * the least room, so it wins the slot. The host still has the final say; these are preferences.
 */
fun GenericCommandButton.toCommandButton(context: Context): CommandButton =
    when (this) {
        is GenericCommandButton.Like -> {
            val liked = this.isLiked
            CommandButton
                .Builder(
                    if (liked) {
                        CommandButton.ICON_HEART_FILLED
                    } else {
                        CommandButton.ICON_HEART_UNFILLED
                    },
                    // Resource fallback for hosts (e.g. AA templated surface) that
                    // don't map the media3 icon constants
                ).setCustomIconResId(
                    if (liked) {
                        R.drawable.baseline_favorite_24
                    } else {
                        R.drawable.baseline_favorite_border_24
                    },
                ).setDisplayName(
                    if (liked) {
                        context.getString(R.string.liked)
                    } else {
                        context.getString(
                            R.string.like,
                        )
                    },
                ).setSessionCommand(SessionCommand(MEDIA_CUSTOM_COMMAND.LIKE, Bundle()))
                .setSlots(CommandButton.SLOT_OVERFLOW)
                .build()
        }
        GenericCommandButton.Radio -> {
            CommandButton
                .Builder(
                    CommandButton.ICON_RADIO,
                ).setDisplayName(context.getString(R.string.radio))
                .setSessionCommand(
                    SessionCommand(
                        MEDIA_CUSTOM_COMMAND.RADIO,
                        Bundle(),
                    ),
                ).setSlots(CommandButton.SLOT_OVERFLOW)
                .build()
        }
        is GenericCommandButton.Repeat -> {
            val repeatMode = this.repeatState
            CommandButton
                .Builder(
                    when (repeatMode) {
                        RepeatState.One -> CommandButton.ICON_REPEAT_ONE

                        RepeatState.All -> CommandButton.ICON_REPEAT_ALL

                        else -> CommandButton.ICON_REPEAT_OFF
                    },
                ).setDisplayName(
                    when (repeatMode) {
                        RepeatState.One -> context.getString(R.string.repeat_one)

                        RepeatState.All -> context.getString(R.string.repeat_all)

                        else -> context.getString(R.string.repeat_off)
                    },
                ).setSessionCommand(
                    SessionCommand(
                        MEDIA_CUSTOM_COMMAND.REPEAT,
                        Bundle(),
                    ),
                ).setSlots(CommandButton.SLOT_OVERFLOW)
                .build()
        }
        is GenericCommandButton.Shuffle -> {
            CommandButton
                .Builder(
                    if (this.isShuffled) {
                        CommandButton.ICON_SHUFFLE_ON
                    } else {
                        CommandButton.ICON_SHUFFLE_OFF
                    },
                    // Resource fallback for hosts that don't map the media3 icon
                    // constants (AA templated surface renders a gear otherwise)
                ).setCustomIconResId(R.drawable.baseline_shuffle_24)
                .setDisplayName(context.getString(R.string.shuffle))
                .setSessionCommand(
                    SessionCommand(
                        MEDIA_CUSTOM_COMMAND.SHUFFLE,
                        Bundle(),
                    ),
                ).setSlots(CommandButton.SLOT_OVERFLOW)
                .build()
        }
    }

/**
 * True only when every byte of [key] is on disk *at this moment* and [position] falls
 * inside the resource.
 *
 * KEY_CONTENT_LENGTH only says the resource length is known — CacheDataSource writes it
 * as soon as an unbounded request resolves a length, long before the download finishes.
 * The `isCached(0, total)` range check is what actually proves completeness, so both
 * halves are load-bearing; dropping either one lets a partial resource pass.
 *
 * [position] is checked because the recorded length can itself be short: an upstream that
 * closes its body early makes CacheDataSource store the truncated length as if it were the
 * whole resource. Serving that as a cache hit means the reader eventually asks for a
 * position past the end, where CacheDataSource computes a negative remainder and throws
 * ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE — another error Media3 refuses to retry.
 * Falling through to a real URL instead costs one wasted resolve at end of track.
 *
 * The answer is a snapshot, not a lease: nothing here locks the spans, and an evictor or
 * a "clear cache" tap can delete them straight afterwards. Callers must keep the window
 * they trust this for short.
 */
@UnstableApi
internal fun Cache.isFullyCached(
    key: String,
    position: Long,
): Boolean {
    val total = ContentMetadata.getContentLength(getContentMetadata(key))
    return total > 0L && position < total && isCached(key, 0L, total)
}