package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.vanshajpahwa.sonora.extension.greyScale
import app.vanshajpahwa.sonora.ui.theme.typo

@Composable
fun SettingItem(
    title: String = "Title",
    subtitle: String = "Subtitle",
    isEnable: Boolean = true,
    onClick: (() -> Unit)? = null,
    switch: Pair<Boolean, ((Boolean) -> Unit)>? = null,
    onDisable: (() -> Unit)? = null, // Callback when the item is disabled, switch off settings
    otherView: @Composable (() -> Unit)? = null,
) {
    // Key on isEnable (not Unit) so the auto-disable fires whenever the gate flips to false mid-session
    // (e.g. the user logs out while Settings is open), not only on first composition. Without this, any
    // child flag gated behind a login stays stuck ON until Settings is reopened (issues #2157, #2064).
    //
    // Deliberately above the search filter below: a setting hidden by a search query must still turn
    // its child flags off when its gate closes. Returning first would have made that fix quietly
    // conditional on the search box being empty.
    LaunchedEffect(isEnable) {
        if (!isEnable && onDisable != null) {
            onDisable.invoke()
        }
    }

    // Settings search filters here rather than at the call sites, so every setting is searchable by
    // existing rather than by being registered anywhere.
    if (!settingMatchesQuery(LocalSettingsSearchQuery.current, title, subtitle)) return

    Box(
        Modifier
            .then(
                if (onClick != null && isEnable) {
                    Modifier.clickable { onClick.invoke() }
                } else {
                    Modifier
                },
            ).then(
                if (!isEnable) {
                    Modifier.greyScale()
                } else {
                    Modifier
                },
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    // Vertical only. The gutter belongs to the screen, and SettingScreen's list
                    // already applies it, so adding another here would inset settings rows twice and
                    // leave them sitting further from the edge than every other screen's content.
                    .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = title,
                    style =
                        typo().labelMedium.let {
                            if (!isEnable) it.greyScale() else it
                        },
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                // One role for every description, and no line limit.
                //
                // This used to switch between bodySmall and bodyMedium on a per-item flag, so the
                // same kind of text rendered at two sizes down one screen depending on which the
                // author happened to pass. bodyMedium for all of them: descriptions are the text
                // people actually read to decide what a setting does, and this app is read at arm's
                // length, so the larger of the two roles is the right one.
                //
                // maxLines is gone too. It was clipping longer descriptions mid sentence, which is
                // worse than the extra row it saved: a setting explained halfway is not explained.
                Text(
                    text = subtitle,
                    style =
                        typo().bodyMedium.let {
                            if (!isEnable) it.greyScale() else it
                        },
                )

                otherView?.let {
                    Spacer(Modifier.height(16.dp))
                    it.invoke()
                }
            }
            if (switch != null) {
                Spacer(Modifier.width(10.dp))
                Switch(
                    modifier = Modifier.wrapContentWidth(),
                    checked = switch.first,
                    onCheckedChange = {
                        switch.second.invoke(it)
                    },
                    enabled = isEnable,
                )
            }
        }
    }
}