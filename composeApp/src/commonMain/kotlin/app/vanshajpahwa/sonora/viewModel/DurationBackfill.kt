package app.vanshajpahwa.sonora.viewModel

import app.vanshajpahwa.domain.repository.SongRepository
import app.vanshajpahwa.domain.repository.StreamRepository
import app.vanshajpahwa.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit

/**
 * Fills in the running time of songs that were stored without one.
 *
 * durationSeconds is a non-null Int, so a track that arrived from a source that did not supply a
 * duration is written as 0 and is indistinguishable from a track that is genuinely zero seconds
 * long. On a real library that left a list where some rows showed a time and others were blank,
 * which reads as broken rather than as incomplete.
 *
 * Rather than show a placeholder, the missing values are fetched once and written back, so the gap
 * closes permanently instead of being labelled forever.
 *
 * Three things keep this from becoming a request storm:
 *
 * - Only visible rows ask. The call sites are inside list item composables, and a lazy list only
 *   composes what is on screen, so scrolling past a hundred rows queues the handful actually seen.
 * - Every id is attempted at most once per process, whether it succeeded or not. A track YouTube
 *   has no duration for must not be retried every time it scrolls back into view.
 * - At most [CONCURRENCY] fetches run at a time, so a fast scroll through a screenful queues rather
 *   than opening a dozen connections at once.
 */
class DurationBackfill(
    private val streamRepository: StreamRepository,
    private val songRepository: SongRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gate = Semaphore(CONCURRENCY)
    private val mutex = Mutex()
    private val attempted = mutableSetOf<String>()

    /**
     * Ask for [videoId]'s duration if it is not already known.
     *
     * Returns immediately. Nothing is reported back: the write lands in Room and any list built on
     * a Room flow redraws itself.
     */
    fun request(videoId: String) {
        if (videoId.isBlank()) return
        scope.launch {
            val firstTime = mutex.withLock { attempted.add(videoId) }
            if (!firstTime) return@launch
            gate.withPermit {
                val seconds =
                    runCatching {
                        streamRepository
                            .getFullMetadata(videoId)
                            .lastOrNull()
                            ?.data
                            ?.durationSeconds
                    }.getOrNull()
                if (seconds != null && seconds > 0) {
                    songRepository.updateDurationSeconds(seconds, videoId)
                } else {
                    Logger.d(TAG, "No duration available for $videoId")
                }
            }
        }
    }

    companion object {
        private const val TAG = "DurationBackfill"

        /** Low on purpose. This is cosmetic work and must never compete with playback. */
        private const val CONCURRENCY = 2
    }
}
