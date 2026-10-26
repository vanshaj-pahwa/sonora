package app.vanshajpahwa.sonora.expect.ui

import androidx.compose.runtime.Composable

/**
 * Live speech to text for the search bar, driven from our own listening sheet rather than the
 * system's dialog.
 *
 * [transcript] fills in as words are recognised, [level] follows the input volume from 0 to 1 for
 * the mic animation, and [failed] is set when nothing usable was heard.
 */
interface VoiceSearchController {
    val listening: Boolean
    val transcript: String
    val level: Float
    val failed: Boolean

    fun start()

    fun stop()
}

/**
 * Null when the device has no speech recogniser, so callers can leave the mic out instead of
 * offering a button that does nothing. [onResult] receives the final transcript.
 */
@Composable
expect fun rememberVoiceSearchController(onResult: (String) -> Unit): VoiceSearchController?
