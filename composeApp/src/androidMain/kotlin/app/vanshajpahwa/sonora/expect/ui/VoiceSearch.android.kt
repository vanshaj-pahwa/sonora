package app.vanshajpahwa.sonora.expect.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

private class AndroidVoiceSearch(
    private val recognizer: SpeechRecognizer,
    private val hasPermission: () -> Boolean,
    private val requestPermission: () -> Unit,
) : VoiceSearchController,
    RecognitionListener {
    override var listening by mutableStateOf(false)
    override var transcript by mutableStateOf("")
    override var level by mutableFloatStateOf(0f)
    override var failed by mutableStateOf(false)

    var onResult: (String) -> Unit = {}

    init {
        recognizer.setRecognitionListener(this)
    }

    private val intent =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            .putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

    override fun start() {
        if (!hasPermission()) {
            requestPermission()
            return
        }
        transcript = ""
        failed = false
        level = 0f
        listening = true
        recognizer.startListening(intent)
    }

    override fun stop() {
        recognizer.stopListening()
    }

    fun release() {
        recognizer.destroy()
        listening = false
    }

    fun onPermissionResult(granted: Boolean) {
        if (granted) start() else failed = true
    }

    override fun onResults(results: Bundle?) {
        listening = false
        val heard = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
        if (heard.isNullOrBlank()) {
            failed = true
        } else {
            transcript = heard
            onResult(heard)
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        partialResults
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.firstOrNull()
            ?.takeIf { it.isNotBlank() }
            ?.let { transcript = it }
    }

    override fun onError(error: Int) {
        listening = false
        failed = true
    }

    // The recogniser reports roughly -2 dB in silence and 10 dB for clear speech.
    override fun onRmsChanged(rmsdB: Float) {
        level = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
    }

    override fun onEndOfSpeech() {
        level = 0f
    }

    override fun onReadyForSpeech(params: Bundle?) = Unit

    override fun onBeginningOfSpeech() = Unit

    override fun onBufferReceived(buffer: ByteArray?) = Unit

    override fun onEvent(
        eventType: Int,
        params: Bundle?,
    ) = Unit
}

@Composable
actual fun rememberVoiceSearchController(onResult: (String) -> Unit): VoiceSearchController? {
    val context = LocalContext.current
    if (!remember { SpeechRecognizer.isRecognitionAvailable(context) }) return null
    val currentOnResult by rememberUpdatedState(onResult)
    // The permission callback has to reach the controller, which is built after the launcher.
    val holder = remember { arrayOfNulls<AndroidVoiceSearch>(1) }
    val permission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            holder[0]?.onPermissionResult(granted)
        }
    val voice =
        remember {
            AndroidVoiceSearch(
                recognizer = SpeechRecognizer.createSpeechRecognizer(context),
                hasPermission = {
                    ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                        PackageManager.PERMISSION_GRANTED
                },
                requestPermission = { permission.launch(Manifest.permission.RECORD_AUDIO) },
            ).also { holder[0] = it }
        }
    voice.onResult = { currentOnResult(it) }
    DisposableEffect(voice) {
        onDispose { voice.release() }
    }
    return voice
}
