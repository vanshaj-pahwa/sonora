package app.vanshajpahwa.media3.service.mediasourcefactory

import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.drm.DrmSessionManagerProvider
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import app.vanshajpahwa.common.MERGING_DATA_TYPE
import app.vanshajpahwa.common.VideoPlaybackOverride
import app.vanshajpahwa.domain.manager.DataStoreManager
import app.vanshajpahwa.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@UnstableApi
internal class MergingMediaSourceFactory(
    private val defaultMediaSourceFactory: DefaultMediaSourceFactory,
    dataStoreManager: DataStoreManager,
    scope: CoroutineScope,
) : MediaSource.Factory {
    /**
     * The setting, mirrored, because createMediaSource cannot afford to read it.
     *
     * ExoPlayer calls createMediaSource once per media item on the application thread, so reading
     * the flag straight from DataStore there put a blocking disk read on the main thread for every
     * item. Setting a fifty track queue meant fifty of them back to back, which is a large part of
     * the stall between tapping a song and the player appearing.
     *
     * Volatile rather than synchronised: a single boolean written by the collector and read by the
     * playback thread needs visibility, not mutual exclusion. Worst case a toggle lands one media
     * item late, and the item after it is already correct.
     */
    @Volatile
    private var watchVideoInsteadOfAudio: Boolean = false

    init {
        scope.launch {
            dataStoreManager.watchVideoInsteadOfPlayingAudio
                .map { it == DataStoreManager.Values.TRUE }
                .collect { watchVideoInsteadOfAudio = it }
        }
    }

    override fun setDrmSessionManagerProvider(drmSessionManagerProvider: DrmSessionManagerProvider): MediaSource.Factory {
        defaultMediaSourceFactory.setDrmSessionManagerProvider(drmSessionManagerProvider)
        return this
    }

    override fun setLoadErrorHandlingPolicy(loadErrorHandlingPolicy: LoadErrorHandlingPolicy): MediaSource.Factory {
        defaultMediaSourceFactory.setLoadErrorHandlingPolicy(loadErrorHandlingPolicy)
        return this
    }

    override fun getSupportedTypes(): IntArray = defaultMediaSourceFactory.supportedTypes

    override fun createMediaSource(mediaItem: MediaItem): MediaSource {
        // The player's own choice for this track wins; the setting is only the default.
        val useVideo = VideoPlaybackOverride.enabled ?: watchVideoInsteadOfAudio
        if (mediaItem.mediaMetadata.description == MERGING_DATA_TYPE.VIDEO && useVideo) {
            val videoItem =
                mediaItem
                    .buildUpon()
                    .setMediaId("${MERGING_DATA_TYPE.VIDEO}${mediaItem.mediaId}")
                    .setCustomCacheKey("${MERGING_DATA_TYPE.VIDEO}${mediaItem.mediaId}")
                    .build()
            return MergingMediaSource(
                defaultMediaSourceFactory.createMediaSource(videoItem),
                defaultMediaSourceFactory.createMediaSource(mediaItem),
            )
        } else {
            return defaultMediaSourceFactory.createMediaSource(mediaItem)
        }

//        val default = defaultMediaSourceFactory.createMediaSource(mediaItem.buildUpon().setMediaId("AUDIO-${mediaItem.mediaId}").build())
    }
}