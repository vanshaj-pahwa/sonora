package app.vanshajpahwa.sonora.ui.component

import androidx.compose.foundation.clickable
import app.vanshajpahwa.sonora.ui.theme.SonoraShapes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.vanshajpahwa.sonora.extension.NonLazyGrid
import app.vanshajpahwa.sonora.ui.icon.Album
import app.vanshajpahwa.sonora.ui.icon.Podcasts
import app.vanshajpahwa.sonora.ui.icon.Downloading
import app.vanshajpahwa.sonora.ui.icon.Favorite
import app.vanshajpahwa.sonora.ui.icon.Insights
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.icon.TrendingUp
import app.vanshajpahwa.sonora.ui.navigation.destination.list.CatalogDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.library.LibraryDynamicPlaylistDestination
import app.vanshajpahwa.sonora.ui.screen.library.LibraryDynamicPlaylistType
import app.vanshajpahwa.sonora.ui.theme.typo
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.albums
import app.vanshajpahwa.sonora.resources.podcasts
import app.vanshajpahwa.sonora.resources.downloaded
import app.vanshajpahwa.sonora.resources.favorite
import app.vanshajpahwa.sonora.resources.followed
import app.vanshajpahwa.sonora.resources.most_played

@Composable
fun LibraryTilingBox(navController: NavController) {
    val listItem =
        listOf(
            LibraryTilingState.Favorite,
            LibraryTilingState.Followed,
            LibraryTilingState.MostPlayed,
            LibraryTilingState.Downloaded,
            LibraryTilingState.Albums,
            LibraryTilingState.Podcasts,
        )
    NonLazyGrid(
        columns = 2,
        itemCount = listItem.size,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, end = 10.dp),
    ) { number ->
        Box(
            Modifier.padding(start = 10.dp, top = 10.dp),
        ) {
            LibraryTilingItem(
                listItem[number],
                onClick = {
                    when (listItem[number]) {
                        LibraryTilingState.Favorite -> {
                            navController.navigate(
                                LibraryDynamicPlaylistDestination(
                                    type = LibraryDynamicPlaylistType.Favorite.toStringParams(),
                                ),
                            )
                        }

                        LibraryTilingState.Followed -> {
                            navController.navigate(
                                LibraryDynamicPlaylistDestination(
                                    type = LibraryDynamicPlaylistType.Followed.toStringParams(),
                                ),
                            )
                        }

                        LibraryTilingState.MostPlayed -> {
                            navController.navigate(
                                LibraryDynamicPlaylistDestination(
                                    type = LibraryDynamicPlaylistType.MostPlayed.toStringParams(),
                                ),
                            )
                        }

                        LibraryTilingState.Albums -> {
                            navController.navigate(
                                CatalogDestination(type = CatalogDestination.ALBUMS),
                            )
                        }

                        LibraryTilingState.Podcasts -> {
                            navController.navigate(
                                CatalogDestination(type = CatalogDestination.PODCASTS),
                            )
                        }

                        LibraryTilingState.Downloaded -> {
                            navController.navigate(
                                LibraryDynamicPlaylistDestination(
                                    type = LibraryDynamicPlaylistType.Downloaded.toStringParams(),
                                ),
                            )
                        }
                    }
                },
            )
        }
    }
}

@Composable
fun LibraryTilingItem(
    state: LibraryTilingState,
    onClick: () -> Unit = {},
) {
    val title = stringResource(state.title)
    ElevatedCard(
        modifier =
            Modifier.fillMaxWidth().clickable {
                onClick.invoke()
            },
        shape = SonoraShapes.Container,
        elevation = CardDefaults.elevatedCardElevation(),
        colors =
            CardDefaults.elevatedCardColors().copy(
                containerColor = state.containerColor,
            ),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                state.icon,
                contentDescription = title,
                modifier =
                    Modifier
                        .size(50.dp)
                        .padding(10.dp),
                tint = state.iconColor,
            )
            Text(
                title,
                style = typo().titleSmall,
                color = Color.Black,
            )
        }
    }
}

data class LibraryTilingState(
    val title: StringResource,
    val containerColor: Color,
    val icon: ImageVector,
    val iconColor: Color,
) {
    companion object {
        val Favorite =
            LibraryTilingState(
                title = Res.string.favorite,
                containerColor = Color(0xffff99ae),
                icon = SonoraIcons.Favorite,
                iconColor = Color(0xffD10000),
            )
        val Followed =
            LibraryTilingState(
                title = Res.string.followed,
                containerColor = Color(0xffFFEB3B),
                icon = SonoraIcons.Insights,
                iconColor = Color.Black,
            )
        val MostPlayed =
            LibraryTilingState(
                title = Res.string.most_played,
                containerColor = Color(0xff00BCD4),
                icon = SonoraIcons.TrendingUp,
                iconColor = Color.Black,
            )
        val Downloaded =
            LibraryTilingState(
                title = Res.string.downloaded,
                containerColor = Color(0xff4CAF50),
                icon = SonoraIcons.Downloading,
                iconColor = Color.Black,
            )

        // Catalog tiles rather than library ones: the four above open lists built from what the user
        // has saved, these two open browse pages. They sit in the same grid because the question a
        // user asks here is the same one, "show me albums", and splitting them into a second grid
        // would be a distinction that only makes sense to whoever wrote the data layer.
        val Albums =
            LibraryTilingState(
                title = Res.string.albums,
                containerColor = Color(0xffFF9800),
                icon = SonoraIcons.Album,
                iconColor = Color.Black,
            )
        val Podcasts =
            LibraryTilingState(
                title = Res.string.podcasts,
                containerColor = Color(0xff9C27B0),
                icon = SonoraIcons.Podcasts,
                iconColor = Color.Black,
            )
    }
}