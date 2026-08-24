package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.border
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.vanshajpahwa.sonora.ui.theme.typo

/**
 * One option in a [SettingChoiceRow]: the stored value, the label under the tile, and the preview
 * drawn inside it.
 *
 * [preview] should draw the thing itself rather than an icon standing in for it. These previews are
 * built from the same components and geometry the feature uses, so they cannot drift out of date the
 * way a bundled screenshot would.
 */
data class SettingChoice(
    val value: String,
    val label: String,
    val preview: @Composable BoxScope.() -> Unit,
)

/**
 * A row of tappable option tiles that behaves as a radio group.
 *
 * This replaces sending the user to a select-one dialog. The options here are visual, so the choice
 * is far easier to make when the previews are on screen next to each other and one tap away, rather
 * than behind a dialog that lists them as text.
 *
 * Selection is carried by the border and the label colour, not by dimming the unselected preview:
 * the previews are the whole point of this control and are meant to stay readable.
 */
@Composable
fun SettingChoiceRow(
    choices: List<SettingChoice>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    previewHeight: Dp = 56.dp,
) {
    val shape = SonoraShapes.Container
    Row(
        // selectableGroup, plus Role.RadioButton on each tile, is what makes this announce as a radio
        // group to accessibility services instead of as a handful of unrelated buttons.
        modifier = modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        choices.forEach { choice ->
            val isSelected = choice.value == selected
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(shape)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                                },
                            shape = shape,
                        ).selectable(
                            selected = isSelected,
                            role = Role.RadioButton,
                            onClick = { onSelect(choice.value) },
                        ).padding(vertical = 12.dp, horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(previewHeight),
                    contentAlignment = Alignment.Center,
                    content = choice.preview,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = choice.label,
                    style = typo().bodySmall,
                    color =
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    // Pinned to exactly two lines so every tile is the same height and the borders
                    // match. A one-line label next to a label that wraps made one box visibly taller
                    // than the other, which read as the options themselves being different sizes.
                    //
                    // Pinned rather than measured: Row(IntrinsicSize.Max) asks a Text for its height
                    // at unconstrained width, where it reports a single line, so the tile ends up too
                    // short for the label that actually wraps.
                    minLines = 2,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
