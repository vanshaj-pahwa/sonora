package app.vanshajpahwa.sonora.expect.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

private class AndroidUpdateInstaller(
    private val context: Context,
    private val scope: CoroutineScope,
) : UpdateInstaller {
    override var state: UpdateInstallState by mutableStateOf(UpdateInstallState.Idle)

    private var pending: File? = null

    // Cache, not external storage: no storage permission, the FileProvider already shares it, and
    // the system may reclaim it once the update is installed.
    private val directory get() = File(context.cacheDir, "updates")

    override fun install(
        apkUrl: String,
        version: String,
    ) {
        if (state is UpdateInstallState.Downloading) return
        val target = File(directory, "sonora-${version.removePrefix("v")}.apk")
        if (target.exists()) {
            prompt(target)
            return
        }
        state = UpdateInstallState.Downloading(null)
        scope.launch {
            val downloaded = runCatching { download(apkUrl, target) }.isSuccess
            if (downloaded) prompt(target) else state = UpdateInstallState.Failed
        }
    }

    /** Continues an install that was waiting on the permission, once the user is back. */
    fun onResume() {
        val file = pending ?: return
        if (state == UpdateInstallState.NeedsPermission && canInstall()) prompt(file)
    }

    private suspend fun download(
        url: String,
        target: File,
    ) = withContext(Dispatchers.IO) {
        directory.mkdirs()
        // Only the newest APK is worth keeping; older ones would otherwise pile up in the cache.
        directory.listFiles()?.forEach { it.delete() }
        val partial = File(directory, target.name + ".part")
        // GitHub answers the asset URL with a redirect to its file host, which this follows.
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.instanceFollowRedirects = true
            connection.connect()
            check(connection.responseCode in 200..299) { "HTTP ${connection.responseCode}" }
            val total = connection.contentLengthLong
            var read = 0L
            var reported = -1
            connection.inputStream.use { input ->
                partial.outputStream().use { output ->
                    val buffer = ByteArray(64 * 1024)
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        output.write(buffer, 0, count)
                        read += count
                        if (total > 0) {
                            val percent = (read * 100 / total).toInt()
                            if (percent != reported) {
                                reported = percent
                                withContext(Dispatchers.Main) {
                                    state = UpdateInstallState.Downloading(percent / 100f)
                                }
                            }
                        }
                    }
                }
            }
            check(partial.renameTo(target)) { "Could not finish the download" }
        } finally {
            connection.disconnect()
            partial.delete()
        }
    }

    private fun canInstall(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O || context.packageManager.canRequestPackageInstalls()

    private fun prompt(file: File) {
        pending = file
        if (!canInstall()) {
            state = UpdateInstallState.NeedsPermission
            context.startActivity(
                Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
            return
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.FileProvider", file)
        context.startActivity(
            Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, "application/vnd.android.package-archive")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK),
        )
        state = UpdateInstallState.Idle
    }
}

@Composable
actual fun rememberUpdateInstaller(): UpdateInstaller? {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    val installer = remember { AndroidUpdateInstaller(context, scope) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) installer.onResume()
            }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    return installer
}
