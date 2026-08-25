package app.vanshajpahwa.sonora.expect

import android.content.Context
import android.content.Intent
import org.koin.core.context.GlobalContext

/**
 * The media output panel, the same sheet the system volume control opens.
 *
 * Declared as a literal because the constant lives outside the public SDK surface, so referring to
 * it by name does not compile even though the action itself is what the platform dispatches.
 */
private const val ACTION_MEDIA_OUTPUT = "android.settings.panel.action.MEDIA_OUTPUT"

actual fun openOutputSwitcher() {
    val context = GlobalContext.get().get<Context>()
    // Wrapped: a panel is not guaranteed on every build, and failing to find one must not take
    // playback down with it.
    runCatching {
        context.startActivity(
            Intent(ACTION_MEDIA_OUTPUT).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }
}
