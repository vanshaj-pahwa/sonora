package app.vanshajpahwa.sonora.ui.screen.other

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import app.vanshajpahwa.sonora.ui.component.CenterLoadingBox
import app.vanshajpahwa.sonora.ui.component.EndOfPage
import app.vanshajpahwa.sonora.ui.component.HomeItemContentPlaylist
import app.vanshajpahwa.sonora.ui.component.HomeItem
import app.vanshajpahwa.sonora.ui.icon.ArrowBackIosNew
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.navigation.destination.list.AlbumDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.PlaylistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.CatalogDestination
import app.vanshajpahwa.sonora.ui.theme.typo
import app.vanshajpahwa.sonora.viewModel.CatalogViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.albums
import app.vanshajpahwa.sonora.resources.error_occurred
import app.vanshajpahwa.sonora.resources.favorite
import app.vanshajpahwa.sonora.resources.recently_added
import app.vanshajpahwa.sonora.resources.music_video
import app.vanshajpahwa.sonora.resources.new_release
import app.vanshajpahwa.sonora.resources.podcasts
import app.vanshajpahwa.sonora.resources.song
import app.vanshajpahwa.sonora.resources.view_count

/**
 * The Albums and Podcasts catalog pages.
 *
 * Both render whatever shelves the browse returned, using the same [HomeItem] component the feed
 * uses, so a shelf here looks and behaves exactly as it does on Home.
 */
@Composable
fun CatalogScreen(
    navController: NavController,
    innerPadding: PaddingValues,
    type: String,
    viewModel: CatalogViewModel = koinViewModel(),
) {
    val shelves by viewModel.shelves.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val savedAlbums by viewModel.savedAlbums.collectAsStateWithLifecycle()
    val recentAlbums by viewModel.recentAlbums.collectAsStateWithLifecycle()

    val viewString = stringResource(Res.string.view_count)
    val songString = stringResource(Res.string.song)
    val newReleaseString = stringResource(Res.string.new_release)
    val musicVideoString = stringResource(Res.string.music_video)

    LaunchedEffect(type) {
        viewModel.load(type, viewString, songString, newReleaseString, musicVideoString)
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
                text =
                    if (type == CatalogDestination.PODCASTS) {
                        stringResource(Res.string.podcasts)
                    } else {
                        stringResource(Res.string.albums)
                    },
                style = typo().titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        when {
            isLoading && shelves.isEmpty() -> CenterLoadingBox(modifier = Modifier.fillMaxSize())

            shelves.isEmpty() ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = errorMessage ?: stringResource(Res.string.error_occurred),
                        style = typo().bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp),
                    )
                }

            // Both pages now keep YouTube's own shelves as horizontal rows.
            //
            // Albums used to be flattened into one grid of 24. That filled the page but threw away
            // the only structure the response carried: "Albums for you" and each genre shelf are
            // different answers to different questions, and merged into a single grid they read as
            // one undifferentiated wall. Rows with their own headings say what each group is, and
            // HomeItem already gives them a see-all, so nothing had to be invented.
            // One shelf cannot be "sections". Rows are the right shape when the response carries
            // several named groups, but Albums comes back as a single "Albums & singles" carousel
            // of 24, and rendering that as one row leaves most of the page empty. So the shape
            // follows the data: several shelves stay as rows, a lone shelf becomes a grid that
            // shows all of it.
            // The grid is the fallback for a page that would otherwise be one lonely row: a single
            // browse shelf AND no local albums yet. Once there is anything local to show, rows win,
            // because then the page has real sections rather than one carousel and empty space.
            shelves.size <= 1 && savedAlbums.isEmpty() && recentAlbums.isEmpty() -> {
                val albums = shelves.flatMap { it.contents.filterNotNull() }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                ) {
                    items(albums, key = { it.browseId ?: it.playlistId ?: it.title }) { album ->
                        Box(modifier = Modifier.padding(4.dp)) {
                            HomeItemContentPlaylist(
                                data = album,
                                onClick = {
                                    val browseId = album.browseId
                                    val playlistId = album.playlistId
                                    when {
                                        browseId != null -> navController.navigate(AlbumDestination(browseId))
                                        playlistId != null -> navController.navigate(PlaylistDestination(playlistId))
                                    }
                                },
                            )
                        }
                    }
                    item(span = { GridItemSpan(2) }) { EndOfPage() }
                }
            }

            else ->
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Local sections first. Each is skipped entirely when empty, so there is never
                    // a heading with nothing under it.
                    if (type == CatalogDestination.ALBUMS && savedAlbums.isNotEmpty()) {
                        item {
                            AlbumRowSection(
                                title = stringResource(Res.string.favorite),
                                albums = savedAlbums,
                                navController = navController,
                            )
                        }
                    }
                    if (type == CatalogDestination.ALBUMS && recentAlbums.isNotEmpty()) {
                        item {
                            AlbumRowSection(
                                title = stringResource(Res.string.recently_added),
                                albums = recentAlbums,
                                navController = navController,
                            )
                        }
                    }
                    items(shelves, key = { it.hashCode() }) { shelf ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            HomeItem(navController = navController, data = shelf)
                        }
                        Spacer(Modifier.padding(vertical = 8.dp))
                    }
                    item { EndOfPage() }
                }
        }
    }
}

/**
 * A local album shelf, drawn with the same card the browse shelves use.
 *
 * AlbumEntity already satisfies HomeContentType through PlaylistType, so it goes straight into
 * HomeItemContentPlaylist and no album-specific card had to be written.
 */
@Composable
private fun AlbumRowSection(
    title: String,
    albums: List<app.vanshajpahwa.domain.data.entities.AlbumEntity>,
    navController: NavController,
) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = title,
            style = typo().headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 15.dp, top = 8.dp, bottom = 8.dp),
        )
        LazyRow(contentPadding = PaddingValues(horizontal = 11.dp)) {
            items(albums, key = { it.browseId }) { album ->
                Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                    HomeItemContentPlaylist(
                        data = album,
                        onClick = { navController.navigate(AlbumDestination(album.browseId)) },
                    )
                }
            }
        }
    }
}
