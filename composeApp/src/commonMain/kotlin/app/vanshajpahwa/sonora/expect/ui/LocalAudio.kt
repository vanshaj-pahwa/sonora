package app.vanshajpahwa.sonora.expect.ui

import androidx.compose.runtime.Composable

/**
 * An audio file that lives on the device rather than on YouTube.
 *
 * [uri] doubles as the identity of the track throughout the app. Everywhere else a track is keyed by
 * its YouTube video id, and the Media3 pipeline builds its MediaItem with `setUri(videoId)`, so a
 * local file that carries its own `content://` URI in that field flows through the existing player
 * untouched: the URI is real, and the stream resolver recognises the scheme and leaves it alone
 * instead of trying to resolve a video id that was never a video id.
 */
data class LocalAudioFile(
    val uri: String,
    val title: String,
    val artist: String,
    val album: String,
    /** Track length in milliseconds, 0 when the file did not report one. */
    val durationMs: Long,
    val albumArtUri: String?,
)

/**
 * Reads the device's audio library.
 *
 * Returns an empty list when permission has not been granted, rather than throwing, so callers can
 * treat "no permission" and "no music" the same way and let the screen ask for access.
 */
expect suspend fun scanLocalAudio(): List<LocalAudioFile>

/** True when the app may read device audio. Always true where no permission is required. */
expect fun hasLocalAudioPermission(): Boolean

/** The permission string to request, or null on platforms that need none. */
expect fun localAudioPermission(): String?

/**
 * Returns a callback that asks the user for audio access, reporting the outcome to [onResult].
 *
 * Composable because the Android side has to register an activity result launcher during
 * composition. On platforms with no permission model the callback fires [onResult] with true.
 */
@Composable
expect fun rememberLocalAudioPermissionRequest(onResult: (granted: Boolean) -> Unit): () -> Unit
