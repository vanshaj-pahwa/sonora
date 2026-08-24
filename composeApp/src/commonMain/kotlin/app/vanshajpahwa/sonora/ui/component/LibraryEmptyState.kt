package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.vanshajpahwa.sonora.ui.theme.typo

/**
 * What a library list shows when it has nothing to show.
 *
 * Deliberately plain: one line of text, centred, no illustration and no call-to-action button. An
 * empty Favorites list is a normal state a user passes through on day one, not an error to dress
 * up, and the pages this appears on are reached from a shelf the user just tapped, so they already
 * know where they are. The value over the previous behaviour is only that the page says something
 * instead of rendering a blank gap under a "0 tracks" header, which reads as a failure to load.
 */
@Composable
fun LibraryEmptyState(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 64.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = typo().bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
        )
    }
}
