package app.vanshajpahwa.sonora.ui.component

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import app.vanshajpahwa.sonora.expect.ui.PlatformBackdrop
import app.vanshajpahwa.sonora.ui.icon.Home
import app.vanshajpahwa.sonora.ui.icon.PlaylistPlay
import app.vanshajpahwa.sonora.ui.icon.Search
import app.vanshajpahwa.sonora.ui.icon.SonoraIcons
import app.vanshajpahwa.sonora.ui.navigation.destination.home.HomeDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.library.LibraryDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.search.SearchDestination
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import org.jetbrains.compose.resources.StringResource
import app.vanshajpahwa.sonora.resources.Res
import app.vanshajpahwa.sonora.resources.home
import app.vanshajpahwa.sonora.resources.library
import app.vanshajpahwa.sonora.resources.search
import kotlin.reflect.KClass

@Composable
expect fun LiquidGlassAppBottomNavigationBar(
    startDestination: Any = HomeDestination,
    navController: NavController,
    backdrop: PlatformBackdrop,
    viewModel: SharedViewModel,
    isScrolledToTop: Boolean = false,
    onOpenNowPlaying: () -> Unit = {},
    reloadDestinationIfNeeded: (KClass<*>) -> Unit = { _ -> },
)

sealed class BottomNavScreen(
    val ordinal: Int,
    val destination: Any,
    val title: StringResource,
    val icon: @Composable () -> Unit,
) {
    data object Home : BottomNavScreen(
        ordinal = 0,
        destination = HomeDestination,
        title = Res.string.home,
        icon = {
            Icon(
                SonoraIcons.Home,
                contentDescription = null,
            )
        },
    )

    data object Search : BottomNavScreen(
        ordinal = 1,
        destination = SearchDestination,
        title = Res.string.search,
        icon = {
            Icon(
                SonoraIcons.Search,
                contentDescription = null,
            )
        },
    )

    data object Library : BottomNavScreen(
        ordinal = 2,
        destination = LibraryDestination,
        title = Res.string.library,
        icon = {
            Icon(
                imageVector = SonoraIcons.PlaylistPlay,
                contentDescription = null,
            )
        },
    )
}