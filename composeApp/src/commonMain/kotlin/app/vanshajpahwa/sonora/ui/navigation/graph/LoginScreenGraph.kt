package app.vanshajpahwa.sonora.ui.navigation.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.vanshajpahwa.sonora.ui.navigation.destination.login.DiscordLoginDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.login.LoginDestination
import app.vanshajpahwa.sonora.ui.navigation.destination.login.SpotifyLoginDestination
import app.vanshajpahwa.sonora.ui.screen.login.DiscordLoginScreen
import app.vanshajpahwa.sonora.ui.screen.login.LoginScreen
import app.vanshajpahwa.sonora.ui.screen.login.SpotifyLoginScreen

fun NavGraphBuilder.loginScreenGraph(
    innerPadding: PaddingValues,
    navController: NavController,
    hideBottomBar: () -> Unit,
    showBottomBar: () -> Unit,
) {
    composable<LoginDestination> {
        LoginScreen(
            innerPadding = innerPadding,
            navController = navController,
            hideBottomNavigation = hideBottomBar,
            showBottomNavigation = showBottomBar,
        )
    }

    composable<SpotifyLoginDestination> {
        SpotifyLoginScreen(
            innerPadding = innerPadding,
            navController = navController,
            hideBottomNavigation = hideBottomBar,
            showBottomNavigation = showBottomBar,
        )
    }

    composable<DiscordLoginDestination> {
        DiscordLoginScreen(
            innerPadding = innerPadding,
            navController = navController,
            hideBottomNavigation = hideBottomBar,
            showBottomNavigation = showBottomBar,
        )
    }

}