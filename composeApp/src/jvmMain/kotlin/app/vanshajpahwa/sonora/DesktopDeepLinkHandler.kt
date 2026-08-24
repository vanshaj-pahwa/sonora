package app.vanshajpahwa.sonora

import com.eygraber.uri.Uri
import app.vanshajpahwa.domain.data.model.intent.GenericIntent
import app.vanshajpahwa.logger.Logger
import java.io.File

/**
 * Singleton to handle deep link URIs on Desktop.
 * Caches URI if app UI is not ready yet, delivers immediately if listener is set.
 *
 * Also provides file-based IPC for single-instance deep link forwarding:
 * when a second instance launches with a URI, it writes the URI to a temp file,
 * and the first instance reads it on restore.
 *
 * Supported URI patterns:
 * - sonora://open-app?url=<encoded_url>  (redirected from website)
 * - sonora://watch?v=VIDEO_ID            (direct scheme)
 * - sonora://playlist?list=PLAYLIST_ID   (direct scheme)
 * - sonora://channel/CHANNEL_ID          (direct scheme)
 * - sonora://album?id=ALBUM_ID           (direct scheme)
 * - https://music.youtube.com/...            (web URL passed via args)
 */
object DesktopDeepLinkHandler {
    private const val TAG = "DesktopDeepLinkHandler"

    private val pendingUriFile: File by lazy {
        File(System.getProperty("java.io.tmpdir"), "sonora_pending_deeplink.txt")
    }

    private var cached: String? = null

    var listener: ((GenericIntent) -> Unit)? = null
        set(value) {
            field = value
            if (value != null) {
                cached?.let { uri ->
                    Logger.d(TAG, "Delivering cached URI: $uri")
                    value.invoke(parseToIntent(uri))
                    cached = null
                }
            }
        }

    fun onNewUri(uri: String) {
        Logger.d(TAG, "Received URI: $uri")
        val intent = parseToIntent(uri)
        val currentListener = listener
        if (currentListener != null) {
            currentListener.invoke(intent)
            cached = null
        } else {
            Logger.d(TAG, "Listener not ready, caching URI: $uri")
            cached = uri
        }
    }

    /**
     * Write URI to a temp file so the running (first) instance can pick it up.
     * Called by the second instance before it exits.
     */
    fun writePendingUri(uri: String) {
        try {
            pendingUriFile.writeText(uri)
            Logger.d(TAG, "Wrote pending URI to file: $uri")
        } catch (e: Exception) {
            Logger.e(TAG, "Failed to write pending URI: ${e.message}")
        }
    }

    /**
     * Read and consume the pending URI file written by a second instance.
     * Called by the first instance when it receives a restore request.
     */
    fun consumePendingUri() {
        try {
            if (pendingUriFile.exists()) {
                val uri = pendingUriFile.readText().trim()
                pendingUriFile.delete()
                if (uri.isNotEmpty()) {
                    Logger.d(TAG, "Consumed pending URI from file: $uri")
                    onNewUri(uri)
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Failed to read pending URI: ${e.message}")
        }
    }

    /**
     * Converts a raw URI string into a [GenericIntent] that App.kt can process.
     *
     * Conversion rules:
     * 1. sonora://open-app?url=<encoded_url>
     *    → Extract the `url` param and use it as intent data
     *
     * 2. Everything else, including sonora://watch?v=xxx and YouTube URLs
     *    → Pass through as-is. App.kt parses the sonora:// scheme directly, so there is
     *      nothing to rewrite. This used to re-encode sonora:// links as web URLs on a
     *      redirector domain purely so one branch could parse both shapes; that branch is
     *      gone, and the rewrite would now produce a URL nothing handles.
     */
    private fun parseToIntent(uri: String): GenericIntent {
        val parsed = Uri.parse(uri)

        val actualUri = when {
            // sonora://open-app?url=<encoded_url>
            parsed.scheme == "sonora" && parsed.host == "open-app" -> {
                val urlParam = parsed.getQueryParameter("url")
                if (urlParam != null) {
                    Logger.d(TAG, "Extracted URL from open-app: $urlParam")
                    Uri.parse(urlParam)
                } else {
                    // sonora://open-app without params → just open the app, no navigation
                    Logger.d(TAG, "open-app without URL param, just opening app")
                    null
                }
            }

            // sonora://watch?v=xxx and YouTube URLs alike → App.kt handles them directly
            else -> parsed
        }

        return if (actualUri != null) {
            GenericIntent(
                action = "android.intent.action.VIEW",
                data = actualUri,
            )
        } else {
            // No data → just triggers app restore, no navigation
            GenericIntent(action = "android.intent.action.VIEW")
        }
    }
}
