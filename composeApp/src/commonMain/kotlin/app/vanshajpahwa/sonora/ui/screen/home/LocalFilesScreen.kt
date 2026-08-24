package app.vanshajpahwa.sonora.ui.screen.home

import androidx.compose.foundation.background
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import app.vanshajpahwa.sonora.expect.ui.rememberLocalAudioPermissionRequest
import app.vanshajpahwa.sonora.ui.component.CenterLoadingBox
import app.vanshajpahwa.sonora.ui.component.EndOfPage
import app.vanshajpahwa.sonora.ui.component.rememberHolderPainter
import app.vanshajpahwa.sonora.ui.icon.Folder
import app.vanshajpahwa.sonora.ui.icon.ArrowBackIosNew
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.LocalFilesViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.allow_access
import app.vanshajpahwa.sonora.resources.local_files
import app.vanshajpahwa.sonora.resources.local_files_empty
import app.vanshajpahwa.sonora.resources.local_files_permission_rationale

/**
 * The device's own audio library.
 *
 * Everything here plays through the normal player and queue, so the mini player, notification and
 * Now Playing screen behave exactly as they do for streamed tracks. What it will not do is anything
 * that needs the network for these files: no lyrics, no related tracks, no like state, because there
 * is nothing on the other end to ask.
 */
@Composable
fun LocalFilesScreen(
    navController: NavController,
    innerPadding: PaddingValues,
    viewModel: LocalFilesViewModel = koinViewModel(),
) {
    val files by viewModel.files.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val hasPermission by viewModel.hasPermission.collectAsStateWithLifecycle()

    val requestPermission =
        rememberLocalAudioPermissionRequest { granted ->
            // Re-reads the permission and rescans either way: a denial has to move the screen out of
            // its loading state, not leave it spinning.
            viewModel.refresh()
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigateUp() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = SonoraIcons.ArrowBackIosNew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(Res.string.local_files),
                style = typo().titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        when {
            !hasPermission ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = SonoraIcons.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(Res.string.local_files_permission_rationale),
                        style = typo().bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = requestPermission) {
                        Text(text = stringResource(Res.string.allow_access), style = typo().bodySmall)
                    }
                }

            isLoading -> CenterLoadingBox(modifier = Modifier.fillMaxSize())

            files.isEmpty() ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(Res.string.local_files_empty),
                        style = typo().bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp),
                    )
                }

            else ->
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(files, key = { it.uri }) { file ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.play(file.uri) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(
                                model = file.albumArtUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                placeholder = rememberHolderPainter(),
                                error = rememberHolderPainter(),
                                modifier = Modifier.size(48.dp).clip(SonoraShapes.Media),
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = file.title,
                                    style = typo().titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = file.artist,
                                    style = typo().bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                    item { EndOfPage() }
                }
        }
    }
}
