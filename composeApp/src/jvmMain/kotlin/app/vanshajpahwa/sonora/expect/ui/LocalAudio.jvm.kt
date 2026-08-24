package app.vanshajpahwa.sonora.expect.ui

import androidx.compose.runtime.Composable

/**
 * Desktop has no scanned media library and no permission model to satisfy, so this reports "allowed,
 * but nothing found" and the Local files entry point simply never appears.
 *
 * Walking the user's Music folder would be the obvious next step here, but it is a different feature
 * with its own questions (which folders, how deep, how to watch for changes) and this file exists so
 * commonMain compiles for Desktop, not to half-answer them.
 */
actual suspend fun scanLocalAudio(): List<LocalAudioFile> = emptyList()

actual fun hasLocalAudioPermission(): Boolean = true

actual fun localAudioPermission(): String? = null

@Composable
actual fun rememberLocalAudioPermissionRequest(onResult: (granted: Boolean) -> Unit): () -> Unit = { onResult(true) }
