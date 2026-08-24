package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.vanshajpahwa.domain.extension.now
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.utils.VersionManager
import org.jetbrains.compose.resources.stringResource
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.app_name
import app.vanshajpahwa.sonora.resources.author_name
import app.vanshajpahwa.sonora.resources.version_format

@Composable
fun EndOfPage(withoutCredit: Boolean = false) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(280.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        if (!withoutCredit) {
            Text(
                "@${now().year} " + stringResource(Res.string.app_name) + " " +
                    stringResource(
                        Res.string.version_format,
                        VersionManager.getVersionName(),
                    ) + "\n" + stringResource(Res.string.author_name),
                style = typo().bodySmall,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .padding(
                            top = 20.dp,
                        ).alpha(0.8f),
            )
        }
    }
}