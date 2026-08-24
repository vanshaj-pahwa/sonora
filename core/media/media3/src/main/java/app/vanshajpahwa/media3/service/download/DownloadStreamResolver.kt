package app.vanshajpahwa.media3.service.download

import androidx.core.net.toUri
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.dash.manifest.DashManifestParser
import androidx.media3.exoplayer.offline.DownloadManager
import app.vanshajpahwa.common.MERGING_DATA_TYPE
import app.vanshajpahwa.domain.extension.now
import app.vanshajpahwa.logger.Logger
import app.vanshajpahwa.media3.extension.isFullyCached
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.io.ByteArrayInputStream

/**
 * Resolves a download's real stream URL at request time.
 *
 * Downloads and playback each need this and each used to carry their own copy inline. Pulling it
 * out gives the resolution rules one home, so a change to how a URL is checked or refreshed does
 * not have to be made twice and cannot drift between the two paths.
 */
@androidx.media3.common.util.UnstableApi
internal fun downloadStreamResolverFactory(
    playerCache: androidx.media3.datasource.cache.SimpleCache,
    downloadCache: androidx.media3.datasource.cache.SimpleCache,
    streamRepository: app.vanshajpahwa.domain.repository.StreamRepository,
    dataStoreManager: app.vanshajpahwa.domain.manager.DataStoreManager,
): ResolvingDataSource.Factory =
            ResolvingDataSource.Factory(
                CacheDataSource
                    .Factory()
                    .setCache(playerCache)
                    .setUpstreamDataSourceFactory(
                        OkHttpDataSource.Factory(
                            OkHttpClient
                                .Builder()
                                .build(),
                        ),
                    ),
            ) { dataSpec ->
                val mediaId = dataSpec.key ?: error("No media id")
                Logger.w("Stream", mediaId)
                Logger.w("Stream", mediaId.startsWith(MERGING_DATA_TYPE.VIDEO).toString())
                // Already downloaded in full: hand the DataSpec back untouched so the enclosing
                // CacheDataSource reads straight off disk. Safe precisely because the whole track
                // is there — it never needs the upstream, which is the only thing the bare media id
                // in the URI cannot survive. Re-resolving here would re-fetch a track the user
                // already has.
                if (downloadCache.isFullyCached(mediaId, dataSpec.position)) {
                    Logger.w("Stream", "Already downloaded $mediaId")
                    return@Factory dataSpec
                }
                // Anything short of a full copy must resolve a real URL, and must never hand back
                // the incoming DataSpec: its URI is the bare media id. Skipping the bytes already on
                // disk is not this resolver's job — CacheWriter does that via cache.getCachedLength(),
                // and the CacheDataSource wrapping this resolver still serves whatever playerCache
                // holds.
                //
                // The upstream here is OkHttpDataSource (not DefaultDataSource, as on the playback
                // side), so a scheme-less URI dies in HttpUrl.parse as
                // HttpDataSourceException("Malformed URL", ERROR_CODE_FAILED_RUNTIME_CHECK) — a
                // misleading error for what is really "we could not get a stream URL". Fail loudly
                // instead, and let DownloadManager mark the download failed for the true reason.
                var dataSpecReturn: DataSpec = dataSpec
                var resolved = false
                runBlocking(Dispatchers.IO) {
                    if (mediaId.contains(MERGING_DATA_TYPE.VIDEO)) {
                        val id = mediaId.removePrefix(MERGING_DATA_TYPE.VIDEO)
                        streamRepository.getNewFormat(id).lastOrNull()?.let {
                            val videoUrl = it.videoUrl
                            if (videoUrl != null && it.expiredTime > now()) {
                                Logger.d("Stream", videoUrl)
                                Logger.w("Stream", "Video from format")
                                val is403Url = streamRepository.is403Url(videoUrl).firstOrNull() != false
                                if (!is403Url) {
                                    dataSpecReturn = dataSpec.withUri(videoUrl.toUri())
                                    resolved = true
                                    return@runBlocking
                                }
                            }
                        }
                        streamRepository
                            .getStream(
                                dataStoreManager,
                                id,
                                isDownloading = true,
                                true,
                            ).lastOrNull()
                            ?.let {
                                dataSpecReturn = dataSpec.withUri(it.toUri())
                                resolved = true
                            }
                    } else {
                        streamRepository.getNewFormat(mediaId).lastOrNull()?.let {
                            val audioUrl = it.audioUrl
                            if (audioUrl != null && it.expiredTime > now()) {
                                Logger.d("Stream", audioUrl)
                                Logger.w("Stream", "Audio from format")
                                val is403Url = streamRepository.is403Url(audioUrl).firstOrNull() != false
                                if (!is403Url) {
                                    dataSpecReturn = dataSpec.withUri(audioUrl.toUri())
                                    resolved = true
                                    return@runBlocking
                                }
                            }
                        }
                        streamRepository
                            .getStream(
                                dataStoreManager,
                                mediaId,
                                isDownloading = true,
                                isVideo = false,
                            ).lastOrNull()
                            ?.let {
                                if (it.contains("MPD")) {
                                    DashManifestParser().parse(
                                        it.toUri(),
                                        ByteArrayInputStream(it.toByteArray()),
                                    )
                                }
                                dataSpecReturn = dataSpec.withUri(it.toUri())
                                resolved = true
                            }
                    }
                }
                if (!resolved) {
                    Logger.e("Stream", "Failed to resolve download stream URL for $mediaId")
                    throw java.io.IOException("Failed to resolve stream URL for $mediaId")
                }
                return@Factory dataSpecReturn
            }
