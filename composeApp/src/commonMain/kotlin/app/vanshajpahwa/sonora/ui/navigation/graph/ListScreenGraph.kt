package app.vanshajpahwa.sonora.ui.navigation.graph

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import app.vanshajpahwa.sonora.ui.navigation.destination.list.CatalogDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.MoreLikeDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.AlbumDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.ArtistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.LocalPlaylistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.MoreAlbumsDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.PlaylistDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.list.PodcastDestination
import app.vanshajpahwa.sonora.ui.screen.library.LocalPlaylistScreen
import app.vanshajpahwa.sonora.ui.screen.other.CatalogScreen
import app.vanshajpahwa.sonora.ui.screen.other.MoreLikeScreen
import app.vanshajpahwa.sonora.ui.screen.other.AlbumScreen
import app.vanshajpahwa.sonora.ui.screen.other.ArtistScreen
import app.vanshajpahwa.sonora.ui.screen.other.MoreAlbumsScreen
import app.vanshajpahwa.sonora.ui.screen.other.PlaylistScreen
import app.vanshajpahwa.sonora.ui.screen.other.PodcastScreen
import app.vanshajpahwa.sonora.ui.theme.ForceDarkContent

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
fun NavGraphBuilder.listScreenGraph(
    innerPadding: PaddingValues,
    navController: NavController,
) {
    composable<CatalogDestination> { entry ->
        CatalogScreen(
            navController = navController,
            innerPadding = innerPadding,
            type = entry.toRoute<CatalogDestination>().type,
        )
    }
    composable<MoreLikeDestination> { entry ->
        val args = entry.toRoute<MoreLikeDestination>()
        MoreLikeScreen(
            navController = navController,
            innerPadding = innerPadding,
            seedVideoId = args.seedVideoId,
            seedName = args.seedName,
            seedChannelId = args.seedChannelId,
        )
    }
    composable<AlbumDestination> { entry ->
        val data = entry.toRoute<AlbumDestination>()
        ForceDarkContent {
            AlbumScreen(
                browseId = data.browseId,
                navController = navController,
            )
        }
    }
    composable<ArtistDestination> { entry ->
        val data = entry.toRoute<ArtistDestination>()
        ForceDarkContent {
            ArtistScreen(
                channelId = data.channelId,
                navController = navController,
            )
        }
    }
    composable<LocalPlaylistDestination> { entry ->
        val data = entry.toRoute<LocalPlaylistDestination>()
        ForceDarkContent {
            LocalPlaylistScreen(
                id = data.id,
                navController = navController,
            )
        }
    }
    composable<MoreAlbumsDestination> { entry ->
        val data = entry.toRoute<MoreAlbumsDestination>()
        MoreAlbumsScreen(
            innerPadding = innerPadding,
            navController = navController,
            type = data.type,
            id = data.id,
        )
    }
    composable<PlaylistDestination> { entry ->
        val data = entry.toRoute<PlaylistDestination>()
        ForceDarkContent {
            PlaylistScreen(
                playlistId = data.playlistId,
                isYourYouTubePlaylist = data.isYourYouTubePlaylist,
                navController = navController,
            )
        }
    }
    composable<PodcastDestination> { entry ->
        val data = entry.toRoute<PodcastDestination>()
        ForceDarkContent {
            PodcastScreen(
                podcastId = data.podcastId,
                navController = navController,
            )
        }
    }
}