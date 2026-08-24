package app.vanshajpahwa.sonora.expect.ui

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import app.vanshajpahwa.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext

private const val TAG = "LocalAudio"

/** Album art lives on a separate provider table, keyed by album id rather than track id. */
private val ALBUM_ART_BASE: Uri = Uri.parse("content://media/external/audio/albumart")

private fun context(): Context = GlobalContext.get().get()

actual fun localAudioPermission(): String? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Split media permissions landed in 33. READ_EXTERNAL_STORAGE still exists below that and is
        // declared with maxSdkVersion="32", so each range asks for exactly the one it can be granted.
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

actual fun hasLocalAudioPermission(): Boolean {
    val permission = localAudioPermission() ?: return true
    return ContextCompat.checkSelfPermission(context(), permission) == PackageManager.PERMISSION_GRANTED
}

actual suspend fun scanLocalAudio(): List<LocalAudioFile> {
    if (!hasLocalAudioPermission()) return emptyList()
    return withContext(Dispatchers.IO) {
        val out = mutableListOf<LocalAudioFile>()
        val projection =
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
            )
        // IS_MUSIC filters out ringtones, notification tones and alarms, which otherwise show up as
        // one second "tracks" and swamp a real library.
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sort = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"
        try {
            context()
                .contentResolver
                .query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    sort,
                )?.use { cursor ->
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idCol)
                        val albumId = cursor.getLong(albumIdCol)
                        out +=
                            LocalAudioFile(
                                uri =
                                    ContentUris
                                        .withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                                        .toString(),
                                title = cursor.getString(titleCol) ?: "Unknown",
                                // MediaStore writes the literal string "<unknown>" rather than null
                                // when a file carries no artist tag, and that would be shown as is.
                                artist =
                                    cursor.getString(artistCol)?.takeUnless { it == MediaStore.UNKNOWN_STRING }
                                        ?: "Unknown artist",
                                album = cursor.getString(albumCol).orEmpty(),
                                durationMs = cursor.getLong(durationCol),
                                albumArtUri =
                                    if (albumId > 0) {
                                        ContentUris.withAppendedId(ALBUM_ART_BASE, albumId).toString()
                                    } else {
                                        null
                                    },
                            )
                    }
                }
        } catch (e: Exception) {
            // A revoked permission or an OEM provider quirk should leave the screen empty, not take
            // the app down.
            Logger.e(TAG, "Local audio scan failed: ${e.message}")
        }
        out
    }
}

@Composable
actual fun rememberLocalAudioPermissionRequest(onResult: (granted: Boolean) -> Unit): () -> Unit {
    val permission = localAudioPermission()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            onResult(granted)
        }
    return {
        if (permission == null) onResult(true) else launcher.launch(permission)
    }
}
