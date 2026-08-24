package app.vanshajpahwa.sonora.ui.screen.other

import androidx.compose.foundation.background
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import app.vanshajpahwa.sonora.ui.component.CenterLoadingBox
import app.vanshajpahwa.sonora.ui.component.EndOfPage
import app.vanshajpahwa.sonora.ui.component.rememberHolderPainter
import app.vanshajpahwa.sonora.ui.icon.ArrowBackIosNew
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.MoreLikeViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.more_like

/**
 * Everything behind a Home "More like X" shelf.
 *
 * The shelf shows three; this shows the whole response. Tapping a row plays it and queues the rest
 * behind it, so the list behaves like any other playlist in the app rather than as a dead end.
 */
@Composable
fun MoreLikeScreen(
    navController: NavController,
    innerPadding: PaddingValues,
    seedVideoId: String,
    seedName: String,
    seedChannelId: String? = null,
    viewModel: MoreLikeViewModel = koinViewModel(),
) {
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val nowPlayingVideoId by viewModel.nowPlayingVideoId.collectAsStateWithLifecycle()

    LaunchedEffect(seedVideoId, seedChannelId) { viewModel.load(seedVideoId, seedChannelId) }

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
                text = stringResource(Res.string.more_like, seedName),
                style = typo().titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (isLoading && tracks.isEmpty()) {
            CenterLoadingBox(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(tracks, key = { it.videoId }) { track ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.play(track.videoId, seedName) }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = track.thumbnails?.lastOrNull()?.url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            placeholder = rememberHolderPainter(),
                            error = rememberHolderPainter(),
                            modifier = Modifier.size(48.dp).clip(SonoraShapes.Media),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                style = typo().titleSmall,
                                // The row that is playing is tinted rather than badged: the list is
                                // long and a per row indicator would fight the artwork on every line.
                                color =
                                    if (track.videoId == nowPlayingVideoId) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onBackground
                                    },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = track.artists?.joinToString { it.name }.orEmpty(),
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
