package app.vanshajpahwa.sonora.ui.component

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import app.vanshajpahwa.sonora.expect.ui.PlatformBackdrop
import app.vanshajpahwa.sonora.viewModel.SharedViewModel
import kotlin.reflect.KClass

@Composable
actual fun LiquidGlassAppBottomNavigationBar(
    startDestination: Any,
    navController: NavController,
    backdrop: PlatformBackdrop,
    viewModel: SharedViewModel,
    isScrolledToTop: Boolean,
    onOpenNowPlaying: () -> Unit,
    reloadDestinationIfNeeded: (KClass<*>) -> Unit
) {
}