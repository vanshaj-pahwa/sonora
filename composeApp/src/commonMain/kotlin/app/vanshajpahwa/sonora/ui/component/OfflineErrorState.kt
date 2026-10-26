package app.vanshajpahwa.sonora.ui.component

import app.vanshajpahwa.sonora.expect.ui.PlatformBackdrop
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.vanshajpahwa.sonora.ui.icon.CheckCircle
import app.vanshajpahwa.sonora.ui.icon.CloudOff
import app.vanshajpahwa.sonora.ui.icon.Download
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.theme.typo
import org.jetbrains.compose.resources.stringResource
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.home_offline_subtitle
import app.vanshajpahwa.sonora.resources.home_offline_title
import app.vanshajpahwa.sonora.resources.back_online
import app.vanshajpahwa.sonora.resources.offline_banner
import app.vanshajpahwa.sonora.resources.listen_to_downloaded
import app.vanshajpahwa.sonora.resources.retry

/**
 * Spotify-style minimal offline / error state shown on the Home tab when
 * the home feed cannot load (no network or backend failure).
 *
 * Hosts:
 * - Centered cloud-off icon
 * - Title + subtitle copy
 * - Primary action: retry the home feed
 * - Secondary action: jump to the locally downloaded library
 */
@Composable
fun OfflineErrorState(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    onRetry: () -> Unit,
    /**
     * Jump to the downloaded library, or null to omit that link.
     *
     * Null when this is rendered inline under Home's shortcut row, which already carries a Downloaded
     * tile: repeating it here made the same destination reachable twice within one screenful. It stays
     * non-null for the full screen use, where nothing else is on screen and this is the only way out.
     */
    onOpenDownloaded: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .padding(horizontal = 16.dp),
        ) {
            Icon(
                imageVector = SonoraIcons.CloudOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                modifier = Modifier.size(80.dp),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(Res.string.home_offline_title),
                style = typo().titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.home_offline_subtitle),
                style = typo().bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(28.dp))
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
                shape = SonoraShapes.Capsule,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background,
                ),
            ) {
                Text(
                    text = stringResource(Res.string.retry),
                    color = MaterialTheme.colorScheme.background,
                )
            }
            if (onOpenDownloaded != null) {
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = onOpenDownloaded,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                ) {
                    Icon(
                        imageVector = SonoraIcons.Download,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(Res.string.listen_to_downloaded),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}

/**
 * Bar reporting the connection: offline while nothing remote is reaching us, then briefly back
 * online once it returns.
 *
 * Sits in the app scaffold under the mini player and the navigation bar rather than inside a page,
 * because it is a statement about the app rather than about whichever screen happens to be open,
 * and because a page level error state interrupted content that had loaded perfectly well. This is
 * the shape Spotify and YouTube both settled on for the same reason.
 *
 * Offline uses the error container so it stays inside the theme. The theme has no success colour,
 * so back online is a fixed green with white content, which reads on both the light and dark
 * palettes. With a [backdrop] it is liquid glass like the navigation bar beside it, with the colour
 * laid over the glass as a tint so the state still reads at a glance.
 */
@Composable
fun ConnectionBanner(
    backOnline: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    backdrop: PlatformBackdrop? = null,
) {
    val container = if (backOnline) BackOnlineGreen else MaterialTheme.colorScheme.errorContainer
    val content = if (backOnline) Color.White else MaterialTheme.colorScheme.onErrorContainer
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (backdrop != null) {
                        Modifier.liquidGlass(backdrop, SonoraShapes.Capsule, interactive = false)
                    } else {
                        Modifier.clip(SonoraShapes.Capsule)
                    },
                ).background(if (backdrop != null) container.copy(alpha = 0.55f) else container, SonoraShapes.Capsule)
                .heightIn(min = 48.dp)
                .padding(start = 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (backOnline) SonoraIcons.CheckCircle else SonoraIcons.CloudOff,
            contentDescription = null,
            tint = content,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = stringResource(if (backOnline) Res.string.back_online else Res.string.offline_banner),
            style = typo().bodyMedium,
            color = content,
            modifier = Modifier.weight(1f),
        )
        // TextButton, not a filled one: retry is worth offering, not worth competing with the
        // player directly above it. heightIn holds the 48dp target even though the bar is short.
        if (!backOnline) {
            TextButton(
                onClick = onRetry,
                modifier = Modifier.heightIn(min = 48.dp),
            ) {
                Text(
                    text = stringResource(Res.string.retry),
                    style = typo().labelMedium,
                    color = content,
                )
            }
        }
    }
}

private val BackOnlineGreen = Color(0xFF1E7B3A)
