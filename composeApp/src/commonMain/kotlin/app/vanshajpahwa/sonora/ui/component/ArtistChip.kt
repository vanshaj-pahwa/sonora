package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.vanshajpahwa.sonora.ui.theme.typo
import coil3.compose.AsyncImage

/**
 * An artist's name, with their picture when one is known, as a single tappable target.
 *
 * The name alone looked like ordinary text, so nothing on the page said the artist was somewhere
 * you could go. The picture is drawn only when [thumbnail] is present, which keeps this free of a
 * network request: callers read it from the local database, and any artist the user has played,
 * followed or browsed is already stored with one.
 */
@Composable
fun ArtistChip(
    name: String,
    thumbnail: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        modifier =
            modifier
                .clip(CircleShape)
                .clickable { onClick() }
                .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        thumbnail?.let { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .size(24.dp)
                        .clip(CircleShape),
            )
        }
        Text(
            text = name,
            style = typo().titleSmall,
            color = textColor,
            textAlign = TextAlign.Center,
        )
    }
}
