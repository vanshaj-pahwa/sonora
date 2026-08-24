package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.koin.compose.koinInject

/**
 * The equalizer bars that mark the current track in a list.
 *
 * The bars follow the player, not the queue. They animate only while playback is actually running;
 * pausing or stopping freezes them on the frame they reached, so the row still reads as the current
 * track without claiming sound is coming out of it, and resuming picks the motion back up where it
 * left off.
 *
 * Callers decide whether to show this at all, by comparing their track against the current one.
 * Whether it moves is decided here, off the live controller state, so play and pause stay in sync
 * everywhere without each screen threading the flag through.
 */
@Composable
fun NowPlayingIndicator(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val sharedViewModel: SharedViewModel = koinInject()
    val controllerState by sharedViewModel.controllerState.collectAsStateWithLifecycle()

    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/audio_playing_animation.json").decodeToString(),
        )
    }
    // restartOnPlay = false so resuming continues the loop instead of snapping back to frame zero.
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = controllerState.isPlaying,
        restartOnPlay = false,
        iterations = Compottie.IterateForever,
    )
    Image(
        painter =
            rememberLottiePainter(
                composition = composition,
                progress = { progress },
            ),
        contentDescription = contentDescription,
        modifier = modifier,
    )
}
