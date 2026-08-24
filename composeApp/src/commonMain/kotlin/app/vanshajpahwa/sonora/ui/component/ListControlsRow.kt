package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.clickable
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.vanshajpahwa.sonora.ui.icon.GridView
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.icon.SwapVert
import app.vanshajpahwa.sonora.ui.icon.ViewList
import app.vanshajpahwa.sonora.ui.theme.typo

/** How a library list is ordered. [Default] is whatever order the page itself means. */
enum class ListSort {
    Default,
    Title,
    Artist,
}

/**
 * The sort and layout controls that sit between a library list's app bar and its first row.
 *
 * They live here rather than in the app bar because that bar already carries four controls; a
 * fifth and sixth would crowd it on a phone. A separate row also puts them next to the content
 * they act on, which is where the pattern reads from in Photos and Files.
 *
 * The layout button shows the layout you would *switch to*, not the one you are in. That is the
 * convention users already have from other apps, and the alternative (showing current state)
 * reads as a label rather than a button.
 */
@Composable
fun ListControlsRow(
    sort: ListSort,
    onSortChange: (ListSort) -> Unit,
    isGrid: Boolean,
    onToggleLayout: () -> Unit,
    defaultSortLabel: String,
    titleLabel: String,
    artistLabel: String,
    modifier: Modifier = Modifier,
) {
    var menuOpen by remember { mutableStateOf(false) }
    val contentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier =
                Modifier
                    .clip(SonoraShapes.Capsule)
                    .clickable { menuOpen = true }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = SonoraIcons.SwapVert,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text =
                    when (sort) {
                        ListSort.Default -> defaultSortLabel
                        ListSort.Title -> titleLabel
                        ListSort.Artist -> artistLabel
                    },
                style = typo().bodySmall,
                color = contentColor,
            )
            DropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false },
            ) {
                listOf(
                    ListSort.Default to defaultSortLabel,
                    ListSort.Title to titleLabel,
                    ListSort.Artist to artistLabel,
                ).forEach { (option, label) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = label,
                                style = typo().bodyMedium,
                                // The active option takes the accent rather than a tick, which
                                // keeps the row heights uniform and matches the now-playing cue.
                                color =
                                    if (option == sort) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                            )
                        },
                        onClick = {
                            onSortChange(option)
                            menuOpen = false
                        },
                    )
                }
            }
        }

        RippleIconButton(
            imageVector = if (isGrid) SonoraIcons.ViewList else SonoraIcons.GridView,
            fillMaxSize = false,
            tint = contentColor,
            onClick = onToggleLayout,
        )
    }
}
