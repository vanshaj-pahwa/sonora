package app.vanshajpahwa.sonora.ui.screen.other

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.vanshajpahwa.sonora.expect.ui.VoiceSearchController
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.voice_search_failed
import app.vanshajpahwa.sonora.resources.voice_search_listening
import app.vanshajpahwa.sonora.resources.voice_search_retry
import app.vanshajpahwa.sonora.ui.icon.Mic
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.theme.typo
import org.jetbrains.compose.resources.stringResource

/**
 * The listening sheet behind the search bar's mic: what has been heard so far, and a mic that
 * swells with the voice level. Tapping the mic stops listening early, or starts again after a miss.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSearchSheet(
    voice: VoiceSearchController,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    LaunchedEffect(Unit) { voice.start() }
    ModalBottomSheet(
        onDismissRequest = {
            voice.stop()
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val heading =
                when {
                    voice.failed -> stringResource(Res.string.voice_search_failed)
                    voice.transcript.isNotBlank() -> voice.transcript
                    else -> stringResource(Res.string.voice_search_listening)
                }
            Text(
                text = heading,
                style = typo().headlineMedium,
                color =
                    if (voice.transcript.isNotBlank() && !voice.failed) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                textAlign = TextAlign.Center,
                minLines = 2,
                maxLines = 3,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (voice.failed) stringResource(Res.string.voice_search_retry) else " ",
                style = typo().bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(32.dp))
            val pulse by animateFloatAsState(
                targetValue = if (voice.listening) 1f + voice.level * 0.45f else 1f,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                label = "voicePulse",
            )
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
                Box(
                    modifier =
                        Modifier
                            .size(104.dp)
                            .scale(pulse)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)),
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { if (voice.listening) voice.stop() else voice.start() },
                ) {
                    Icon(
                        imageVector = SonoraIcons.Mic,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(40.dp),
                    )
                }
            }
        }
    }
}
