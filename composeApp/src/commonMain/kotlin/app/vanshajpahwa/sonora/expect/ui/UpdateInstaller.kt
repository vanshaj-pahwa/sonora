package app.vanshajpahwa.sonora.expect.ui

import androidx.compose.runtime.Composable

sealed interface UpdateInstallState {
    data object Idle : UpdateInstallState

    /** [progress] runs from 0 to 1, or is null while the size is still unknown. */
    data class Downloading(
        val progress: Float?,
    ) : UpdateInstallState

    /** Waiting on the system's "install unknown apps" permission for this app. */
    data object NeedsPermission : UpdateInstallState

    data object Failed : UpdateInstallState
}

/** Downloads a release APK and hands it to the system installer. */
interface UpdateInstaller {
    val state: UpdateInstallState

    fun install(
        apkUrl: String,
        version: String,
    )
}

/** Null where installing an APK from inside the app is not possible. */
@Composable
expect fun rememberUpdateInstaller(): UpdateInstaller?
